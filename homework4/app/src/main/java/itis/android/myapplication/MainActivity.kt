package itis.android.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import itis.android.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                CoroutineHomeworkScreen()
            }
        }
    }
}

private enum class MyDispatcherType {
    DEFAULT,
    IO,
    MAIN

}

private fun MyDispatcherType.asDispatcher(): CoroutineDispatcher {
    return when (this) {
        MyDispatcherType.DEFAULT -> Dispatchers.Default
        MyDispatcherType.IO -> Dispatchers.IO
        MyDispatcherType.MAIN -> Dispatchers.Main
    }
}

private class ShowToastException : Exception()
private class ShowSnackbarException : Exception()
private class ResetSettingsException : Exception()

@Composable
fun CoroutineHomeworkScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var sliderValue by remember { mutableStateOf(10f) }
    var selectedDispatcherType by remember { mutableStateOf(MyDispatcherType.DEFAULT) }
    var isSequentialStart by remember { mutableStateOf(true) }
    var isParallelStart by remember { mutableStateOf(false) }
    var isLazyStart by remember { mutableStateOf(false) }
    var isBackgroundWorkEnabled by remember { mutableStateOf(true) }
    var isRunningNow by remember { mutableStateOf(false) }
    val currentJobs = remember { mutableStateListOf<Job>() }
    var parentJob by remember { mutableStateOf<Job?>(null) }
    var wasCancelledByUser by remember { mutableStateOf(false) }
    var coroutinesCountForRestart by remember { mutableStateOf(0) }
    val minDelayMillis = 1_000L
    val maxDelayMillis = 10_000L
    val longDelayBorderMillis = 7_000L
    val errorChancePercent = 30

    fun resetSettingsToDefault() {
        sliderValue = 10f
        selectedDispatcherType = MyDispatcherType.DEFAULT
        isSequentialStart = true
        isParallelStart = false
        isLazyStart = false
    }

    fun showSimpleToast(messageResId: Int) {
        Toast.makeText(
            context,
            context.getString(messageResId),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun startCoroutinesWithCount(coroutinesCount: Int) {
        if (isRunningNow) return
        isRunningNow = true
        currentJobs.clear()
        parentJob?.cancel()
        wasCancelledByUser = false
        val howManyCoroutines = coroutinesCount
        val dispatcher = selectedDispatcherType.asDispatcher()
        var finishedCoroutinesCount = 0
        val oneBigJob = coroutineScope.launch(dispatcher) {
            val innerScope = this
            suspend fun runOneHeavyJob(jobNumber: Int) {
                try {
                    val randomDelayMillis = Random.nextLong(
                        minDelayMillis,
                        maxDelayMillis + 1L


                    )
                    delay(randomDelayMillis)

                    if (randomDelayMillis >= longDelayBorderMillis) {
                        val shouldThrowError =
                            Random.nextInt(100) < errorChancePercent


                        if (shouldThrowError) {
                            when (Random.nextInt(3)) {
                                0 -> throw ShowToastException()
                                1 -> throw ShowSnackbarException()
                                else -> throw ResetSettingsException()
                            }
                        }
                    }
                } catch (e: ShowToastException) {
                    coroutineScope.launch {
                        showSimpleToast(R.string.error_toast_message)
                    }

                } catch (e: ShowSnackbarException) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.error_snackbar_message)
                        )
                    }
                } catch (e: ResetSettingsException) {
                    coroutineScope.launch {
                        resetSettingsToDefault()
                        showSimpleToast(R.string.error_reset_message)
                    }

                }

                finishedCoroutinesCount++
            }

            if (isSequentialStart) {
                for (index in 0 until howManyCoroutines) {
                    val oneJob = innerScope.launch(
                        start = if (isLazyStart) {
                            CoroutineStart.LAZY
                        } else {
                            CoroutineStart.DEFAULT
                        }
                    ) {
                        runOneHeavyJob(index)
                    }
                    currentJobs.add(oneJob)
                    if (isLazyStart) {
                        oneJob.start()
                    }
                    oneJob.join()
                }

            } else {
                for (index in 0 until howManyCoroutines) {
                    val oneJob = innerScope.launch(
                        start = if (isLazyStart) {
                            CoroutineStart.LAZY

                        } else {
                            CoroutineStart.DEFAULT
                        }
                    ) {
                        runOneHeavyJob(index)


                    }
                    currentJobs.add(oneJob)
                }
                if (isLazyStart) {
                    currentJobs.forEach { job ->
                        if (job.isActive.not()) {
                            job.start()
                        }
                    }
                }
                currentJobs.forEach { job ->
                    job.join()
                }
            }
        }
        parentJob = oneBigJob
        coroutineScope.launch {
            oneBigJob.join()
            isRunningNow = false
            currentJobs.clear()
            parentJob = null

            if (!wasCancelledByUser) {
                val finished = finishedCoroutinesCount
                val planned = howManyCoroutines
                if (planned > 0) {
                    val resultMessage = context.getString(
                        R.string.finished_coroutines_message,
                        finished,
                        planned
                    )
                    Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                }
            } else {
                wasCancelledByUser = false
            }
        }
    }
    fun startCoroutines() {
        val howMany = sliderValue.toInt()
        startCoroutinesWithCount(howMany)
    }

    fun cancelAllRunningCoroutines(showToast: Boolean): Int {
        if (!isRunningNow) return 0

        var cancelledCount = 0
        currentJobs.forEach { job ->
            if (job.isActive) {
                job.cancel()
                cancelledCount++
            }
        }
        currentJobs.clear()
        parentJob?.cancel()
        isRunningNow = false
        wasCancelledByUser = true
        if (showToast) {
            val cancelMessage = context.getString(
                R.string.cancelled_coroutines_message,
                cancelledCount
            )
            Toast.makeText(context, cancelMessage, Toast.LENGTH_SHORT).show()
        }
        return cancelledCount
    }

    DisposableEffect(lifecycleOwner, isBackgroundWorkEnabled) {

        val observer = LifecycleEventObserver { _, event ->
            if (!isBackgroundWorkEnabled) {
                when (event) {
                    Lifecycle.Event.ON_STOP -> {
                        if (isRunningNow) {
                            val cancelledNow = cancelAllRunningCoroutines(showToast = false)
                            coroutinesCountForRestart = cancelledNow
                        }
                    }

                    Lifecycle.Event.ON_START -> {
                        if (!isRunningNow && coroutinesCountForRestart > 0) {
                            sliderValue = coroutinesCountForRestart.toFloat()
                            startCoroutinesWithCount(coroutinesCountForRestart)
                            coroutinesCountForRestart = 0
                        }
                    }

                    else -> Unit
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(

        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.screen_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(
                    id = R.string.slider_title_coroutines_count,
                    sliderValue.toInt()
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = sliderValue,
                onValueChange = { newValue ->
                    val step = 5
                    val snapped = (newValue.toInt() / step) * step
                    val fixed = snapped.coerceIn(10, 100)
                    sliderValue = fixed.toFloat()
                },
                valueRange = 10f..100f,
                steps = ((100 - 10) / 5) - 1,
                colors = SliderDefaults.colors()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.dispatcher_title),
                style = MaterialTheme.typography.bodyMedium
            )
            var isDropdownExpanded by remember { mutableStateOf(false) }
            val dispatcherNameResId = when (selectedDispatcherType) {
                MyDispatcherType.DEFAULT -> R.string.dispatcher_default
                MyDispatcherType.IO -> R.string.dispatcher_io
                MyDispatcherType.MAIN -> R.string.dispatcher_main
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDropdownExpanded = true }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = false,
                    value = stringResource(id = dispatcherNameResId),
                    onValueChange = {},
                    label = {
                        Text(text = stringResource(id = R.string.dispatcher_label))
                    }
                )
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.dispatcher_default)) },
                        onClick = {
                            selectedDispatcherType = MyDispatcherType.DEFAULT
                            isDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.dispatcher_io)) },
                        onClick = {
                            selectedDispatcherType = MyDispatcherType.IO
                            isDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.dispatcher_main)) },
                        onClick = {
                            selectedDispatcherType = MyDispatcherType.MAIN
                            isDropdownExpanded = false
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) { Text(text = stringResource(id = R.string.switch_sequential_label))
                Switch(
                    checked = isSequentialStart,
                    onCheckedChange = { newValue ->
                        if (newValue) {
                            isSequentialStart = true
                            isParallelStart = false
                        } else {
                            isSequentialStart = false
                            isParallelStart = true
                        }
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) { Text(text = stringResource(id = R.string.switch_parallel_label))
                Switch(
                    checked = isParallelStart,
                    onCheckedChange = { newValue ->
                        if (newValue) {
                            isParallelStart = true
                            isSequentialStart = false
                        } else {
                            isParallelStart = false
                            isSequentialStart = true
                        }
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) { Text(text = stringResource(id = R.string.switch_lazy_start_label))
                Switch(
                    checked = isLazyStart,
                    onCheckedChange = { newValue ->
                        isLazyStart = newValue
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) { Text(text = stringResource(id = R.string.switch_background_label))
                Switch(
                    checked = isBackgroundWorkEnabled,
                    onCheckedChange = { newValue ->
                        isBackgroundWorkEnabled = newValue
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) { if (!isRunningNow) {
                    Button(onClick = { startCoroutines() }) {
                        Text(text = stringResource(id = R.string.button_start_label))
                    }
                } else {
                    Button(onClick = { cancelAllRunningCoroutines(showToast = true) }) {
                        Text(text = stringResource(id = R.string.button_cancel_label))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    CircularProgressIndicator()
                }
            }
        }


    }
}

@Composable
private fun DropdownItem(
    titleResId: Int,
    onClick: () -> Unit
) {
    androidx.compose.material3.DropdownMenuItem(
        text = { Text(text = stringResource(id = titleResId)) },
        onClick = onClick
    )
}
@Preview(showBackground = true)
@Composable
fun CoroutineHomeworkScreenPreview() {
    MyApplicationTheme {
        CoroutineHomeworkScreen()
    }
}
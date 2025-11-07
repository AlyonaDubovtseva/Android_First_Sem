package itis.android.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import itis.android.myapplication.MainViewModel
import itis.android.myapplication.R
import itis.android.myapplication.notifications.NotificationConfig
import itis.android.myapplication.notifications.NotificationImportance

@Composable
fun NotificationSettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lastId by viewModel.lastNotificationId.collectAsState(initial = null)
    val intentData by viewModel.intentData.collectAsState(initial = null)

    var title by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var shouldExpand by rememberSaveable { mutableStateOf(false) }
    var shouldOpenMain by rememberSaveable { mutableStateOf(false) }
    var hasReply by rememberSaveable { mutableStateOf(false) }
    var selectedImportance by rememberSaveable { mutableStateOf(NotificationImportance.MEDIUM) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var showTitleError by rememberSaveable { mutableStateOf(false) }

    val messageIsPresent = message.isNotBlank()

    LaunchedEffect(messageIsPresent) {
        if (!messageIsPresent && shouldExpand) {
            shouldExpand = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.screen_notifications),
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                if (showTitleError && it.isNotBlank()) {
                    showTitleError = false
                }
            },
            label = { Text(text = stringResource(id = R.string.notification_title_hint)) },
            singleLine = true,
            isError = showTitleError,
            supportingText = {
                if (showTitleError) {
                    Text(text = stringResource(id = R.string.notification_title_error))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text(text = stringResource(id = R.string.notification_message_hint)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onDone = { /* no-op */ })
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(id = R.string.expand_switch_label))
                if (!messageIsPresent) {
                    Text(
                        text = stringResource(id = R.string.expand_switch_disabled_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = shouldExpand,
                onCheckedChange = { shouldExpand = it },
                enabled = messageIsPresent
            )
        }

        Box {
            OutlinedButton(
                onClick = { dropdownExpanded = !dropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.priority_label) + ": " +
                            stringResource(id = selectedImportance.labelRes),
                    modifier = Modifier.weight(1f)
                )
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                NotificationImportance.entries.forEach { importance ->
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = importance.labelRes)) },
                        onClick = {
                            selectedImportance = importance
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = stringResource(id = R.string.open_main_switch_label))
            Switch(
                checked = shouldOpenMain,
                onCheckedChange = { shouldOpenMain = it }
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = stringResource(id = R.string.reply_switch_label))
            Switch(
                checked = hasReply,
                onCheckedChange = { hasReply = it }
            )
        }

        Button(
            onClick = {
                val trimmedTitle = title.trim()
                if (trimmedTitle.isEmpty()) {
                    showTitleError = true
                    return@Button
                }
                val trimmedMessage = message.trim().takeUnless { it.isEmpty() }
                val config = NotificationConfig(
                    title = trimmedTitle,
                    message = trimmedMessage,
                    importance = selectedImportance,
                    shouldExpand = shouldExpand && trimmedMessage != null,
                    shouldOpenMain = shouldOpenMain,
                    hasReply = hasReply
                )
                val id = viewModel.createNotification(context, config)
                Toast.makeText(
                    context,
                    context.getString(R.string.toast_notification_created, id),
                    Toast.LENGTH_SHORT
                ).show()
                title = ""
                message = ""
                shouldExpand = false
                showTitleError = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.create_notification))
        }

        Text(
            text = lastId?.let { stringResource(id = R.string.last_notification_id, it) }
                ?: stringResource(id = R.string.last_notification_id_empty),
            style = MaterialTheme.typography.bodyMedium
        )

        intentData?.let { data ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.intent_title, data.title),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    data.message?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(id = R.string.intent_message, it))
                    }
                }
            }
        }
    }
}


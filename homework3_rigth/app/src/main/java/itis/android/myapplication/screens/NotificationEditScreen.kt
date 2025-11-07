package itis.android.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import itis.android.myapplication.MainViewModel
import itis.android.myapplication.R

@Composable
fun NotificationEditScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notifications by viewModel.notifications.collectAsState()

    var idInput by rememberSaveable { mutableStateOf("") }
    var textInput by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.screen_editor),
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = stringResource(id = R.string.active_notifications_count, notifications.size),
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = idInput,
            onValueChange = { idInput = it },
            label = { Text(text = stringResource(id = R.string.notification_id_hint)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            )
        )

        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text(text = stringResource(id = R.string.notification_new_text_hint)) },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val id = idInput.trim().toIntOrNull()
                if (id == null) {
                    Toast.makeText(
                        context,
                        R.string.toast_invalid_id,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }
                val updated = viewModel.updateNotification(context, id, textInput)
                if (updated) {
                    Toast.makeText(
                        context,
                        R.string.toast_notification_updated,
                        Toast.LENGTH_SHORT
                    ).show()
                    textInput = ""
                } else {
                    Toast.makeText(
                        context,
                        R.string.toast_notification_not_found,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.update_notification))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val cleared = viewModel.clearNotifications(context)
                val messageRes = if (cleared) {
                    R.string.toast_notifications_cleared
                } else {
                    R.string.toast_no_notifications
                }
                Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.clear_notifications))
        }
    }
}


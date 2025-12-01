package itis.android.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.dp
import itis.android.myapplication.MainViewModel
import itis.android.myapplication.R

@Composable
fun MessagesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()

    var messageInput by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.screen_messages),
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = messageInput,
            onValueChange = { messageInput = it },
            label = { Text(text = stringResource(id = R.string.messages_hint)) },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val trimmed = messageInput.trim()
                if (trimmed.isEmpty()) {
                    Toast.makeText(context, R.string.toast_message_empty, Toast.LENGTH_SHORT).show()
                    return@Button
                }
                viewModel.addUserMessage(trimmed)
                messageInput = ""
                Toast.makeText(context, R.string.toast_message_saved, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.add_message))
        }

        Divider()

        if (messages.isEmpty()) {
            Text(
                text = stringResource(id = R.string.messages_empty_placeholder),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { item ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = item, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}


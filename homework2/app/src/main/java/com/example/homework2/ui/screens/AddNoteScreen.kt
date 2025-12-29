package com.example.homework2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.homework2.R

@Composable
fun AddNoteScreen(
	onSave: (title: String, content: String) -> Unit,
	onCancel: () -> Unit,
) {
	var title by remember { mutableStateOf("") }
	var content by remember { mutableStateOf("") }
	var titleError by remember { mutableStateOf(false) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
			.padding(16.dp)
			.statusBarsPadding()
			.navigationBarsPadding()
			.imePadding(),
		verticalArrangement = Arrangement.SpaceBetween
	) {
		Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			OutlinedTextField(
				value = title,
				onValueChange = {
					title = it
					if (titleError) titleError = false
				},
				label = { Text(stringResource(id = R.string.title)) },
				isError = titleError,
				modifier = Modifier.fillMaxWidth(),
				supportingText = { if (titleError) Text(stringResource(id = R.string.title_empty)) }
			)
			OutlinedTextField(
				value = content,
				onValueChange = { content = it },
				label = { Text(stringResource(id = R.string.content)) },
				modifier = Modifier
					.fillMaxWidth()
					.height(160.dp)
			)
		}
		Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
			Button(modifier = Modifier.fillMaxWidth(), onClick = {
				if (title.isBlank()) {
					titleError = true
				} else {
					onSave(title.trim(), content.trim())
				}
			}) { Text(stringResource(id = R.string.save)) }
			Button(modifier = Modifier.fillMaxWidth(), onClick = onCancel) { Text(stringResource(id = R.string.cancel)) }
		}
	}
}

package com.example.homework2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.homework2.ColorChoice
import com.example.homework2.Note
import com.example.homework2.R

@Composable
fun HomeScreen(
	email: String,
	notes: List<Note>,
	colorChoice: ColorChoice,
	onColorChange: (ColorChoice) -> Unit,
	onAddNote: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
			.statusBarsPadding()
			.navigationBarsPadding()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text(text = stringResource(id = R.string.email) + ": " + email, style = MaterialTheme.typography.titleMedium)

		ColorDropdown(current = colorChoice, onChange = onColorChange)

		Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			Text(text = stringResource(id = R.string.notes), style = MaterialTheme.typography.titleMedium)
			Button(onClick = onAddNote) { Text(stringResource(id = R.string.add_note)) }
		}

		LazyColumn(modifier = Modifier.fillMaxSize()) {
			if (notes.isEmpty()) {
				item { Text(stringResource(id = R.string.no_notes)) }
			} else {
				items(notes) { note ->
					OutlinedCard(modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 6.dp)) {
						Column(modifier = Modifier.padding(12.dp)) {
							Text(text = note.title, style = MaterialTheme.typography.titleMedium)
							Spacer(modifier = Modifier.height(4.dp))
							if (note.content.isNotEmpty()) {
								Text(text = note.content)
							}
						}
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorDropdown(current: ColorChoice, onChange: (ColorChoice) -> Unit) {
	val options = listOf(ColorChoice.Red, ColorChoice.Green, ColorChoice.Blue)
	val expanded = remember { mutableStateOf(false) }
	ExposedDropdownMenuBox(
		expanded = expanded.value,
		onExpandedChange = { expanded.value = !expanded.value }
	) {
		OutlinedTextField(
			value = when (current) {
				ColorChoice.Red -> stringResource(id = R.string.color_red)
				ColorChoice.Green -> stringResource(id = R.string.color_green)
				ColorChoice.Blue -> stringResource(id = R.string.color_blue)
			},
			onValueChange = {},
			readOnly = true,
			label = { Text(stringResource(id = R.string.choose_color)) },
			trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
			modifier = Modifier
				.menuAnchor()
				.fillMaxWidth()
		)
		ExposedDropdownMenu(
			expanded = expanded.value,
			onDismissRequest = { expanded.value = false }
		) {
			options.forEach { choice ->
				DropdownMenuItem(
					text = {
						Text(
							when (choice) {
								ColorChoice.Red -> stringResource(id = R.string.color_red)
								ColorChoice.Green -> stringResource(id = R.string.color_green)
								ColorChoice.Blue -> stringResource(id = R.string.color_blue)
							}
						)
					},
					onClick = {
						onChange(choice)
						expanded.value = false
					}
				)
			}
		}
	}
}

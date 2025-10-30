package com.example.homework2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.homework2.ColorChoice

private fun backgroundFor(choice: ColorChoice): Color = when (choice) {
	ColorChoice.Red -> Color(0xFFFFEBEE)
	ColorChoice.Green -> Color(0xFFE8F5E9)
	ColorChoice.Blue -> Color(0xFFE3F2FD)
}
private fun primaryFor(choice: ColorChoice): Color = when (choice) {
	ColorChoice.Red -> Color(0xFFB71C1C)
	ColorChoice.Green -> Color(0xFF1B5E20)
	ColorChoice.Blue -> Color(0xFF0D47A1)
}

@Composable
fun Homework2Theme(
	colorChoice: ColorChoice,
	useDarkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit
) {
	val scheme = if (useDarkTheme) {
		darkColorScheme(
			primary = primaryFor(colorChoice),
			background = backgroundFor(colorChoice)
		)
	} else {
		lightColorScheme(
			primary = primaryFor(colorChoice),
			background = backgroundFor(colorChoice)
		)
	}
	MaterialTheme(
		colorScheme = scheme,
		typography = Typography,
		content = content
	)
}
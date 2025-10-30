package com.example.homework2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.homework2.EmailError
import com.example.homework2.LoginState
import com.example.homework2.PasswordError
import com.example.homework2.R

@Composable
fun LoginScreen(
	state: LoginState,
	onEmailChange: (String) -> Unit,
	onPasswordChange: (String) -> Unit,
	onTogglePasswordVisibility: () -> Unit,
	onSubmit: () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
			.padding(horizontal = 16.dp)
			.statusBarsPadding()
			.navigationBarsPadding()
			.imePadding(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.SpaceBetween
	) {
		Column(modifier = Modifier.fillMaxWidth()) {
			Text(text = stringResource(id = R.string.login))
			Spacer(modifier = Modifier.height(16.dp))
			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(),
				value = state.email,
				onValueChange = { new -> onEmailChange(new) },
				label = { Text(text = stringResource(id = R.string.email)) },
				isError = state.emailError != null,
				supportingText = {
					when (state.emailError) {
						EmailError.Empty -> Text(stringResource(id = R.string.empty_email))
						EmailError.Invalid -> Text(stringResource(id = R.string.invalid_email))
						null -> {}
					}
				}
			)
			Spacer(modifier = Modifier.height(12.dp))
			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(),
				value = state.password,
				onValueChange = { new -> onPasswordChange(new) },
				label = { Text(text = stringResource(id = R.string.password)) },
				isError = state.passwordError != null,
				visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
				trailingIcon = {
					IconButton(onClick = onTogglePasswordVisibility) {
						Icon(
							imageVector = if (state.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
							contentDescription = null
						)
					}
				},
				supportingText = {
					when (state.passwordError) {
						PasswordError.Empty -> Text(stringResource(id = R.string.empty_password))
						PasswordError.TooShort -> Text(stringResource(id = R.string.short_password))
						null -> {}
					}
				},
				keyboardOptions = KeyboardOptions(autoCorrectEnabled = false)
			)
		}
		Button(
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 16.dp),
			onClick = onSubmit
		) {
			Text(text = stringResource(id = R.string.continue_btn))
		}
	}
}

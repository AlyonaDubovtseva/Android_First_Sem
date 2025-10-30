package com.example.homework2

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
	var loginState by mutableStateOf(LoginState())
		private set

	val notes = mutableStateListOf<Note>()

	var themeChoice by mutableStateOf(ColorChoice.Red)
		private set

	fun onEmailChange(newEmail: String) {
		loginState = loginState.copy(email = newEmail, emailError = null)
	}

	fun onPasswordChange(newPassword: String) {
		loginState = loginState.copy(password = newPassword, passwordError = null)
	}

	fun onTogglePasswordVisibility() {
		loginState = loginState.copy(isPasswordVisible = !loginState.isPasswordVisible)
	}

	fun validateLogin(): Boolean {
		var valid = true
		if (loginState.email.isBlank()) {
			loginState = loginState.copy(emailError = EmailError.Empty)
			valid = false
		} else if (!Patterns.EMAIL_ADDRESS.matcher(loginState.email).matches()) {
			loginState = loginState.copy(emailError = EmailError.Invalid)
			valid = false
		}
		if (loginState.password.isBlank()) {
			loginState = loginState.copy(passwordError = PasswordError.Empty)
			valid = false
		} else if (loginState.password.length < 8) {
			loginState = loginState.copy(passwordError = PasswordError.TooShort)
			valid = false
		}
		return valid
	}

	fun addNote(title: String, content: String) {
		notes.add(Note(title = title, content = content))
	}

	fun onThemeChange(choice: ColorChoice) {
		themeChoice = choice
	}
}

data class LoginState(
	val email: String = "",
	val password: String = "",
	val isPasswordVisible: Boolean = false,
	val emailError: EmailError? = null,
	val passwordError: PasswordError? = null,
)

data class Note(
	val title: String,
	val content: String,
)

enum class EmailError { Empty, Invalid }
enum class PasswordError { Empty, TooShort }

enum class ColorChoice { Red, Green, Blue }

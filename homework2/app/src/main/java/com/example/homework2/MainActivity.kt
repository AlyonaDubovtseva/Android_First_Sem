package com.example.homework2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homework2.ui.screens.AddNoteScreen
import com.example.homework2.ui.screens.HomeScreen
import com.example.homework2.ui.screens.LoginScreen
import com.example.homework2.ui.theme.Homework2Theme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			val appViewModel: MainViewModel = viewModel()
			Homework2Theme(colorChoice = appViewModel.themeChoice) {
				val navController = rememberNavController()
				NavHost(navController = navController, startDestination = Routes.Login.route) {
					composable(Routes.Login.route) {
						LoginScreen(
							state = appViewModel.loginState,
							onEmailChange = appViewModel::onEmailChange,
							onPasswordChange = appViewModel::onPasswordChange,
							onTogglePasswordVisibility = appViewModel::onTogglePasswordVisibility,
							onSubmit = {
								if (appViewModel.validateLogin()) {
									navController.navigate(Routes.Home.route)
								}
							}
						)
					}
					composable(Routes.Home.route) {
						HomeScreen(
							email = appViewModel.loginState.email,
							notes = appViewModel.notes,
							colorChoice = appViewModel.themeChoice,
							onColorChange = appViewModel::onThemeChange,
							onAddNote = { navController.navigate(Routes.AddNote.route) }
						)
					}
					composable(Routes.AddNote.route) {
						AddNoteScreen(
							onSave = { title, content ->
								if (title.isNotBlank()) {
									appViewModel.addNote(title.trim(), content.trim())
									navController.popBackStack()
								}
							},
							onCancel = { navController.popBackStack() }
						)
					}
				}
			}
		}
	}
}

sealed class Routes(val route: String) {
	data object Login : Routes("login")
	data object Home : Routes("home")
	data object AddNote : Routes("add_note")
}

package com.example.roomapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.roomapp.data.datastore.AppDataStore
import com.example.roomapp.data.database.AppDatabase
import com.example.roomapp.data.repository.CatRepository
import com.example.roomapp.data.repository.UserRepository
import com.example.roomapp.viewmodel.AuthViewModel
import com.example.roomapp.viewmodel.CatsViewModel
import com.example.roomapp.viewmodel.ProfileViewModel
import com.example.roomapp.viewmodel.RestoreAccountViewModel
import com.example.roomapp.ui.screens.LoginScreen
import com.example.roomapp.ui.screens.RegisterScreen
import com.example.roomapp.ui.screens.AddCatScreen
import com.example.roomapp.ui.screens.CatsListScreen
import com.example.roomapp.ui.screens.ProfileScreen
import com.example.roomapp.ui.screens.RestoreAccountScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    val context = LocalContext.current

    val database = remember { AppDatabase.getDatabase(context) }
    val userRepository = remember { UserRepository(database.userDao()) }
    val catRepository = remember { CatRepository(database.catDao()) }
    val dataStore = remember { AppDataStore(context) }

    LaunchedEffect(Unit) {
        userRepository.cleanupDeletedUsers()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AuthViewModel(userRepository, dataStore) as T
                    }
                }
            )

            LoginScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Screen.Register.route) {
            val viewModel: AuthViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AuthViewModel(userRepository, dataStore) as T
                    }
                }
            )

            RegisterScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Screen.CatsList.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0L

            val viewModel: CatsViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return CatsViewModel(catRepository, dataStore, userId) as T
                    }
                }
            )

            CatsListScreen(
                navController = navController,
                viewModel = viewModel,
                userId = userId
            )
        }

        composable(Screen.AddCat.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0L

            val viewModel: CatsViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return CatsViewModel(catRepository, dataStore, userId) as T
                    }
                }
            )

            AddCatScreen(
                navController = navController,
                viewModel = viewModel,
                userId = userId
            )
        }

        composable(Screen.Profile.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0L

            val viewModel: ProfileViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ProfileViewModel(userRepository, dataStore, userId) as T
                    }
                }
            )

            ProfileScreen(
                navController = navController,
                viewModel = viewModel,
                userId = userId
            )
        }

        composable(Screen.RestoreAccount.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0L

            val viewModel: RestoreAccountViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return RestoreAccountViewModel(userRepository, dataStore, userId) as T
                    }
                }
            )

            RestoreAccountScreen(
                navController = navController,
                viewModel = viewModel,
                userId = userId
            )
        }
    }
}
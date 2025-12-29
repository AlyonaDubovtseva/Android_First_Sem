package com.example.roomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.example.roomapp.ui.theme.AppTheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CatLoversApp()
                }
            }
        }
    }
}

@Composable
fun CatLoversApp() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val database = remember { AppDatabase.getDatabase(context) }
    val userRepository = remember { UserRepository(database.userDao()) }
    val catRepository = remember { CatRepository(database.catDao()) }
    val dataStore = remember { AppDataStore(context) }

    val isLoggedIn by dataStore.isLoggedIn.collectAsState(initial = false)
    val userId by dataStore.userId.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        userRepository.cleanupDeletedUsers()

        val savedUserId = dataStore.userId.first()
        val savedIsLoggedIn = dataStore.isLoggedIn.first()
        
        if (savedIsLoggedIn && savedUserId != null && savedUserId > 0) {
            val user = userRepository.getActiveUserById(savedUserId)
            if (user != null) {
                navController.navigate("cats_list/$savedUserId") {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                dataStore.clearUserData()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
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

        composable("register") {
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

        composable("cats_list/{userId}") { backStackEntry ->
            val userIdFromArgs = backStackEntry.arguments?.getString("userId")?.toLongOrNull()
            val userIdFromStore by dataStore.userId.collectAsState(initial = null)
            val userId = userIdFromArgs ?: userIdFromStore ?: 0L

            if (userId == 0L) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
                return@composable
            }

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

        composable("add_cat/{userId}") { backStackEntry ->
            val userIdFromArgs = backStackEntry.arguments?.getString("userId")?.toLongOrNull()
            val userIdFromStore by dataStore.userId.collectAsState(initial = null)
            val userId = userIdFromArgs ?: userIdFromStore ?: 0L

            if (userId == 0L) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
                return@composable
            }

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

        composable("profile/{userId}") { backStackEntry ->
            val userIdFromArgs = backStackEntry.arguments?.getString("userId")?.toLongOrNull()
            val userIdFromStore by dataStore.userId.collectAsState(initial = null)
            val userId = userIdFromArgs ?: userIdFromStore ?: 0L

            if (userId == 0L) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
                return@composable
            }

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

        composable("restore_account/{userId}") { backStackEntry ->
            val userIdFromArgs = backStackEntry.arguments?.getString("userId")?.toLongOrNull()
            val userIdFromStore by dataStore.userId.collectAsState(initial = null)
            val userId = userIdFromArgs ?: userIdFromStore ?: 0L

            if (userId == 0L) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
                return@composable
            }

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
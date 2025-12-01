package itis.android.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.core.content.ContextCompat
import itis.android.myapplication.notifications.NotificationHelper
import itis.android.myapplication.screens.NotificationEditScreen
import itis.android.myapplication.screens.NotificationSettingsScreen
import itis.android.myapplication.screens.MessagesScreen
import itis.android.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        viewModel.handleIncomingNotificationIntent(intent)
        clearIntentExtras()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val items = BottomNavItem.entries.toList()

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            items.forEach { item ->
                                NavigationBarItem(
                                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            launchSingleTop = true
                                            restoreState = true
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                        }
                                    },
                                    icon = { Icon(imageVector = item.icon, contentDescription = null) },
                                    label = { Text(text = stringResource(id = item.labelRes)) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Settings.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(BottomNavItem.Settings.route) {
                            NotificationSettingsScreen(viewModel = viewModel)
                        }
                        composable(BottomNavItem.Editor.route) {
                            NotificationEditScreen(viewModel = viewModel)
                        }
                        composable(BottomNavItem.Messages.route) {
                            MessagesScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleIncomingNotificationIntent(intent)
        clearIntentExtras()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val permission = Manifest.permission.POST_NOTIFICATIONS
        val isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun clearIntentExtras() {
        intent?.removeExtra(NotificationHelper.EXTRA_TITLE)
        intent?.removeExtra(NotificationHelper.EXTRA_MESSAGE)
    }

    private enum class BottomNavItem(
        val route: String,
        val labelRes: Int,
        val icon: ImageVector
    ) {
        Settings("notifications", R.string.screen_notifications, Icons.Filled.Notifications),
        Editor("editor", R.string.screen_editor, Icons.Filled.Edit),
        Messages("messages", R.string.screen_messages, Icons.Filled.Message)
    }
}
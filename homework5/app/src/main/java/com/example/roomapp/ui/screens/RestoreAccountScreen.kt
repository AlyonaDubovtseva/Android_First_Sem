package com.example.roomapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.roomapp.R
import com.example.roomapp.viewmodel.RestoreAccountViewModel
import com.example.roomapp.viewmodel.UserState
import com.example.roomapp.viewmodel.RestoreState
import com.example.roomapp.viewmodel.DeleteState
import com.example.roomapp.ui.navigation.Screen
import com.example.roomapp.ui.theme.PastelPink
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestoreAccountScreen(
    navController: NavController,
    viewModel: RestoreAccountViewModel,
    userId: Long
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val restoreState by viewModel.restoreState.collectAsStateWithLifecycle()
    val deleteState by viewModel.deleteState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    
    LaunchedEffect(restoreState) {
        when (restoreState) {
            is RestoreState.Success -> {
                val message = context.getString(R.string.account_restored)
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                    navController.navigate(Screen.CatsList.createRoute(userId)) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is RestoreState.Error -> {
                val message = context.getString(R.string.error_restore)
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                    viewModel.resetRestoreState()
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is DeleteState.Success -> {
                val message = context.getString(R.string.account_deleted_permanently)
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is DeleteState.Error -> {
                val message = context.getString(R.string.error_delete)
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                    viewModel.resetDeleteState()
                }
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.restore_account_title),
                        color = PastelPink
                    )
                }
            )
        }
    ) { paddingValues ->
        when (userState) {
            is UserState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PastelPink)
                }
            }

            is UserState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = stringResource(R.string.error),
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = (userState as UserState.Error).message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { navController.navigate(Screen.Login.route) }
                    ) {
                        Text(stringResource(R.string.to_home))
                    }
                }
            }

            is UserState.Success -> {
                val user = (userState as UserState.Success).user
                val daysLeft = user.daysUntilPermanentDeletion()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = stringResource(R.string.warning),
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = stringResource(R.string.account_deleted),
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = stringResource(R.string.restore_info),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (daysLeft > 0) {
                            Text(
                                text = stringResource(R.string.days_left, daysLeft),
                                style = MaterialTheme.typography.bodyLarge,
                                color = PastelPink
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.restore_period_expired),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        if (daysLeft > 0) {
                            Button(
                                onClick = { viewModel.restoreAccount() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PastelPink,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.restore_account))
                            }
                        }

                        Button(
                            onClick = { viewModel.deletePermanently() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.delete_permanently))
                        }

                        OutlinedButton(
                            onClick = { navController.navigate(Screen.Login.route) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }
            }

        }


    }
}
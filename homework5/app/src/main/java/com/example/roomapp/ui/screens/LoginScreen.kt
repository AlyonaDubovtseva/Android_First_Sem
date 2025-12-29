package com.example.roomapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.roomapp.R
import com.example.roomapp.viewmodel.AuthViewModel
import com.example.roomapp.viewmodel.AuthState
import com.example.roomapp.ui.components.LoadingScreen
import com.example.roomapp.ui.navigation.Screen
import com.example.roomapp.ui.theme.PastelPink
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                val userId = (authState as AuthState.Success).userId
                navController.navigate(Screen.CatsList.createRoute(userId)) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }

            is AuthState.DeletedAccount -> {
                val userId = (authState as AuthState.DeletedAccount).userId
                navController.navigate(Screen.RestoreAccount.createRoute(userId))
            }

            is AuthState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = (authState as AuthState.Error).message.toString(),
                        withDismissAction = true
                    )
                }
            }

            else -> {}
        }
    }

    if (authState is AuthState.Loading) {
        LoadingScreen()
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = PastelPink.copy(alpha = 0.1f)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = stringResource(R.string.logo),
                modifier = Modifier.size(80.dp),
                tint = PastelPink
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.welcome),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = stringResource(R.string.email))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = authState is AuthState.Error
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = stringResource(R.string.password))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                isError = authState is AuthState.Error
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.login(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelPink,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = email.isNotBlank() && password.isNotBlank()
            ) {
                Text(
                    text = stringResource(R.string.login_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = {
                    navController.navigate(Screen.Register.route)
                }
            ) {
                Text(
                    text = stringResource(R.string.dont_have_account),
                    color = PastelPink
                )
            }



        }
    }
}
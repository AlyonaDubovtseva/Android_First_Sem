package com.example.roomapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.roomapp.R
import com.example.roomapp.viewmodel.CatsViewModel
import com.example.roomapp.ui.navigation.Screen
import com.example.roomapp.ui.theme.PastelPink
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCatScreen(
    navController: NavController,
    viewModel: CatsViewModel,
    userId: Long
) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val error by viewModel.errorState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_cat),
                        color = PastelPink
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.cat_name)) },
                leadingIcon = {
                    Icon(Icons.Default.Pets, contentDescription = stringResource(R.string.name))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = error?.contains("имя") == true
            )
            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text(stringResource(R.string.cat_breed)) },
                leadingIcon = {
                    Icon(Icons.Default.Category, contentDescription = stringResource(R.string.breed))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = error?.contains("порода") == true
            )
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text(stringResource(R.string.cat_age)) },
                leadingIcon = {
                    Icon(Icons.Default.AccessTime, contentDescription = stringResource(R.string.age))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = error?.contains("возраст") == true
            )
            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text(stringResource(R.string.rating_label)) },
                leadingIcon = {
                    Icon(Icons.Default.Star, contentDescription = stringResource(R.string.cat_rating))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = error?.contains("рейтинг") == true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.cat_description)) },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = stringResource(R.string.description))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                isError = error?.contains("описание") == true
            )
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text(stringResource(R.string.image_url_optional)) },
                leadingIcon = {
                    Icon(Icons.Default.Image, contentDescription = stringResource(R.string.image))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        if (name.isBlank()) {
                            snackbarHostState.showSnackbar(context.getString(R.string.enter_cat_name))
                            return@launch
                        }
                        if (breed.isBlank()) {
                            snackbarHostState.showSnackbar(context.getString(R.string.enter_breed))
                            return@launch
                        }

                        val ageInt = age.toIntOrNull()
                        if (ageInt == null || ageInt <= 0) {
                            snackbarHostState.showSnackbar(context.getString(R.string.enter_valid_age))
                            return@launch
                        }
                        val ratingFloat = rating.toFloatOrNull() ?: 0.0f
                        if (ratingFloat < 0 || ratingFloat > 5) {
                            snackbarHostState.showSnackbar(context.getString(R.string.rating_range))
                            return@launch
                        }
                        val result = viewModel.addCat(
                            name = name,
                            breed = breed,
                            age = ageInt,
                            description = description,
                            rating = ratingFloat,
                            imageUrl = if (imageUrl.isNotBlank()) imageUrl else null
                        )
                        if (result.isSuccess) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelPink,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = name.isNotBlank() && breed.isNotBlank() && age.isNotBlank()
            ) {
                Text(
                    text = stringResource(R.string.add_cat),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }
}
package com.example.roomapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.roomapp.R
import com.example.roomapp.viewmodel.CatsViewModel
import com.example.roomapp.ui.components.CatCard
import com.example.roomapp.ui.components.ShimmerCatCard
import com.example.roomapp.ui.components.SortBottomSheet
import com.example.roomapp.ui.navigation.Screen
import com.example.roomapp.ui.theme.PastelPink
import com.example.roomapp.ui.theme.SoftPink
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatsListScreen(
    navController: NavController,
    viewModel: CatsViewModel,
    userId: Long
) {
    val cats by viewModel.catsState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val sortType by viewModel.currentSortType.collectAsStateWithLifecycle()
    val error by viewModel.errorState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSortSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    withDismissAction = true
                )
                viewModel.clearError()
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.my_cats),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SoftPink
                ),
                actions = {
                    IconButton(
                        onClick = { showSortSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(R.string.sort),
                            tint = PastelPink
                        )
                    }

                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Profile.createRoute(userId))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.profile_icon),
                            tint = PastelPink
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddCat.createRoute(userId))
                },
                containerColor = PastelPink,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_cat_icon))
            }
        },
        containerColor = SoftPink
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && cats.isEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(5) {
                            ShimmerCatCard()
                        }
                    }
                }

                cats.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_cats_message),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.add_first),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cats) { cat ->
                            CatCard(cat = cat)
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }

                }
            }
        }
    }
    if (showSortSheet) {
        SortBottomSheet(
            currentSortType = sortType,
            onSortTypeSelected = { newSortType ->
                viewModel.loadCats(newSortType)
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }
}
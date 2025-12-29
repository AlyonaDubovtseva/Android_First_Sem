package com.example.roomapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomapp.R
import com.example.roomapp.data.datastore.AppDataStore
import com.example.roomapp.data.entity.Cat
import com.example.roomapp.data.repository.CatRepository
import com.example.roomapp.model.SortType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CatsViewModel(
    private val catRepository: CatRepository,
    private val dataStore: AppDataStore,
    private val ownerId: Long
) : ViewModel() {

    private val _catsState = MutableStateFlow<List<Cat>>(emptyList())
    val catsState: StateFlow<List<Cat>> = _catsState.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _currentSortType = MutableStateFlow(SortType.DATE_NEW)
    val currentSortType: StateFlow<SortType> = _currentSortType.asStateFlow()
    private var loadJob: Job? = null
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()
    init {
        loadCats(SortType.DATE_NEW)
    }
    fun loadCats(sortType: SortType = SortType.DATE_NEW) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            _currentSortType.value = sortType
            dataStore.saveSortType(sortType.name)
            catRepository.getCatsByOwner(ownerId, sortType).collectLatest { cats ->
                _catsState.value = cats
                _isLoading.value = false
                _errorState.value = null
            }
        }
    }
    suspend fun addCat(
        name: String,
        breed: String,
        age: Int,
        description: String,
        rating: Float = 0.0f,
        imageUrl: String? = null
    ): Result<Long> {
        return try {
            if (ownerId <= 0) {
                _errorState.value = R.string.wrong_id.toString()
                return Result.failure(Exception(R.string.wrong_id.toString()))
            }
            val cat = Cat(
                ownerId = ownerId,
                name = name,
                breed = breed,
                age = age,
                description = description,
                rating = rating,
                imageUrl = imageUrl,
                createdAt = Clock.System.now()
            )

            catRepository.addCat(cat).fold(
                onSuccess = { id ->
                    loadCats(_currentSortType.value)
                    Result.success(id)
                },
                onFailure = { e ->
                    val errorMessage = e.message ?: R.string.error.toString()
                    _errorState.value = errorMessage
                    Result.failure(e)
                }
            )
        } catch (e: Exception) {
            val errorMessage = e.message ?: R.string.error_generic.toString()
            _errorState.value = errorMessage
            Result.failure(e)
        }
    }
    fun clearError() {
        _errorState.value = null
    }
}

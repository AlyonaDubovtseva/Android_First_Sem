package com.example.roomapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomapp.R
import com.example.roomapp.data.datastore.AppDataStore
import com.example.roomapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestoreAccountViewModel(
    private val userRepository: UserRepository,
    private val dataStore: AppDataStore,
    private val userId: Long
) : ViewModel() {

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _restoreState = MutableStateFlow<RestoreState>(RestoreState.Idle)
    val restoreState: StateFlow<RestoreState> = _restoreState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _userState.value = UserState.Loading

            val user = userRepository.getUserById(userId)
            if (user != null && user.isDeleted) {
                _userState.value = UserState.Success(user)
            } else {
                _userState.value = UserState.Error(R.string.error_account_not_found.toString())
            }
        }
    }

    fun restoreAccount() {
        viewModelScope.launch {
            _restoreState.value = RestoreState.Loading

            val success = userRepository.restoreUser(userId)
            if (success) {
                val currentUser = when (val state = _userState.value) {
                    is UserState.Success -> state.user
                    else -> null
                }
                dataStore.saveUserId(userId)
                dataStore.saveUserEmail(currentUser?.email ?: "")
                _restoreState.value = RestoreState.Success
            } else {

                _restoreState.value = RestoreState.Error(R.string.error_restore_failed.toString())
            }
        }
    }

    fun deletePermanently() {
        viewModelScope.launch {
            _deleteState.value = DeleteState.Loading
            val success = userRepository.deleteUserPermanently(userId)
            if (success) {
                _deleteState.value = DeleteState.Success
            } else {

                _deleteState.value = DeleteState.Error(R.string.error_delete_failed.toString())
            }

        }
    }
    fun resetRestoreState() {
        _restoreState.value = RestoreState.Idle
    }
    fun resetDeleteState() {
        _deleteState.value = DeleteState.Idle
    }


}
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

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val dataStore: AppDataStore,
    private val userId: Long
) : ViewModel() {

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()


    init {

        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _userState.value = UserState.Loading

            if (userId <= 0) {
                _userState.value = UserState.Error(R.string.wrong_id.toString())
                return@launch
            }

            val user = userRepository.getActiveUserById(userId)
            if (user != null) {
                _userState.value = UserState.Success(user)
            } else {
                val anyUser = userRepository.getUserById(userId)
                if (anyUser != null) {
                    _userState.value = UserState.Success(anyUser)
                } else {
                    _userState.value = UserState.Error(R.string.error_user_not_found.toString())
                }
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading
            dataStore.clearUserData()
            _logoutState.value = LogoutState.Success
        }
    }


    fun softDeleteAccount() {
        viewModelScope.launch {
            _deleteState.value = DeleteState.Loading

            val success = userRepository.softDeleteUser(userId)
            if (success) {

                dataStore.clearUserData()
                _deleteState.value = DeleteState.Success
            } else {
                _deleteState.value = DeleteState.Error(R.string.error_delete_failed.toString())
            }
        }
    }

    fun resetLogoutState() {
        _logoutState.value = LogoutState.Idle
    }
    fun resetDeleteState() {
        _deleteState.value = DeleteState.Idle
    }



}
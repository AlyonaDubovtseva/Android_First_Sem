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

class AuthViewModel(
    private val userRepository: UserRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error(R.string.please_fill_all_fields.toString())
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            userRepository.loginUser(email, password).fold(
                onSuccess = { user ->
                    if (user.isDeleted) {
                        _authState.value = AuthState.DeletedAccount(user.id)
                    } else {
                        if (user.id <= 0) {
                            _authState.value = AuthState.Error(R.string.wrong_id.toString())
                            return@launch
                        }
                        dataStore.saveUserId(user.id)
                        dataStore.saveUserEmail(user.email)
                        _authState.value = AuthState.Success(user.id)
                    }
                },
                onFailure = { e ->
                    _authState.value = AuthState.Error(e.message ?: R.string.error.toString())
                }
            )
        }
    }

    fun register(email: String, password: String, name: String, phone: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank() || phone.isBlank()) {
            _authState.value = AuthState.Error(R.string.error_empty_fields.toString())
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error(R.string.error_password_length.toString())
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error(R.string.error_invalid_email.toString())
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            userRepository.registerUser(email, password, name, phone).fold(
                onSuccess = { userId ->
                    if (userId <= 0) {
                        _authState.value = AuthState.Error(R.string.wrong_id.toString())
                        return@launch
                    }
                    dataStore.saveUserId(userId)
                    dataStore.saveUserEmail(email)

                    _authState.value = AuthState.Success(userId)
                },
                onFailure = { e ->
                    _authState.value = AuthState.Error(e.message ?: R.string.error_register_failed.toString())
                }

            )


        }

    }

}
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: Long) : AuthState()
    data class Error(val message: Comparable<*>) : AuthState()
    data class DeletedAccount(val userId: Long) : AuthState()
}
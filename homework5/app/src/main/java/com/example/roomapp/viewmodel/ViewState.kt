package com.example.roomapp.viewmodel

import com.example.roomapp.data.entity.User

sealed class UserState {
    object Loading : UserState()
    data class Success(val user: User) : UserState()
    data class Error(val message: String) : UserState()
}

sealed class LogoutState {
    object Idle : LogoutState()
    object Loading : LogoutState()
    object Success : LogoutState()
    data class Error(val message: String) : LogoutState()
}

sealed class DeleteState {
    object Idle : DeleteState()
    object Loading : DeleteState()
    object Success : DeleteState()
    data class Error(val message: String) : DeleteState()
}

sealed class RestoreState {
    object Idle : RestoreState()
    object Loading : RestoreState()
    object Success : RestoreState()
    data class Error(val message: String) : RestoreState()
}


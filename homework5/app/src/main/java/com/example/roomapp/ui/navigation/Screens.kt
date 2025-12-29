package com.example.roomapp.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object CatsList : Screen("cats_list/{userId}") {
        fun createRoute(userId: Long) = "cats_list/$userId"
    }
    object AddCat : Screen("add_cat/{userId}") {
        fun createRoute(userId: Long) = "add_cat/$userId"
    }
    object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: Long) = "profile/$userId"
    }
    object RestoreAccount : Screen("restore_account/{userId}") {
        fun createRoute(userId: Long) = "restore_account/$userId"
    }
}
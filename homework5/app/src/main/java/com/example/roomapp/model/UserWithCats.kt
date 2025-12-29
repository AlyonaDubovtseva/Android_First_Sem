package com.example.roomapp.model


import com.example.roomapp.data.entity.Cat
import com.example.roomapp.data.entity.User

data class UserWithCats(
    val user: User,
    val cats: List<Cat>
)
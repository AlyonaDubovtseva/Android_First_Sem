package com.example.roomapp.data.repository

import com.example.roomapp.R
import com.example.roomapp.data.dao.UserDao
import com.example.roomapp.data.entity.User
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        phone: String
    ): Result<Long> {
        return try {
            if (userDao.userExists(email)) {
                return Result.failure(Exception(R.string.wrong_id.toString()))
            }
            val user = User(
                email = email,
                password = password,
                name = name,
                phone = phone,
                createdAt = Clock.System.now()
            )
            val userId = userDao.insert(user)
            if (userId <= 0) {
                return Result.failure(Exception(R.string.wrong_id.toString()))
            }

            val createdUser = userDao.getUserById(userId)
            if (createdUser == null) {
                return Result.failure(Exception(R.string.wrong_id.toString()))
            }
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun loginUser(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val activeUser = userDao.getActiveUserByCredentials(email, password)
            if (activeUser != null) {
                return Result.success(activeUser)
            }

            val deletedUser = userDao.getDeletedUserByCredentials(email, password)
            if (deletedUser != null) {
                return Result.success(deletedUser)
            }

            Result.failure(Exception(R.string.error_invalid_credentials.toString()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }

    suspend fun getActiveUserById(userId: Long): User? {
        return userDao.getActiveUserById(userId)
    }

    suspend fun softDeleteUser(userId: Long): Boolean {
        return try {
            val user = userDao.getUserById(userId)
            if (user != null) {
                val deletedUser = user.softDelete()
                userDao.update(deletedUser)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun restoreUser(userId: Long): Boolean {
        return try {
            val user = userDao.getUserById(userId)
            if (user != null) {
                val restoredUser = user.restore()
                userDao.update(restoredUser)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteUserPermanently(userId: Long): Boolean {
        return try {
            userDao.deletePermanently(userId)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun cleanupDeletedUsers() {
        try {
            val now = Clock.System.now()
            val sevenDaysInSeconds = 7L * 24 * 60 * 60
            val sevenDaysAgo = Instant.fromEpochSeconds(now.epochSeconds - sevenDaysInSeconds)
            val usersToDelete = userDao.getUsersToDeletePermanently(sevenDaysAgo)

            usersToDelete.forEach { user ->
                userDao.deletePermanently(user.id)
            }
        } catch (e: Exception) {
        }
    }
}
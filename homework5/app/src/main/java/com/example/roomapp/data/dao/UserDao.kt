package com.example.roomapp.data.dao

import androidx.room.*
import com.example.roomapp.data.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long
    @Update
    suspend fun update(user: User)
    @Query("SELECT * FROM users WHERE email = :email AND password = :password AND is_deleted = 0 LIMIT 1")
    suspend fun getActiveUserByCredentials(email: String, password: String): User?
    @Query("SELECT * FROM users WHERE email = :email AND password = :password AND is_deleted = 1 LIMIT 1")
    suspend fun getDeletedUserByCredentials(email: String, password: String): User?
    @Query("SELECT * FROM users WHERE id = :id AND is_deleted = 0 LIMIT 1")
    suspend fun getActiveUserById(id: Long): User?
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email AND is_deleted = 0)")
    suspend fun userExists(email: String): Boolean
    @Query("SELECT * FROM users WHERE is_deleted = 1 AND deleted_at <= :threshold")
    suspend fun getUsersToDeletePermanently(threshold: Instant): List<User>
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deletePermanently(userId: Long)
}

package com.example.roomapp.data.dao

import androidx.room.*
import com.example.roomapp.data.entity.Cat
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {
    @Insert
    suspend fun insert(cat: Cat): Long
    @Update
    suspend fun update(cat: Cat)
    @Delete
    suspend fun delete(cat: Cat)
    @Query("SELECT * FROM cats WHERE owner_id = :ownerId ORDER BY name ASC")
    fun getCatsSortedByName(ownerId: Long): Flow<List<Cat>>
    @Query("SELECT * FROM cats WHERE owner_id = :ownerId ORDER BY name DESC")
    fun getCatsSortedByNameDesc(ownerId: Long): Flow<List<Cat>>
    @Query("SELECT * FROM cats WHERE owner_id = :ownerId ORDER BY age ASC")
    fun getCatsSortedByAge(ownerId: Long): Flow<List<Cat>>
    @Query("SELECT * FROM cats WHERE owner_id = :ownerId ORDER BY age DESC")
    fun getCatsSortedByAgeDesc(ownerId: Long): Flow<List<Cat>>
    @Query("SELECT * FROM cats WHERE owner_id = :ownerId ORDER BY rating DESC")
    fun getCatsSortedByRating(ownerId: Long): Flow<List<Cat>>
    @Query("SELECT * FROM cats WHERE owner_id = :ownerId ORDER BY created_at DESC")
    fun getCatsSortedByDateNew(ownerId: Long): Flow<List<Cat>>
    @Query("SELECT * FROM cats WHERE owner_id = :ownerId ORDER BY created_at ASC")
    fun getCatsSortedByDateOld(ownerId: Long): Flow<List<Cat>>
    @Query("SELECT * FROM cats WHERE owner_id = :ownerId")
    fun getCatsByOwner(ownerId: Long): Flow<List<Cat>>
}
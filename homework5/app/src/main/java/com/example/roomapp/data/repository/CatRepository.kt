package com.example.roomapp.data.repository

import com.example.roomapp.data.dao.CatDao
import com.example.roomapp.model.SortType
import com.example.roomapp.data.entity.Cat
import kotlinx.coroutines.flow.Flow

class CatRepository(
    private val catDao: CatDao
) {
    suspend fun addCat(cat: Cat): Result<Long> {
        return try {
            val id = catDao.insert(cat)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCatsByOwner(ownerId: Long, sortType: SortType): Flow<List<Cat>> {
        return when (sortType) {
            SortType.NAME_ASC -> catDao.getCatsSortedByName(ownerId)
            SortType.NAME_DESC -> catDao.getCatsSortedByNameDesc(ownerId)
            SortType.AGE_ASC -> catDao.getCatsSortedByAge(ownerId)
            SortType.AGE_DESC -> catDao.getCatsSortedByAgeDesc(ownerId)
            SortType.RATING -> catDao.getCatsSortedByRating(ownerId)
            SortType.DATE_NEW -> catDao.getCatsSortedByDateNew(ownerId)
            SortType.DATE_OLD -> catDao.getCatsSortedByDateOld(ownerId)
        }
    }
}
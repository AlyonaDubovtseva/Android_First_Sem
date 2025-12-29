package com.example.roomapp.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.roomapp.data.database.DateConverter
import com.example.roomapp.data.dao.CatDao
import com.example.roomapp.data.dao.UserDao
import com.example.roomapp.data.entity.Cat
import com.example.roomapp.data.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@Database(
    entities = [User::class, Cat::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun catDao(): CatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "catlovers.db"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            CoroutineScope(Dispatchers.IO).launch {
                val database = getDatabase(context)
                val userDao = database.userDao()
                val catDao = database.catDao()
                val testUser = User(
                    email = "test@example.com",
                    password = "password123",
                    name = "Тестовый пользователь",
                    phone = "+79001234567",
                    createdAt = Instant.fromEpochMilliseconds(System.currentTimeMillis())
                )

                val userId = userDao.insert(testUser)
                val cats = listOf(
                    Cat(ownerId = userId, name = "Мурзик", breed = "Британский", age = 3, description = "Спокойный и ласковый кот", rating = 4.5f, imageUrl = null),
                    Cat(ownerId = userId, name = "Барсик", breed = "Сиамский", age = 2, description = "Игривый и любопытный", rating = 4.8f, imageUrl = null),
                    Cat(ownerId = userId, name = "Снежок", breed = "Персидский", age = 5, description = "Пушистый и нежный", rating = 4.3f, imageUrl = null),
                    Cat(ownerId = userId, name = "Рыжик", breed = "Мейн-кун", age = 4, description = "Большой и добрый", rating = 4.9f, imageUrl = null),
                    Cat(ownerId = userId, name = "Васька", breed = "Дворовый", age = 1, description = "Молодой и активный", rating = 4.0f, imageUrl = null),
                    Cat(ownerId = userId, name = "Пушок", breed = "Русская голубая", age = 2, description = "Элегантный и умный", rating = 4.7f, imageUrl = null),
                    Cat(ownerId = userId, name = "Тигра", breed = "Бенгальский", age = 3, description = "Энергичный и дружелюбный", rating = 4.6f, imageUrl = null),
                    Cat(ownerId = userId, name = "Луна", breed = "Турецкая ангора", age = 4, description = "Нежная и ласковая", rating = 4.8f, imageUrl = null),
                    Cat(ownerId = userId, name = "Солнышко", breed = "Абиссинская", age = 2, description = "Игривая и общительная", rating = 4.5f, imageUrl = null),
                    Cat(ownerId = userId, name = "Маркиз", breed = "Норвежская лесная", age = 5, description = "Величественный и спокойный", rating = 4.9f, imageUrl = null),
                    Cat(ownerId = userId, name = "Зефир", breed = "Рэгдолл", age = 3, description = "Мягкий и послушный", rating = 4.7f, imageUrl = null),
                    Cat(ownerId = userId, name = "Оскар", breed = "Шотландская вислоухая", age = 4, description = "Дружелюбный и любопытный", rating = 4.6f, imageUrl = null),
                    Cat(ownerId = userId, name = "Мила", breed = "Экзотическая короткошерстная", age = 2, description = "Спокойная и ласковая", rating = 4.8f, imageUrl = null),
                    Cat(ownerId = userId, name = "Чарли", breed = "Американская короткошерстная", age = 3, description = "Активный и веселый", rating = 4.5f, imageUrl = null),
                    Cat(ownerId = userId, name = "Белла", breed = "Бирманская", age = 4, description = "Элегантная и нежная", rating = 4.9f, imageUrl = null),
                    Cat(ownerId = userId, name = "Макс", breed = "Ориентальная", age = 2, description = "Общительный и умный", rating = 4.6f, imageUrl = null),
                    Cat(ownerId = userId, name = "Лили", breed = "Тонкинская", age = 3, description = "Игривая и дружелюбная", rating = 4.7f, imageUrl = null),
                    Cat(ownerId = userId, name = "Джек", breed = "Европейская короткошерстная", age = 4, description = "Независимый и умный", rating = 4.4f, imageUrl = null),
                    Cat(ownerId = userId, name = "Софи", breed = "Гималайская", age = 5, description = "Спокойная и ласковая", rating = 4.8f, imageUrl = null),
                    Cat(ownerId = userId, name = "Рокки", breed = "Американский керл", age = 2, description = "Активный и любопытный", rating = 4.5f, imageUrl = null),
                    Cat(ownerId = userId, name = "Грейс", breed = "Балинезийская", age = 3, description = "Элегантная и общительная", rating = 4.7f, imageUrl = null)
                )
                cats.forEach { catDao.insert(it) }
            }
        }
    }
}
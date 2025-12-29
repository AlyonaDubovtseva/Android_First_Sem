package com.example.roomapp.data.entity

import androidx.room.*
import kotlinx.datetime.Instant

@Entity(
    tableName = "cats",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["owner_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["owner_id"])
    ]
)
data class Cat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "owner_id")
    val ownerId: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "breed")
    val breed: String,
    @ColumnInfo(name = "age")
    val age: Int,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
    @ColumnInfo(name = "rating")
    val rating: Float = 0.0f,
    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null
)
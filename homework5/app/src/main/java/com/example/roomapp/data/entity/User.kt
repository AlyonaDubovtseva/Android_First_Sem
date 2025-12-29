package com.example.roomapp.data.entity
import androidx.room.*
import kotlinx.datetime.Instant

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "phone")
    val phone: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.DISTANT_PAST,
    @ColumnInfo(name = "deleted_at")
    val deletedAt: Instant? = null,
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false
) {

    fun softDelete(): User {
        return copy(
            isDeleted = true,
            deletedAt = Instant.fromEpochMilliseconds(System.currentTimeMillis())
        )
    }

    fun restore(): User {
        return copy(
            isDeleted = false,
            deletedAt = null
        )
    }
    fun daysUntilPermanentDeletion(): Int {
        val deletedAt = deletedAt ?: return 7
        val now = Instant.fromEpochMilliseconds(System.currentTimeMillis())
        val daysPassed = (now.epochSeconds - deletedAt.epochSeconds) / (24 * 3600)
        return (7 - daysPassed).coerceAtLeast(0).toInt()
    }
}

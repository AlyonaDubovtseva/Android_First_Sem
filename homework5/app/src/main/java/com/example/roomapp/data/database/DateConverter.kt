package com.example.roomapp.data.database

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { 
            if (it == 0L) Instant.DISTANT_PAST
            else Instant.fromEpochMilliseconds(it)
        }
    }
    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.let { 
            if (it == Instant.DISTANT_PAST) 0L
            else {
                it.epochSeconds * 1000L
            }
        }
    }
}
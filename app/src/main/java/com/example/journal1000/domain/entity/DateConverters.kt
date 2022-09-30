package com.example.journal1000.domain.entity

import androidx.room.TypeConverter
import java.util.*

class DateConverters {
    @TypeConverter
    fun fromDate(value: Date): Long {
        return value.time
    }

    @TypeConverter
    fun toDate(value: Long): Date {
        return Date(value)
    }
}
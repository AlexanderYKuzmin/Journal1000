package com.example.journal1000.domain.entity

import androidx.room.TypeConverter
import java.util.*

class Converters() {

    @TypeConverter
    fun fromPlayerOrder(value: PlayerOrder): Int {
        return value.ordinal
    }

    @TypeConverter
    fun toPlayerOrder(value: Int): PlayerOrder {
        return enumValues<PlayerOrder>()[value]
    }

    @TypeConverter
    fun fromTimeStamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toTimeStamp(date: Date?): Long? {
        return date?.time
    }
}
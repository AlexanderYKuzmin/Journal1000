package com.example.journal1000.extensions

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String.toDate(): Date? {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
    return try {
        dateFormat.parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun String.substringIfOutOfRange(range: IntRange): String {
    return if (this.length > 9) {
        this.substring(range)
    } else this
}


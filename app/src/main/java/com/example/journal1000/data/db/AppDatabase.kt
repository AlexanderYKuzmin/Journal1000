package com.example.journal1000.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.journal1000.domain.entity.Converters
import com.example.journal1000.domain.entity.Game
import com.example.journal1000.domain.entity.Player
import com.example.journal1000.domain.entity.Score
import com.example.journal1000.domain.repository.GameDao

@Database(entities = [Game::class, Score::class, Player::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        val db: AppDatabase? = null
        const val DB_NAME = "main_db"

        fun getInstance(context: Context): AppDatabase {
            db?.let {
                return it
            }
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME
            ).build()
        }
    }

    abstract fun gameDao(): GameDao
}
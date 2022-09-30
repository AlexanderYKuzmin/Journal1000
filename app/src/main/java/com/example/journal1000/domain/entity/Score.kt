package com.example.journal1000.domain.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "scores",
    foreignKeys = [
        ForeignKey(entity = Game::class,
        parentColumns = ["game_id"],
        childColumns = ["game_id"],
        onDelete = CASCADE
        )])
@Parcelize
data class Score(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "score_id")
    var scoreId: Long = 0L,

    @ColumnInfo(name = "player_1")
    var playerOneCount: Int = 0,

    @ColumnInfo(name = "player_2")
    var playerTwoCount: Int = 0,

    @ColumnInfo(name = "player_3")
    var playerThreeCount: Int = 0,

    @ColumnInfo(name = "step")
    var step: Int = 0,

    @ColumnInfo(name = "game_id")
    var gameId: Long = 0
) : Parcelable

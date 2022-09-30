package com.example.journal1000.domain.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "players",
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["game_id"],
            childColumns = ["game_id"],
            onDelete = CASCADE
        )]
)
@Parcelize
data class Player(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "player_id")
    var playerId: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "bolt_num")
    var boltNumber: Int = 0,

    @ColumnInfo(name = "hundred")
    var onHundred: Boolean = false,

    @ColumnInfo(name = "barrel")
    var isOnBarrel: Boolean = false,

    @ColumnInfo(name = "barrel_count")
    var onBarrelAttemptCount: Int = 0,

    @ColumnInfo(name = "winner")
    var isWinner: Boolean = false,

    @ColumnInfo(name = "count")
    var count: Int = 0,

    @ColumnInfo(name = "order")
    val playerOrder: PlayerOrder,

    @ColumnInfo(name = "game_id")
    var gameId: Long = 0,

    @ColumnInfo(name = "req_points")
    var requestedPoints: Int = 0,

    @ColumnInfo(name = "in_time")
    var countInTime: Int = 0

) : Parcelable
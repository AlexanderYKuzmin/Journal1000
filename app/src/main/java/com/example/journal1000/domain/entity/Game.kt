package com.example.journal1000.domain.entity

import androidx.room.*
import java.util.Date

@Entity(tableName = "games")
data class Game(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "game_id")
    var gameId: Long? = null,

    @ColumnInfo(name = "players_qnt")
    val numberOfPlayers: Int,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "date")
    val gameDate: Date = Date(),

    @ColumnInfo(name = "player_order")
    @TypeConverters(Converters::class)
    var onHundredPlayer: PlayerOrder = PlayerOrder.ONE,

    @ColumnInfo(name = "type")
    val type: GameType,

    @ColumnInfo(name = "finished")
    var isGameFinished: Boolean = false,

    @ColumnInfo(name = "winner")
    var winner: String? = null
    ) {

    @Ignore
    var hasWinner: Boolean = false
}

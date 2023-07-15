package com.example.journal1000.domain.repository

import androidx.room.*
import com.example.journal1000.domain.entity.Game
import com.example.journal1000.domain.entity.GameWithScores
import com.example.journal1000.domain.entity.Player
import com.example.journal1000.domain.entity.Score
import java.util.*

@Dao
interface GameDao {

    @Query("SELECT * FROM games")
    fun getListOfGames(): List<GameWithScores>

    @Query("SELECT * FROM games WHERE date BETWEEN :fromDate AND :toDate")
    suspend fun getListOfGamesByDate(fromDate: Date, toDate: Date): MutableList<GameWithScores>

    @Query("SELECT * FROM games WHERE game_id == :id")
    suspend fun getGameWithScores(id: Long): GameWithScores?

    @Query("DELETE FROM games")
    fun deleteAll()

    @Delete
    suspend fun deleteGame(games: List<Game>)  // Должно удаляться вроде как все каскадом

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(game: Game): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveScores(scores: List<Score>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayers(players: List<Player>)
}
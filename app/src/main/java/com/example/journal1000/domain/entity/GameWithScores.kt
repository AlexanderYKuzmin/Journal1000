package com.example.journal1000.domain.entity

import androidx.room.Embedded
import androidx.room.Relation

class GameWithScores (
    @Embedded
    var game: Game,

    @Relation(parentColumn = "game_id", entityColumn = "game_id")
    var scores: List<Score> = emptyList(),

    @Relation(parentColumn = "game_id", entityColumn = "game_id")
    var players: List<Player>,
)
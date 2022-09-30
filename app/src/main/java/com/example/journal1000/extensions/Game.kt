package com.example.journal1000.extensions

import com.example.journal1000.domain.entity.Game

fun Game.isNew(): Boolean = this.gameId == 0L
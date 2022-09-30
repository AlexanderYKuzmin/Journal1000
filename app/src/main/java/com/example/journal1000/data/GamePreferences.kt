package com.example.journal1000.data

data class GamePreferences(
  val id: Long,
  val numberOfPlayers: Int,
  val name1: String?,
  val name2: String?,
  val name3: String?
)
package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playerName: String = "Pavi Hunter",
    val score: Int,
    val fruitsEaten: Int,
    val snakeLength: Int,
    val gameMode: String,
    val difficulty: String,
    val rivalsDefeated: Int = 0,
    val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "player_stats")
data class PlayerStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalGamesPlayed: Int = 0,
    val totalScoreAccumulated: Long = 0L,
    val highestScore: Int = 0,
    val totalFruitsEaten: Int = 0,
    val longestSnakeLength: Int = 3,
    val totalRivalsDefeated: Int = 0,
    val unlockedSkins: String = "raja_pavi",
    val selectedSkinId: String = "raja_pavi"
)

package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_scores ORDER BY score DESC LIMIT 50")
    fun getAllHighScores(): Flow<List<GameScoreEntity>>

    @Query("SELECT * FROM game_scores WHERE gameMode = :mode ORDER BY score DESC LIMIT 30")
    fun getHighScoresByMode(mode: String): Flow<List<GameScoreEntity>>

    @Query("SELECT MAX(score) FROM game_scores WHERE gameMode = :mode")
    fun getMaxScoreForMode(mode: String): Flow<Int?>

    @Query("SELECT MAX(score) FROM game_scores")
    fun getOverallMaxScore(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: GameScoreEntity): Long

    @Query("DELETE FROM game_scores")
    suspend fun clearAllScores()

    // Player Career Stats
    @Query("SELECT * FROM player_stats WHERE id = 1 LIMIT 1")
    fun getPlayerStats(): Flow<PlayerStatsEntity?>

    @Query("SELECT * FROM player_stats WHERE id = 1 LIMIT 1")
    suspend fun getPlayerStatsDirect(): PlayerStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayerStats(stats: PlayerStatsEntity)
}

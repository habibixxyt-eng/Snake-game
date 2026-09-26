package com.example.data

import com.example.model.GameMode
import com.example.model.SnakeSkin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameRepository(private val gameDao: GameDao) {

    fun getScoresForMode(mode: GameMode): Flow<List<GameScoreEntity>> {
        return gameDao.getHighScoresByMode(mode.name)
    }

    fun getAllScores(): Flow<List<GameScoreEntity>> {
        return gameDao.getAllHighScores()
    }

    fun getHighScoreForMode(mode: GameMode): Flow<Int?> {
        return gameDao.getMaxScoreForMode(mode.name)
    }

    fun getOverallHighScore(): Flow<Int?> {
        return gameDao.getOverallMaxScore()
    }

    val playerStats: Flow<PlayerStatsEntity?> = gameDao.getPlayerStats()

    suspend fun recordGameFinished(
        playerName: String,
        score: Int,
        fruitsEaten: Int,
        snakeLength: Int,
        mode: GameMode,
        difficulty: String,
        rivalsDefeated: Int
    ): Long = withContext(Dispatchers.IO) {
        val entry = GameScoreEntity(
            playerName = playerName.ifBlank { "Pavi King" },
            score = score,
            fruitsEaten = fruitsEaten,
            snakeLength = snakeLength,
            gameMode = mode.name,
            difficulty = difficulty,
            rivalsDefeated = rivalsDefeated
        )
        val scoreId = gameDao.insertScore(entry)

        // Update Career Stats
        val currentStats = gameDao.getPlayerStatsDirect() ?: PlayerStatsEntity()
        val newHighest = maxOf(currentStats.highestScore, score)
        val newLongest = maxOf(currentStats.longestSnakeLength, snakeLength)

        // Check unlocked skins based on cumulative or high scores
        val currentUnlocked = currentStats.unlockedSkins.split(",").toMutableSet()
        SnakeSkin.values().forEach { skin ->
            if (newHighest >= skin.unlockScore) {
                currentUnlocked.add(skin.id)
            }
        }

        val updatedStats = currentStats.copy(
            totalGamesPlayed = currentStats.totalGamesPlayed + 1,
            totalScoreAccumulated = currentStats.totalScoreAccumulated + score,
            highestScore = newHighest,
            totalFruitsEaten = currentStats.totalFruitsEaten + fruitsEaten,
            longestSnakeLength = newLongest,
            totalRivalsDefeated = currentStats.totalRivalsDefeated + rivalsDefeated,
            unlockedSkins = currentUnlocked.joinToString(",")
        )
        gameDao.savePlayerStats(updatedStats)

        scoreId
    }

    suspend fun setSelectedSkin(skinId: String) = withContext(Dispatchers.IO) {
        val current = gameDao.getPlayerStatsDirect() ?: PlayerStatsEntity()
        gameDao.savePlayerStats(current.copy(selectedSkinId = skinId))
    }

    suspend fun clearAllScores() = withContext(Dispatchers.IO) {
        gameDao.clearAllScores()
    }
}

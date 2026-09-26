package com.example.model

enum class GameStatus {
    READY,
    PLAYING,
    PAUSED,
    GAME_OVER
}

data class ScorePopup(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val position: GridPoint,
    val colorHex: Long = 0xFFFBBF24
)

data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val colorHex: Long,
    val alpha: Float = 1.0f,
    val radius: Float = 6f
)

data class GameState(
    val status: GameStatus = GameStatus.READY,
    val mode: GameMode = GameMode.CLASSIC,
    val difficulty: Difficulty = Difficulty.CASUAL,
    val selectedSkin: SnakeSkin = SnakeSkin.RAJA_PAVI,
    val score: Int = 0,
    val highScore: Int = 0,
    val fruitsEaten: Int = 0,
    val comboMultiplier: Int = 1,
    val comboTimerTicks: Int = 0,
    val remainingTimeSeconds: Int = 90, // for TIME_ATTACK
    val boardWidth: Int = 20,
    val boardHeight: Int = 26,
    val snakeBody: List<GridPoint> = listOf(
        GridPoint(10, 14),
        GridPoint(10, 15),
        GridPoint(10, 16)
    ),
    val currentDirection: Direction = Direction.UP,
    val queuedDirection: Direction = Direction.UP,
    val pendingGrowth: Int = 0,
    val fruits: List<ActiveFruit> = emptyList(),
    val rivals: List<RivalSnake> = emptyList(),
    val rivalsDefeated: Int = 0,
    val popups: List<ScorePopup> = emptyList(),
    val particles: List<Particle> = emptyList(),
    // Active powerups
    val shieldTicksRemaining: Int = 0,
    val speedBoostTicksRemaining: Int = 0,
    val doublePointsTicksRemaining: Int = 0,
    val fireTrailTicksRemaining: Int = 0,
    val isTurboActive: Boolean = false,
    val gameOverReason: String = "",
    val tamilPunchline: String = "Pavi Endra Paambu varaaru, vazhi vidu!"
) {
    val isInvincible: Boolean get() = shieldTicksRemaining > 0
    val currentSpeedMultiplier: Float
        get() {
            var mult = 1.0f
            if (speedBoostTicksRemaining > 0) mult *= 1.4f
            if (fireTrailTicksRemaining > 0) mult *= 1.6f
            if (isTurboActive) mult *= 1.5f
            return mult
        }
}

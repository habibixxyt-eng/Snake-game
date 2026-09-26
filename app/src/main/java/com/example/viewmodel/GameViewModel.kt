package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.GameSoundManager
import com.example.data.GameRepository
import com.example.data.PaviDatabase
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PaviDatabase.getInstance(application)
    val repository = GameRepository(database.gameDao())
    val soundManager = GameSoundManager(application)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _controlMode = MutableStateFlow(ControlMode.DPAD)
    val controlMode: StateFlow<ControlMode> = _controlMode.asStateFlow()

    val playerStats = repository.playerStats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private var gameLoopJob: Job? = null
    private var timeAttackJob: Job? = null

    private val tamilPunchlines = listOf(
        "Naan oru thadava saapta, nooru thadava valarvaen!",
        "Pavi Endra Paambu varaaru, ellarum vazhi vidu!",
        "Idhu Tamil Naatu Raja Paambu da!",
        "Vera level slither! Vetri namadhey!",
        "Kavanamaa aadu thozha, vaalil muttikitta!",
        "Naagam endraal nadungum ulagam!",
        "Thalaivan Pavi eppodhum jeippaan!"
    )

    init {
        // Collect current mode high score
        viewModelScope.launch {
            _gameState.map { it.mode }.distinctUntilChanged().collect { mode ->
                repository.getHighScoreForMode(mode).collect { high ->
                    _gameState.update { it.copy(highScore = high ?: 0) }
                }
            }
        }

        // Initialize selected skin from career stats
        viewModelScope.launch {
            repository.playerStats.filterNotNull().first().let { stats ->
                val skin = SnakeSkin.values().find { it.id == stats.selectedSkinId } ?: SnakeSkin.RAJA_PAVI
                _gameState.update { it.copy(selectedSkin = skin) }
            }
        }
    }

    fun setControlMode(mode: ControlMode) {
        _controlMode.value = mode
    }

    fun selectSkin(skin: SnakeSkin) {
        _gameState.update { it.copy(selectedSkin = skin) }
        viewModelScope.launch {
            repository.setSelectedSkin(skin.id)
        }
    }

    fun startNewGame(
        mode: GameMode = _gameState.value.mode,
        difficulty: Difficulty = _gameState.value.difficulty
    ) {
        gameLoopJob?.cancel()
        timeAttackJob?.cancel()

        val startX = 10
        val startY = 14
        val initialBody = listOf(
            GridPoint(startX, startY),
            GridPoint(startX, startY + 1),
            GridPoint(startX, startY + 2)
        )

        val rivals = if (mode == GameMode.ARENA_SURVIVAL) {
            listOf(
                RivalSnake(
                    id = "chinna_paambu",
                    name = "Karuppan",
                    body = listOf(GridPoint(3, 3), GridPoint(3, 4), GridPoint(3, 5)),
                    direction = Direction.DOWN,
                    color = Color(0xFFF97316)
                ),
                RivalSnake(
                    id = "rathinam",
                    name = "Rathinam",
                    body = listOf(GridPoint(16, 22), GridPoint(16, 21), GridPoint(16, 20)),
                    direction = Direction.UP,
                    color = Color(0xFFA855F7)
                )
            )
        } else {
            emptyList()
        }

        val initialFruits = spawnInitialFruits(initialBody, rivals)

        _gameState.value = GameState(
            status = GameStatus.PLAYING,
            mode = mode,
            difficulty = difficulty,
            selectedSkin = _gameState.value.selectedSkin,
            score = 0,
            highScore = _gameState.value.highScore,
            fruitsEaten = 0,
            comboMultiplier = 1,
            remainingTimeSeconds = if (mode == GameMode.TIME_ATTACK) 90 else 0,
            boardWidth = 20,
            boardHeight = 26,
            snakeBody = initialBody,
            currentDirection = Direction.UP,
            queuedDirection = Direction.UP,
            fruits = initialFruits,
            rivals = rivals,
            rivalsDefeated = 0,
            tamilPunchline = tamilPunchlines.random()
        )

        soundManager.playTurnClick()
        startGameLoop()

        if (mode == GameMode.TIME_ATTACK) {
            startTimeAttackCountdown()
        }
    }

    private fun spawnInitialFruits(
        snake: List<GridPoint>,
        rivals: List<RivalSnake>
    ): List<ActiveFruit> {
        val occupied = snake.toMutableSet()
        rivals.forEach { occupied.addAll(it.body) }

        val fruits = mutableListOf<ActiveFruit>()
        // Always 1 normal apple
        getRandomEmptyPoint(occupied, 20, 26)?.let {
            fruits.add(ActiveFruit(it, FruitType.APPLE))
            occupied.add(it)
        }
        // 1 special fruit (Watermelon, Banana, or Mango)
        getRandomEmptyPoint(occupied, 20, 26)?.let {
            val specialType = listOf(FruitType.WATERMELON, FruitType.BANANA, FruitType.WILD_BERRY).random()
            fruits.add(ActiveFruit(it, specialType))
        }
        return fruits
    }

    private fun getRandomEmptyPoint(occupied: Set<GridPoint>, w: Int, h: Int): GridPoint? {
        val candidates = mutableListOf<GridPoint>()
        for (x in 1 until w - 1) {
            for (y in 1 until h - 1) {
                val pt = GridPoint(x, y)
                if (!occupied.contains(pt)) {
                    candidates.add(pt)
                }
            }
        }
        return candidates.randomOrNull()
    }

    fun onDirectionChanged(newDirection: Direction) {
        val current = _gameState.value.currentDirection
        val queued = _gameState.value.queuedDirection
        // Prevent 180-degree instant reversal
        if (!newDirection.isOpposite(current) && !newDirection.isOpposite(queued)) {
            _gameState.update { it.copy(queuedDirection = newDirection) }
            soundManager.playTurnClick()
        }
    }

    fun setTurbo(active: Boolean) {
        _gameState.update { it.copy(isTurboActive = active) }
    }

    fun pauseGame() {
        if (_gameState.value.status == GameStatus.PLAYING) {
            _gameState.update { it.copy(status = GameStatus.PAUSED) }
        }
    }

    fun resumeGame() {
        if (_gameState.value.status == GameStatus.PAUSED) {
            _gameState.update { it.copy(status = GameStatus.PLAYING) }
        }
    }

    private fun startTimeAttackCountdown() {
        timeAttackJob?.cancel()
        timeAttackJob = viewModelScope.launch {
            while (isActive && _gameState.value.status == GameStatus.PLAYING) {
                delay(1000L)
                if (_gameState.value.status == GameStatus.PLAYING) {
                    val remaining = _gameState.value.remainingTimeSeconds - 1
                    if (remaining <= 0) {
                        endGame("Time's up! Minnal clock expired.")
                        break
                    } else {
                        _gameState.update { it.copy(remainingTimeSeconds = remaining) }
                    }
                }
            }
        }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (isActive) {
                val state = _gameState.value
                if (state.status == GameStatus.PLAYING) {
                    val baseDelay = state.difficulty.tickDelayMs
                    val speedFactor = state.currentSpeedMultiplier
                    val actualDelay = (baseDelay / speedFactor).toLong().coerceAtLeast(35L)

                    stepGame()
                    delay(actualDelay)
                } else {
                    delay(100L)
                }
            }
        }
    }

    private fun stepGame() {
        val state = _gameState.value
        val dir = state.queuedDirection
        val head = state.snakeBody.first()
        var newHead = head.offset(dir.dx, dir.dy)

        // Wall wrapping or collision
        var fatalWall = false
        if (newHead.x < 0 || newHead.x >= state.boardWidth || newHead.y < 0 || newHead.y >= state.boardHeight) {
            if (state.isInvincible) {
                // Golden shield wraps around
                val wrappedX = (newHead.x + state.boardWidth) % state.boardWidth
                val wrappedY = (newHead.y + state.boardHeight) % state.boardHeight
                newHead = GridPoint(wrappedX, wrappedY)
            } else {
                fatalWall = true
            }
        }

        if (fatalWall) {
            endGame("Crashed into the jungle stone border!")
            return
        }

        // Self-collision
        val bodyWithoutTail = if (state.pendingGrowth > 0) state.snakeBody else state.snakeBody.dropLast(1)
        if (bodyWithoutTail.contains(newHead)) {
            if (!state.isInvincible) {
                endGame("Ouch! Bit your own royal tail!")
                return
            }
        }

        // Rival collision (Player hitting Rival)
        var rivalCrushedIndex = -1
        for (i in state.rivals.indices) {
            val rival = state.rivals[i]
            if (rival.isAlive && rival.body.contains(newHead)) {
                if (state.isInvincible) {
                    rivalCrushedIndex = i
                } else {
                    endGame("Clashed into rival snake ${rival.name}!")
                    return
                }
            }
        }

        // Fruit collection check
        var fruitEaten: ActiveFruit? = null
        val updatedFruits = state.fruits.toMutableList()
        for (fruit in state.fruits) {
            if (fruit.position == newHead) {
                fruitEaten = fruit
                updatedFruits.remove(fruit)
                break
            }
        }

        var newScore = state.score
        var newFruitsEaten = state.fruitsEaten
        var newGrowth = state.pendingGrowth
        var newShieldTicks = (state.shieldTicksRemaining - 1).coerceAtLeast(0)
        var newSpeedTicks = (state.speedBoostTicksRemaining - 1).coerceAtLeast(0)
        var newDoublePointsTicks = (state.doublePointsTicksRemaining - 1).coerceAtLeast(0)
        var newFireTicks = (state.fireTrailTicksRemaining - 1).coerceAtLeast(0)
        var newComboTimer = (state.comboTimerTicks - 1).coerceAtLeast(0)
        var newComboMult = if (newComboTimer == 0) 1 else state.comboMultiplier
        var bonusTime = 0

        val newPopups = mutableListOf<ScorePopup>()

        if (fruitEaten != null) {
            soundManager.playEatFruit()
            newFruitsEaten += 1
            newGrowth += fruitEaten.type.growth

            // Combo multiplier increment
            newComboMult = (newComboMult + 1).coerceAtMost(5)
            newComboTimer = 25 // ~3-4 seconds

            var pts = fruitEaten.type.points * newComboMult
            if (newDoublePointsTicks > 0) pts *= 2
            pts = (pts * state.difficulty.scoreMultiplier).toInt()

            newScore += pts
            newPopups.add(ScorePopup(text = "+$pts", position = newHead))

            // Activate fruit special power-ups
            when (fruitEaten.type) {
                FruitType.BANANA -> {
                    newSpeedTicks = fruitEaten.type.durationTicks
                    soundManager.playPowerUp()
                }
                FruitType.WILD_BERRY -> {
                    newDoublePointsTicks = fruitEaten.type.durationTicks
                    soundManager.playPowerUp()
                }
                FruitType.GOLDEN_MANGO -> {
                    newShieldTicks = fruitEaten.type.durationTicks
                    soundManager.playPowerUp()
                }
                FruitType.CHILLI_PEPPER -> {
                    newFireTicks = fruitEaten.type.durationTicks
                    soundManager.playPowerUp()
                }
                FruitType.WATERMELON -> {
                    // Extra bursts
                    soundManager.playPowerUp()
                }
                FruitType.APPLE -> {
                    // Standard
                }
            }

            if (state.mode == GameMode.TIME_ATTACK) {
                bonusTime = 3 // Extra 3 seconds on fruit eaten!
            }
        }

        // Build new body
        val newBody = mutableListOf(newHead)
        if (newGrowth > 0) {
            newBody.addAll(state.snakeBody)
            newGrowth -= 1
        } else {
            newBody.addAll(state.snakeBody.dropLast(1))
        }

        // Update Rivals in Arena Mode
        var rivalsDefeatedCount = state.rivalsDefeated
        val updatedRivals = state.rivals.mapIndexed { idx, rival ->
            if (!rival.isAlive || idx == rivalCrushedIndex) {
                if (idx == rivalCrushedIndex) rivalsDefeatedCount += 1
                rival.copy(isAlive = false)
            } else {
                val nextDir = rival.nextMove(
                    state.boardWidth,
                    state.boardHeight,
                    updatedFruits,
                    newBody,
                    state.rivals
                )
                val rHead = rival.body.first().offset(nextDir.dx, nextDir.dy)

                // Rival crash into player body or wall
                val isCrash = rHead.x < 0 || rHead.x >= state.boardWidth ||
                        rHead.y < 0 || rHead.y >= state.boardHeight ||
                        newBody.contains(rHead) ||
                        rival.body.dropLast(1).contains(rHead)

                if (isCrash) {
                    rivalsDefeatedCount += 1
                    newScore += 50
                    newPopups.add(ScorePopup(text = "+50 RIVAL DEFEATED!", position = rival.body.first()))
                    soundManager.playPowerUp()
                    // Drop fruits where rival died
                    rival.body.take(2).forEach { pt ->
                        updatedFruits.add(ActiveFruit(pt, FruitType.APPLE))
                    }
                    rival.copy(isAlive = false)
                } else {
                    val rNewBody = listOf(rHead) + rival.body.dropLast(1)
                    rival.copy(body = rNewBody, direction = nextDir)
                }
            }
        }

        // Spawn replacement fruits if needed
        val occupied = (newBody + updatedRivals.filter { it.isAlive }.flatMap { it.body }).toSet()
        if (updatedFruits.size < 2) {
            getRandomEmptyPoint(occupied, state.boardWidth, state.boardHeight)?.let { pt ->
                val roll = Random.nextInt(100)
                val type = when {
                    roll < 45 -> FruitType.APPLE
                    roll < 65 -> FruitType.WATERMELON
                    roll < 80 -> FruitType.BANANA
                    roll < 90 -> FruitType.WILD_BERRY
                    roll < 96 -> FruitType.CHILLI_PEPPER
                    else -> FruitType.GOLDEN_MANGO
                }
                updatedFruits.add(ActiveFruit(pt, type))
            }
        }

        _gameState.update {
            it.copy(
                snakeBody = newBody,
                currentDirection = dir,
                score = newScore,
                highScore = maxOf(it.highScore, newScore),
                fruitsEaten = newFruitsEaten,
                pendingGrowth = newGrowth,
                fruits = updatedFruits,
                rivals = updatedRivals,
                rivalsDefeated = rivalsDefeatedCount,
                shieldTicksRemaining = newShieldTicks,
                speedBoostTicksRemaining = newSpeedTicks,
                doublePointsTicksRemaining = newDoublePointsTicks,
                fireTrailTicksRemaining = newFireTicks,
                comboMultiplier = newComboMult,
                comboTimerTicks = newComboTimer,
                popups = newPopups,
                remainingTimeSeconds = if (it.mode == GameMode.TIME_ATTACK) it.remainingTimeSeconds + bonusTime else 0
            )
        }
    }

    private fun endGame(reason: String) {
        gameLoopJob?.cancel()
        timeAttackJob?.cancel()
        soundManager.playGameOver()

        val state = _gameState.value
        val punchline = tamilPunchlines.random()

        _gameState.update {
            it.copy(
                status = GameStatus.GAME_OVER,
                gameOverReason = reason,
                tamilPunchline = punchline
            )
        }

        // Persist score and update player career stats
        viewModelScope.launch {
            repository.recordGameFinished(
                playerName = "Pavi King",
                score = state.score,
                fruitsEaten = state.fruitsEaten,
                snakeLength = state.snakeBody.size,
                mode = state.mode,
                difficulty = state.difficulty.title,
                rivalsDefeated = state.rivalsDefeated
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        timeAttackJob?.cancel()
        soundManager.release()
    }
}

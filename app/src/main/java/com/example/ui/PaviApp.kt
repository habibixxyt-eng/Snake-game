package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.GameMode
import com.example.model.GameStatus
import com.example.ui.screens.*
import com.example.ui.theme.JungleDark
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    GAME,
    LEADERBOARD,
    SKINS,
    LORE,
    SETTINGS
}

@Composable
fun PaviApp(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    val scope = rememberCoroutineScope()

    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
    val controlMode by viewModel.controlMode.collectAsStateWithLifecycle()
    val playerStats by viewModel.playerStats.collectAsStateWithLifecycle()
    val highScores by viewModel.repository.getAllScores().collectAsStateWithLifecycle(initialValue = emptyList())

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        color = JungleDark
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "screen_transition"
        ) { targetScreen ->
            when (targetScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        gameState = gameState,
                        playerStats = playerStats,
                        onStartGame = { mode, diff ->
                            viewModel.startNewGame(mode, diff)
                            currentScreen = AppScreen.GAME
                        },
                        onNavigateToLeaderboard = { currentScreen = AppScreen.LEADERBOARD },
                        onNavigateToSkins = { currentScreen = AppScreen.SKINS },
                        onNavigateToLore = { currentScreen = AppScreen.LORE },
                        onNavigateToSettings = { currentScreen = AppScreen.SETTINGS }
                    )
                }
                AppScreen.GAME -> {
                    GameScreen(
                        gameState = gameState,
                        onDirectionChanged = { viewModel.onDirectionChanged(it) },
                        onTurboChanged = { viewModel.setTurbo(it) },
                        onPauseGame = { viewModel.pauseGame() },
                        onResumeGame = { viewModel.resumeGame() },
                        onRestartGame = { viewModel.startNewGame() },
                        onExitToHome = {
                            viewModel.pauseGame()
                            currentScreen = AppScreen.HOME
                        }
                    )
                }
                AppScreen.LEADERBOARD -> {
                    LeaderboardScreen(
                        scores = highScores,
                        playerStats = playerStats,
                        onClearScores = {
                            scope.launch { viewModel.repository.clearAllScores() }
                        },
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }
                AppScreen.SKINS -> {
                    SkinsScreen(
                        currentSkin = gameState.selectedSkin,
                        playerStats = playerStats,
                        onSelectSkin = { viewModel.selectSkin(it) },
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }
                AppScreen.LORE -> {
                    LoreScreen(
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }
                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        soundManager = viewModel.soundManager,
                        controlMode = controlMode,
                        onControlModeChanged = { viewModel.setControlMode(it) },
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }
            }
        }
    }
}

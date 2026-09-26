package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.SnakeBoardCanvas
import com.example.ui.components.VirtualControls
import com.example.ui.theme.*

@Composable
fun GameScreen(
    gameState: GameState,
    onDirectionChanged: (Direction) -> Unit,
    onTurboChanged: (Boolean) -> Unit,
    onPauseGame: () -> Unit,
    onResumeGame: () -> Unit,
    onRestartGame: () -> Unit,
    onExitToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (gameState.status == GameStatus.PLAYING) {
            onPauseGame()
        } else {
            onExitToHome()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(JungleDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top HUD Bar
            GameTopHud(
                gameState = gameState,
                onPauseGame = onPauseGame,
                onExitToHome = onExitToHome
            )

            // 2. Active Powerups Strip
            PowerUpsIndicatorBar(gameState = gameState)

            // 3. Central Canvas Playfield
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                SnakeBoardCanvas(
                    gameState = gameState,
                    onDirectionChanged = onDirectionChanged,
                    modifier = Modifier.fillMaxHeight()
                )
            }

            // 4. Virtual Controls (D-Pad + Turbo Dash)
            VirtualControls(
                onDirectionChanged = onDirectionChanged,
                onTurboChanged = onTurboChanged,
                isTurboActive = gameState.isTurboActive
            )
        }

        // Pause Dialog
        if (gameState.status == GameStatus.PAUSED) {
            PauseGameDialog(
                onResume = onResumeGame,
                onRestart = onRestartGame,
                onExit = onExitToHome
            )
        }

        // Game Over Dialog
        if (gameState.status == GameStatus.GAME_OVER) {
            GameOverDialog(
                gameState = gameState,
                onPlayAgain = onRestartGame,
                onExit = onExitToHome
            )
        }
    }
}

@Composable
private fun GameTopHud(
    gameState: GameState,
    onPauseGame: () -> Unit,
    onExitToHome: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Back/Exit & Mode
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onExitToHome,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(JungleCard)
                    .testTag("game_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Exit to Home",
                    tint = CobraGreenLight
                )
            }

            Column {
                Text(
                    text = gameState.mode.title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = SurfaceText
                )
                if (gameState.mode == GameMode.TIME_ATTACK) {
                    Text(
                        text = "${gameState.remainingTimeSeconds}s LEFT",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = if (gameState.remainingTimeSeconds <= 15) RubyRed else RoyalGold
                    )
                } else {
                    Text(
                        text = gameState.difficulty.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = SurfaceTextMuted
                    )
                }
            }
        }

        // Center: Score & Combo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${gameState.score}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = RoyalGold
            )
            if (gameState.comboMultiplier > 1) {
                Text(
                    text = "${gameState.comboMultiplier}x COMBO!",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = CobraGreenLight
                )
            } else {
                Text(
                    text = "BEST: ${gameState.highScore}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SurfaceTextMuted
                )
            }
        }

        // Right: Fruits Eaten & Pause
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .background(JungleCard, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalDining,
                    contentDescription = null,
                    tint = RubyRed,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${gameState.fruitsEaten}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            IconButton(
                onClick = onPauseGame,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(JungleCard)
                    .testTag("game_pause_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause Game",
                    tint = RoyalGold
                )
            }
        }
    }
}

@Composable
private fun PowerUpsIndicatorBar(gameState: GameState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (gameState.shieldTicksRemaining > 0) {
            ActivePowerUpBadge(
                label = "GOLDEN SHIELD",
                icon = Icons.Default.Shield,
                color = RoyalGold,
                ticks = gameState.shieldTicksRemaining
            )
        }
        if (gameState.fireTrailTicksRemaining > 0) {
            ActivePowerUpBadge(
                label = "FIRE DASH",
                icon = Icons.Default.LocalFireDepartment,
                color = RubyRed,
                ticks = gameState.fireTrailTicksRemaining
            )
        }
        if (gameState.speedBoostTicksRemaining > 0) {
            ActivePowerUpBadge(
                label = "SPEED BURST",
                icon = Icons.Default.FastForward,
                color = ElectricCyan,
                ticks = gameState.speedBoostTicksRemaining
            )
        }
        if (gameState.doublePointsTicksRemaining > 0) {
            ActivePowerUpBadge(
                label = "2x BERRIES",
                icon = Icons.Default.Star,
                color = MysticPurple,
                ticks = gameState.doublePointsTicksRemaining
            )
        }
    }
}

@Composable
private fun ActivePowerUpBadge(
    label: String,
    icon: ImageVector,
    color: Color,
    ticks: Int
) {
    Surface(
        modifier = Modifier.padding(horizontal = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.2f),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(color, Color.White)))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Text(
                text = "$label (${ticks / 5}s)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun PauseGameDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = JungleSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(RoyalGold, CobraGreen))),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PauseCircle,
                    contentDescription = null,
                    tint = RoyalGold,
                    modifier = Modifier.size(54.dp)
                )

                Text(
                    text = "GAME PAUSED",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )

                Text(
                    text = "Catch your breath! The jungle awaits your next strike.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SurfaceTextMuted,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("resume_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CobraGreen, contentColor = Color.Black)
                ) {
                    Text("RESUME SLITHERING", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("restart_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("RESTART GAME", color = SurfaceText)
                }

                TextButton(
                    onClick = onExit,
                    modifier = Modifier.testTag("exit_to_home_button")
                ) {
                    Text("EXIT TO MENU", color = RubyRed)
                }
            }
        }
    }
}

@Composable
private fun GameOverDialog(
    gameState: GameState,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    val isNewRecord = gameState.score > 0 && gameState.score >= gameState.highScore

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = JungleSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    if (isNewRecord) listOf(RoyalGold, GoldenSun) else listOf(RubyRed, CrimsonDeep)
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (isNewRecord) RoyalGold.copy(alpha = 0.2f) else RubyRed.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isNewRecord) Icons.Default.EmojiEvents else Icons.Default.Dangerous,
                        contentDescription = null,
                        tint = if (isNewRecord) RoyalGold else RubyRed,
                        modifier = Modifier.size(36.dp)
                    )
                }

                if (isNewRecord) {
                    Text(
                        text = "NEW HIGH SCORE!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = RoyalGold
                    )
                }

                Text(
                    text = "GAME OVER",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )

                Text(
                    text = gameState.gameOverReason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = RubyRed,
                    textAlign = TextAlign.Center
                )

                // Punchline
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = JungleCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\"${gameState.tamilPunchline}\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AmberGlow,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "SCORE", style = MaterialTheme.typography.labelSmall, color = SurfaceTextMuted)
                        Text(
                            text = "${gameState.score}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = RoyalGold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "FRUITS", style = MaterialTheme.typography.labelSmall, color = SurfaceTextMuted)
                        Text(
                            text = "${gameState.fruitsEaten}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = CobraGreenLight
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "LENGTH", style = MaterialTheme.typography.labelSmall, color = SurfaceTextMuted)
                        Text(
                            text = "${gameState.snakeBody.size}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = ElectricCyan
                        )
                    }
                }

                // Buttons
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("play_again_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CobraGreen, contentColor = Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PLAY AGAIN", fontWeight = FontWeight.Black)
                }

                OutlinedButton(
                    onClick = onExit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("game_over_home_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("HOME MENU", color = SurfaceText)
                }
            }
        }
    }
}

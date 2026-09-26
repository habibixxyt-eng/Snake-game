package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PlayerStatsEntity
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    gameState: GameState,
    playerStats: PlayerStatsEntity?,
    onStartGame: (GameMode, Difficulty) -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToSkins: () -> Unit,
    onNavigateToLore: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf(GameMode.CLASSIC) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.CASUAL) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(JungleDark),
        contentPadding = PaddingValues(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Image Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                val context = LocalContext.current
                val bannerResId = remember {
                    context.resources.getIdentifier("pavi_hero_banner", "drawable", context.packageName)
                }

                if (bannerResId != 0) {
                    Image(
                        painter = painterResource(id = bannerResId),
                        contentDescription = "Pavi Endra Paambu Hero Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(CobraGreenDark, JungleDark)
                                )
                            )
                    )
                }

                // Dark gradient overlay to blend into UI
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    JungleDark.copy(alpha = 0.85f),
                                    JungleDark
                                )
                            )
                        )
                )

                // Top Settings Action
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(JungleDark.copy(alpha = 0.6f), CircleShape)
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = CobraGreenLight
                    )
                }

                // Title Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "PAVI ENDRA PAAMBU",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = RoyalGold
                        )
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "THE REIGN OF THE COBRA KING",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 3.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CobraGreenLight
                    )
                }
            }
        }

        // Tamil Punchline Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = JungleCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RoyalGold, CobraGreen)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(RoyalGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "\"${gameState.tamilPunchline}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = AmberGlow
                        )
                        Text(
                            text = "— King Cobra Pavi",
                            style = MaterialTheme.typography.labelSmall,
                            color = SurfaceTextMuted
                        )
                    }
                }
            }
        }

        // Quick Stats Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatPill(
                    label = "High Score",
                    value = "${playerStats?.highestScore ?: gameState.highScore}",
                    icon = Icons.Default.Star,
                    tint = RoyalGold,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "Longest Snake",
                    value = "${playerStats?.longestSnakeLength ?: 3} seg",
                    icon = Icons.Default.Straighten,
                    tint = CobraGreenLight,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "Fruits Eaten",
                    value = "${playerStats?.totalFruitsEaten ?: 0}",
                    icon = Icons.Default.LocalDining,
                    tint = RubyRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Mode Selector Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "SELECT ARENA MODE",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = SurfaceText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameMode.values().forEach { mode ->
                        val isSelected = selectedMode == mode
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMode = mode }
                                .testTag("mode_${mode.name.lowercase()}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) CobraGreenDark else JungleCard
                            ),
                            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(RoyalGold, CobraGreenLight))) else null
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = when (mode) {
                                        GameMode.CLASSIC -> Icons.Default.SportsEsports
                                        GameMode.ARENA_SURVIVAL -> Icons.Default.Shield
                                        GameMode.TIME_ATTACK -> Icons.Default.Timer
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) AmberGlow else SurfaceTextMuted,
                                    modifier = Modifier.size(26.dp)
                                )
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.White else SurfaceText,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Mode brief description
                Text(
                    text = selectedMode.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SurfaceTextMuted,
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                )
            }
        }

        // Difficulty Selector Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "SPEED & DIFFICULTY",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = SurfaceText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Difficulty.values().forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDifficulty = diff },
                            label = {
                                Text(
                                    text = diff.title,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalGold,
                                selectedLabelColor = Color.Black,
                                containerColor = JungleCard,
                                labelColor = SurfaceText
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("diff_${diff.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Big Primary Play Button
        item {
            Button(
                onClick = { onStartGame(selectedMode, selectedDifficulty) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(60.dp)
                    .testTag("play_game_button"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CobraGreen,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                    Text(
                        text = "ENTER JUNGLE ARENA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }

        // Secondary Navigation Grid
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NavCard(
                    title = "Snake Skins",
                    subtitle = "${gameState.selectedSkin.skinName}",
                    icon = Icons.Default.AutoFixHigh,
                    accentColor = CobraGreenLight,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSkins,
                    testTag = "nav_skins"
                )
                NavCard(
                    title = "Leaderboards",
                    subtitle = "High Scores",
                    icon = Icons.Default.Leaderboard,
                    accentColor = RoyalGold,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLeaderboard,
                    testTag = "nav_leaderboard"
                )
                NavCard(
                    title = "Paambu Lore",
                    subtitle = "Fruits Guide",
                    icon = Icons.Default.MenuBook,
                    accentColor = ElectricCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLore,
                    testTag = "nav_lore"
                )
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = JungleCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = SurfaceTextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun NavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = JungleCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = SurfaceText,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = SurfaceTextMuted,
                maxLines = 1,
                fontSize = 10.sp
            )
        }
    }
}

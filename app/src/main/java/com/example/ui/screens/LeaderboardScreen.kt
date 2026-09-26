package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameScoreEntity
import com.example.data.PlayerStatsEntity
import com.example.model.GameMode
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    scores: List<GameScoreEntity>,
    playerStats: PlayerStatsEntity?,
    onClearScores: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var selectedTab by remember { mutableStateOf("ALL") }
    var showClearConfirm by remember { mutableStateOf(false) }

    val filteredScores = remember(scores, selectedTab) {
        when (selectedTab) {
            "ALL" -> scores
            else -> scores.filter { it.gameMode == selectedTab }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = JungleDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "JUNGLE HALL OF FAME",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = RoyalGold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("leaderboard_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = CobraGreenLight
                        )
                    }
                },
                actions = {
                    if (scores.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearConfirm = true },
                            modifier = Modifier.testTag("clear_scores_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear Records",
                                tint = RubyRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = JungleDark,
                    titleContentColor = RoyalGold
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Career Stats Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = JungleSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RoyalGold, CobraGreen)))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = RoyalGold)
                            Text(
                                text = "COBRA CAREER RECORDS",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = RoyalGold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CareerStatItem("Games Played", "${playerStats?.totalGamesPlayed ?: 0}")
                            CareerStatItem("Best Record", "${playerStats?.highestScore ?: 0}")
                            CareerStatItem("Max Length", "${playerStats?.longestSnakeLength ?: 3}")
                            CareerStatItem("Rivals Crushed", "${playerStats?.totalRivalsDefeated ?: 0}")
                        }
                    }
                }
            }

            // Tabs for Game Mode
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL", GameMode.CLASSIC.name, GameMode.ARENA_SURVIVAL.name, GameMode.TIME_ATTACK.name).forEach { modeKey ->
                        val isSelected = selectedTab == modeKey
                        val label = when (modeKey) {
                            "ALL" -> "All Modes"
                            GameMode.CLASSIC.name -> "Classic"
                            GameMode.ARENA_SURVIVAL.name -> "Arena"
                            GameMode.TIME_ATTACK.name -> "Rush"
                            else -> modeKey
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTab = modeKey },
                            label = { Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CobraGreen,
                                selectedLabelColor = Color.Black,
                                containerColor = JungleCard,
                                labelColor = SurfaceText
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Score Rows
            if (filteredScores.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = SurfaceTextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No records yet in this mode!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SurfaceTextMuted
                            )
                            Text(
                                text = "Enter the jungle and set the first legend.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CobraGreenLight
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(filteredScores) { index, entry ->
                    ScoreEntryCard(rank = index + 1, entry = entry)
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear High Scores?") },
            text = { Text("Are you sure you want to reset all leaderboard records? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearScores()
                        showClearConfirm = false
                    }
                ) {
                    Text("CLEAR ALL", color = RubyRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
private fun CareerStatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = Color.White)
        Text(text = title, style = MaterialTheme.typography.labelSmall, color = SurfaceTextMuted, fontSize = 9.sp)
    }
}

@Composable
private fun ScoreEntryCard(rank: Int, entry: GameScoreEntity) {
    val rankBadgeColor = when (rank) {
        1 -> RoyalGold
        2 -> Color(0xFFE2E8F0)
        3 -> Color(0xFFCD7F32)
        else -> JungleCardBorder
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val dateStr = remember(entry.playedAt) { dateFormatter.format(Date(entry.playedAt)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = JungleCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(rankBadgeColor.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                    color = if (rank <= 3) rankBadgeColor else SurfaceText
                )
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = entry.playerName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "• ${entry.difficulty}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CobraGreenLight
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "🍎 ${entry.fruitsEaten} fruits",
                        style = MaterialTheme.typography.labelSmall,
                        color = SurfaceTextMuted
                    )
                    Text(
                        text = "🐍 ${entry.snakeLength} seg",
                        style = MaterialTheme.typography.labelSmall,
                        color = SurfaceTextMuted
                    )
                    if (entry.rivalsDefeated > 0) {
                        Text(
                            text = "⚔️ ${entry.rivalsDefeated} rivals",
                            style = MaterialTheme.typography.labelSmall,
                            color = RubyRed
                        )
                    }
                }

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = SurfaceTextMuted.copy(alpha = 0.7f),
                    fontSize = 9.sp
                )
            }

            // Score Number
            Text(
                text = "${entry.score}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = if (rank == 1) RoyalGold else CobraGreenLight
            )
        }
    }
}

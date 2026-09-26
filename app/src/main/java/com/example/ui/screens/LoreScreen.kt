package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FruitType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoreScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = JungleDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "THE PAAMBU CHRONICLES",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = RoyalGold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("lore_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = CobraGreenLight)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // The Legend Story
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = JungleSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RoyalGold, CobraGreen))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AutoStories, contentDescription = null, tint = RoyalGold)
                            Text(
                                text = "THE LEGEND OF PAVI",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = RoyalGold
                            )
                        }

                        Text(
                            text = "Deep inside the sacred misty hills of Tamil Nadu, there rules a mythical serpent unlike any other — 'Pavi Endra Paambu'. Crowned with the eternal ruby tiara, Pavi does not hunt with venom, but with supreme speed, slithering through ancient stone temples and lush tropical canopies to feast upon enchanted fruits!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SurfaceText,
                            lineHeight = 22.sp
                        )

                        Text(
                            text = "\"Pavi Endra Paambu varaaru, ellarum vazhi vidu!\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = AmberGlow
                        )
                    }
                }
            }

            // Fruit & Power-Ups Guide
            item {
                Text(
                    text = "ENCHANTED JUNGLE FRUITS",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                    color = CobraGreenLight,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            items(FruitType.values().toList()) { fruit ->
                FruitGuideCard(fruit = fruit)
            }

            // Punchlines Collection
            item {
                Text(
                    text = "COBRA PUNCHLINES",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                    color = RoyalGold,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PunchlineCard("Naan oru thadava saapta, nooru thadava valarvaen!", "If I eat once, I grow a hundred times!")
                    PunchlineCard("Pavi Endra Paambu eppodhum tharkolai seiyaadhu!", "The King Cobra never gives up without a fight!")
                    PunchlineCard("Vera level slither! Unnai velva mudiyadhu!", "Unstoppable slithering mastery across the arena!")
                }
            }
        }
    }
}

@Composable
private fun FruitGuideCard(fruit: FruitType) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = JungleCard),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(JungleSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (fruit) {
                        FruitType.APPLE -> "🍎"
                        FruitType.WATERMELON -> "🍉"
                        FruitType.BANANA -> "🍌"
                        FruitType.WILD_BERRY -> "🍇"
                        FruitType.GOLDEN_MANGO -> "🥭"
                        FruitType.CHILLI_PEPPER -> "🌶️"
                    },
                    fontSize = 24.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = fruit.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "(${fruit.tamilName})",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberGlow
                    )
                }

                Text(
                    text = fruit.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SurfaceTextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "+${fruit.points} pts",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CobraGreenLight
                    )
                    Text(
                        text = "+${fruit.growth} growth",
                        style = MaterialTheme.typography.labelSmall,
                        color = SurfaceTextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun PunchlineCard(tamil: String, english: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = JungleCard),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "\"$tamil\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = AmberGlow
            )
            Text(
                text = english,
                style = MaterialTheme.typography.labelSmall,
                color = SurfaceTextMuted,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

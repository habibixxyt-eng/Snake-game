package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlayerStatsEntity
import com.example.model.SnakeSkin
import com.example.ui.theme.*
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinsScreen(
    currentSkin: SnakeSkin,
    playerStats: PlayerStatsEntity?,
    onSelectSkin: (SnakeSkin) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val highestScore = playerStats?.highestScore ?: 0
    val unlockedList = remember(playerStats?.unlockedSkins) {
        playerStats?.unlockedSkins?.split(",")?.toSet() ?: setOf("raja_pavi")
    }

    var previewSkin by remember { mutableStateOf(currentSkin) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = JungleDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SNAKE WARDROBE & SKINS",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = RoyalGold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("skins_back_button")
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Interactive Slithering Preview
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = JungleSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(previewSkin.headColor, RoyalGold)))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LiveSnakePreviewCanvas(skin = previewSkin)

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = previewSkin.skinName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                            Text(
                                text = previewSkin.tamilTitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberGlow
                            )
                        }
                    }
                }
            }

            // Skin Cards List
            items(SnakeSkin.values().toList()) { skin ->
                val isUnlocked = unlockedList.contains(skin.id) || highestScore >= skin.unlockScore
                val isSelected = currentSkin == skin

                SkinCard(
                    skin = skin,
                    isUnlocked = isUnlocked,
                    isSelected = isSelected,
                    highestScore = highestScore,
                    onPreview = { previewSkin = skin },
                    onEquip = {
                        previewSkin = skin
                        onSelectSkin(skin)
                    }
                )
            }
        }
    }
}

@Composable
private fun LiveSnakePreviewCanvas(skin: SnakeSkin) {
    val infiniteTransition = rememberInfiniteTransition(label = "slither")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "slitherPhase"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2
        val cy = size.height / 2 - 10f
        val segCount = 10
        val segRadius = 14f

        for (i in (segCount - 1) downTo 0) {
            val progress = i.toFloat() / segCount.toFloat()
            val x = cx + (5 - i) * 22f
            val wave = sin(phase + i * 0.5f) * 18f
            val y = cy + wave

            val radius = if (i == 0) segRadius * 1.35f else segRadius * (1f - progress * 0.35f)
            val color = if (i == 0) skin.headColor else skin.bodyColor

            drawCircle(
                color = color,
                radius = radius,
                center = Offset(x, y)
            )

            // Inner belly
            if (i > 0) {
                drawCircle(
                    color = skin.underbellyColor.copy(alpha = 0.6f),
                    radius = radius * 0.45f,
                    center = Offset(x, y)
                )
            }
        }

        // Head eyes
        val headX = cx + 5 * 22f
        val headY = cy + sin(phase) * 18f
        val eyeR = 4.5f

        drawCircle(color = Color.White, radius = eyeR, center = Offset(headX + 4f, headY - 5f))
        drawCircle(color = Color.White, radius = eyeR, center = Offset(headX + 4f, headY + 5f))
        drawCircle(color = skin.eyeColor, radius = eyeR * 0.6f, center = Offset(headX + 5f, headY - 5f))
        drawCircle(color = skin.eyeColor, radius = eyeR * 0.6f, center = Offset(headX + 5f, headY + 5f))

        // Crown
        if (skin.hasCrown) {
            drawCircle(
                color = skin.crownColor,
                radius = 8f,
                center = Offset(headX - 6f, headY)
            )
            drawCircle(
                color = RubyRed,
                radius = 3f,
                center = Offset(headX - 6f, headY)
            )
        }
    }
}

@Composable
private fun SkinCard(
    skin: SnakeSkin,
    isUnlocked: Boolean,
    isSelected: Boolean,
    highestScore: Int,
    onPreview: () -> Unit,
    onEquip: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("skin_card_${skin.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CobraGreenDark.copy(alpha = 0.5f) else JungleCard
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RoyalGold, CobraGreenLight))) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Color Palette Pill
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(skin.headColor),
                contentAlignment = Alignment.Center
            ) {
                if (skin.hasCrown) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = skin.crownColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(skin.bodyColor)
                    )
                }
            }

            // Description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skin.skinName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = skin.tamilTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberGlow
                )
                Text(
                    text = skin.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SurfaceTextMuted,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (!isUnlocked) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(12.dp))
                        Text(
                            text = "Reach High Score of ${skin.unlockScore} (Current: $highestScore)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = RoyalGold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Action
            if (isUnlocked) {
                if (isSelected) {
                    FilledTonalButton(
                        onClick = onPreview,
                        enabled = false,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            disabledContainerColor = RoyalGold,
                            disabledContentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("EQUIPPED", fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = onEquip,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CobraGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("equip_skin_${skin.id}")
                    ) {
                        Text("EQUIP", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            } else {
                IconButton(
                    onClick = onPreview,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(JungleDark)
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = "Preview", tint = SurfaceTextMuted)
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SnakeBoardCanvas(
    gameState: GameState,
    onDirectionChanged: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulsing animation for golden shield and fruits
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val tongueFlick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(250, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tongueFlick"
    )

    Box(
        modifier = modifier
            .aspectRatio(gameState.boardWidth.toFloat() / gameState.boardHeight.toFloat())
            .clip(RoundedCornerShape(16.dp))
            .background(JungleDark)
            .pointerInput(Unit) {
                var accumulatedX = 0f
                var accumulatedY = 0f
                detectDragGestures(
                    onDragStart = {
                        accumulatedX = 0f
                        accumulatedY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        accumulatedX += dragAmount.x
                        accumulatedY += dragAmount.y
                    },
                    onDragEnd = {
                        val threshold = 25f
                        if (abs(accumulatedX) > abs(accumulatedY)) {
                            if (abs(accumulatedX) > threshold) {
                                if (accumulatedX > 0) onDirectionChanged(Direction.RIGHT)
                                else onDirectionChanged(Direction.LEFT)
                            }
                        } else {
                            if (abs(accumulatedY) > threshold) {
                                if (accumulatedY > 0) onDirectionChanged(Direction.DOWN)
                                else onDirectionChanged(Direction.UP)
                            }
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / gameState.boardWidth
            val cellH = size.height / gameState.boardHeight

            // 1. Draw Jungle Grid Pattern
            drawJungleGrid(gameState.boardWidth, gameState.boardHeight, cellW, cellH)

            // 2. Draw Active Fruits
            for (fruit in gameState.fruits) {
                drawFruitItem(fruit, cellW, cellH, pulseScale)
            }

            // 3. Draw Rival Snakes (Arena Mode)
            for (rival in gameState.rivals) {
                if (rival.isAlive) {
                    drawRivalSnake(rival, cellW, cellH)
                }
            }

            // 4. Draw Player Snake (Pavi)
            drawPlayerSnake(
                snake = gameState.snakeBody,
                skin = gameState.selectedSkin,
                dir = gameState.currentDirection,
                cellW = cellW,
                cellH = cellH,
                isInvincible = gameState.isInvincible,
                isFireActive = gameState.fireTrailTicksRemaining > 0,
                isTurbo = gameState.isTurboActive,
                tonguePhase = tongueFlick
            )

            // 5. Draw Board Border Glow
            drawRoundRect(
                brush = Brush.linearGradient(
                    listOf(CobraGreenDark, RoyalGold, CobraGreen)
                ),
                size = size,
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 4f)
            )
        }
    }
}

private fun DrawScope.drawJungleGrid(cols: Int, rows: Int, cellW: Float, cellH: Float) {
    // Alternating soft tiles
    for (x in 0 until cols) {
        for (y in 0 until rows) {
            val isEven = (x + y) % 2 == 0
            val tileColor = if (isEven) JungleSurface else JungleCard.copy(alpha = 0.6f)
            drawRect(
                color = tileColor,
                topLeft = Offset(x * cellW, y * cellH),
                size = Size(cellW, cellH)
            )
        }
    }

    // Subtle bamboo grid lines
    for (x in 0..cols) {
        drawLine(
            color = JungleCardBorder.copy(alpha = 0.35f),
            start = Offset(x * cellW, 0f),
            end = Offset(x * cellW, size.height),
            strokeWidth = 1f
        )
    }
    for (y in 0..rows) {
        drawLine(
            color = JungleCardBorder.copy(alpha = 0.35f),
            start = Offset(0f, y * cellH),
            end = Offset(size.width, y * cellH),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawFruitItem(
    fruit: ActiveFruit,
    cellW: Float,
    cellH: Float,
    pulse: Float
) {
    val cx = fruit.position.x * cellW + cellW / 2
    val cy = fruit.position.y * cellH + cellH / 2
    val baseRadius = minOf(cellW, cellH) * 0.42f
    val r = baseRadius * (if (fruit.type != FruitType.APPLE) pulse else 1f)

    when (fruit.type) {
        FruitType.APPLE -> {
            // Apple red body
            drawCircle(
                color = RubyRed,
                radius = r,
                center = Offset(cx, cy + r * 0.1f)
            )
            // Highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = r * 0.28f,
                center = Offset(cx - r * 0.35f, cy - r * 0.25f)
            )
            // Green leaf
            drawOval(
                color = CobraGreen,
                topLeft = Offset(cx, cy - r * 1.05f),
                size = Size(r * 0.5f, r * 0.8f)
            )
        }
        FruitType.WATERMELON -> {
            // Dark green rind
            drawCircle(
                color = Color(0xFF15803D),
                radius = r * 1.15f,
                center = Offset(cx, cy)
            )
            // Inner light rind
            drawCircle(
                color = Color(0xFF86EFAC),
                radius = r * 0.95f,
                center = Offset(cx, cy)
            )
            // Crimson flesh
            drawCircle(
                color = Color(0xFFDC2626),
                radius = r * 0.8f,
                center = Offset(cx, cy)
            )
            // Black seeds
            drawCircle(color = Color.Black, radius = r * 0.12f, center = Offset(cx - r * 0.3f, cy - r * 0.2f))
            drawCircle(color = Color.Black, radius = r * 0.12f, center = Offset(cx + r * 0.3f, cy - r * 0.1f))
            drawCircle(color = Color.Black, radius = r * 0.12f, center = Offset(cx, cy + r * 0.3f))
        }
        FruitType.BANANA -> {
            // Golden banana curved body
            drawRoundRect(
                color = RoyalGold,
                topLeft = Offset(cx - r * 0.9f, cy - r * 0.5f),
                size = Size(r * 1.8f, r * 0.9f),
                cornerRadius = CornerRadius(r * 0.4f, r * 0.4f)
            )
            // Brown tip
            drawCircle(color = Color(0xFF78350F), radius = r * 0.2f, center = Offset(cx - r * 0.8f, cy))
        }
        FruitType.WILD_BERRY -> {
            // Deep violet berry cluster
            drawCircle(color = MysticPurple, radius = r * 0.55f, center = Offset(cx - r * 0.35f, cy - r * 0.2f))
            drawCircle(color = Color(0xFF7E22CE), radius = r * 0.55f, center = Offset(cx + r * 0.35f, cy - r * 0.2f))
            drawCircle(color = Color(0xFF9333EA), radius = r * 0.55f, center = Offset(cx, cy + r * 0.35f))
            drawCircle(color = Color.White.copy(alpha = 0.6f), radius = r * 0.18f, center = Offset(cx - r * 0.3f, cy - r * 0.3f))
        }
        FruitType.GOLDEN_MANGO -> {
            // Pulsing golden aura
            drawCircle(
                color = RoyalGold.copy(alpha = 0.35f),
                radius = r * 1.5f,
                center = Offset(cx, cy)
            )
            // Shiny Mango
            drawOval(
                brush = Brush.radialGradient(
                    listOf(GoldenSun, RoyalGoldDark),
                    center = Offset(cx, cy)
                ),
                topLeft = Offset(cx - r * 0.9f, cy - r * 1.1f),
                size = Size(r * 1.8f, r * 2.2f)
            )
            // Sparkle
            drawCircle(color = Color.White, radius = r * 0.25f, center = Offset(cx - r * 0.35f, cy - r * 0.4f))
        }
        FruitType.CHILLI_PEPPER -> {
            // Fiery curved chilli
            drawRoundRect(
                brush = Brush.linearGradient(listOf(RubyRed, CrimsonDeep)),
                topLeft = Offset(cx - r * 0.6f, cy - r * 0.9f),
                size = Size(r * 1.2f, r * 1.8f),
                cornerRadius = CornerRadius(r * 0.6f, r * 0.6f)
            )
            // Stem
            drawCircle(color = CobraGreenDark, radius = r * 0.25f, center = Offset(cx, cy - r * 0.9f))
        }
    }
}

private fun DrawScope.drawRivalSnake(rival: RivalSnake, cellW: Float, cellH: Float) {
    val body = rival.body
    val radius = minOf(cellW, cellH) * 0.42f

    // Body segments
    for (i in body.indices.reversed()) {
        val pt = body[i]
        val cx = pt.x * cellW + cellW / 2
        val cy = pt.y * cellH + cellH / 2
        val segRadius = if (i == 0) radius * 1.1f else radius * (1f - (i.toFloat() / (body.size + 4)) * 0.3f)

        drawCircle(
            color = rival.color.copy(alpha = if (i == 0) 1f else 0.85f),
            radius = segRadius,
            center = Offset(cx, cy)
        )
    }

    // Rival head eyes
    if (body.isNotEmpty()) {
        val head = body.first()
        val cx = head.x * cellW + cellW / 2
        val cy = head.y * cellH + cellH / 2
        val eyeR = radius * 0.25f

        drawCircle(color = Color.White, radius = eyeR, center = Offset(cx - eyeR * 1.5f, cy - eyeR))
        drawCircle(color = Color.White, radius = eyeR, center = Offset(cx + eyeR * 1.5f, cy - eyeR))
        drawCircle(color = Color.Black, radius = eyeR * 0.6f, center = Offset(cx - eyeR * 1.5f, cy - eyeR))
        drawCircle(color = Color.Black, radius = eyeR * 0.6f, center = Offset(cx + eyeR * 1.5f, cy - eyeR))
    }
}

private fun DrawScope.drawPlayerSnake(
    snake: List<GridPoint>,
    skin: SnakeSkin,
    dir: Direction,
    cellW: Float,
    cellH: Float,
    isInvincible: Boolean,
    isFireActive: Boolean,
    isTurbo: Boolean,
    tonguePhase: Float
) {
    if (snake.isEmpty()) return
    val baseRadius = minOf(cellW, cellH) * 0.44f

    // 1. Draw Powerup Auras
    if (isInvincible || isFireActive) {
        val auraColor = if (isInvincible) RoyalGold.copy(alpha = 0.3f) else RubyRed.copy(alpha = 0.35f)
        for (pt in snake) {
            val cx = pt.x * cellW + cellW / 2
            val cy = pt.y * cellH + cellH / 2
            drawCircle(
                color = auraColor,
                radius = baseRadius * 1.6f,
                center = Offset(cx, cy)
            )
        }
    }

    // 2. Draw Body Segments from tail to neck
    for (i in (snake.size - 1) downTo 1) {
        val pt = snake[i]
        val cx = pt.x * cellW + cellW / 2
        val cy = pt.y * cellH + cellH / 2

        // Taper slightly towards tail
        val progress = i.toFloat() / snake.size.toFloat()
        val segRadius = baseRadius * (1f - progress * 0.25f)

        // Outer body
        drawCircle(
            color = skin.bodyColor,
            radius = segRadius,
            center = Offset(cx, cy)
        )

        // Inner belly scale stripe
        drawCircle(
            color = skin.underbellyColor.copy(alpha = 0.7f),
            radius = segRadius * 0.5f,
            center = Offset(cx, cy)
        )
    }

    // 3. Draw Head (Cobra Hood)
    val headPt = snake.first()
    val hx = headPt.x * cellW + cellW / 2
    val hy = headPt.y * cellH + cellH / 2
    val headRadius = baseRadius * 1.25f

    // Head base (hood shape)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(skin.headColor, skin.bodyColor),
            center = Offset(hx, hy)
        ),
        radius = headRadius,
        center = Offset(hx, hy)
    )

    // Flicking Red Forked Tongue
    val tongueLen = headRadius * (1.1f + tonguePhase * 0.35f)
    val tongueTipX = hx + dir.dx * tongueLen
    val tongueTipY = hy + dir.dy * tongueLen
    val tongueStartX = hx + dir.dx * headRadius * 0.7f
    val tongueStartY = hy + dir.dy * headRadius * 0.7f

    drawLine(
        color = RubyRed,
        start = Offset(tongueStartX, tongueStartY),
        end = Offset(tongueTipX, tongueTipY),
        strokeWidth = 3.5f
    )

    // Eyes
    val eyeOffsetX = if (dir.dx == 0) headRadius * 0.45f else 0f
    val eyeOffsetY = if (dir.dy == 0) headRadius * 0.45f else 0f
    val eyeCenterShiftX = dir.dx * headRadius * 0.25f
    val eyeCenterShiftY = dir.dy * headRadius * 0.25f

    val eye1 = Offset(hx + eyeCenterShiftX - eyeOffsetX, hy + eyeCenterShiftY - eyeOffsetY)
    val eye2 = Offset(hx + eyeCenterShiftX + eyeOffsetX, hy + eyeCenterShiftY + eyeOffsetY)
    val eyeRadius = headRadius * 0.28f

    // Eye sockets (white / glowing)
    drawCircle(color = Color.White, radius = eyeRadius, center = eye1)
    drawCircle(color = Color.White, radius = eyeRadius, center = eye2)

    // Pupils (fierce ruby/black)
    val pupilRadius = eyeRadius * 0.6f
    drawCircle(color = skin.eyeColor, radius = pupilRadius, center = eye1)
    drawCircle(color = skin.eyeColor, radius = pupilRadius, center = eye2)

    // 4. King's Golden Crown (Raja Pavi feature from uploaded logo!)
    if (skin.hasCrown) {
        val crownPath = Path()
        val crownBaseY = hy - headRadius * 0.65f
        val crownW = headRadius * 1.2f
        val crownH = headRadius * 0.75f

        val left = hx - crownW / 2
        val right = hx + crownW / 2
        val bottom = crownBaseY + crownH * 0.4f
        val top = crownBaseY - crownH * 0.6f

        crownPath.moveTo(left, bottom)
        crownPath.lineTo(left, top) // Left peak
        crownPath.lineTo(hx - crownW * 0.2f, bottom - crownH * 0.2f)
        crownPath.lineTo(hx, top - crownH * 0.2f) // Center royal peak
        crownPath.lineTo(hx + crownW * 0.2f, bottom - crownH * 0.2f)
        crownPath.lineTo(right, top) // Right peak
        crownPath.lineTo(right, bottom)
        crownPath.close()

        drawPath(
            path = crownPath,
            brush = Brush.verticalGradient(
                listOf(RoyalGold, RoyalGoldDark),
                startY = top,
                endY = bottom
            )
        )

        // Center ruby jewel on crown
        drawCircle(
            color = RubyRed,
            radius = crownW * 0.12f,
            center = Offset(hx, bottom - crownH * 0.2f)
        )
    }
}

package com.example.ui.components

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Direction
import com.example.ui.theme.*

@Composable
fun VirtualControls(
    onDirectionChanged: (Direction) -> Unit,
    onTurboChanged: (Boolean) -> Unit,
    isTurboActive: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Ergonomic D-Pad
        Box(
            modifier = Modifier
                .size(160.dp)
                .testTag("dpad_container"),
            contentAlignment = Alignment.Center
        ) {
            // Center decorative hub
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(JungleCard)
            )

            // UP Button
            DPadButton(
                icon = Icons.Default.KeyboardArrowUp,
                contentDescription = "Up",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .testTag("btn_up"),
                onClick = { onDirectionChanged(Direction.UP) }
            )

            // DOWN Button
            DPadButton(
                icon = Icons.Default.KeyboardArrowDown,
                contentDescription = "Down",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .testTag("btn_down"),
                onClick = { onDirectionChanged(Direction.DOWN) }
            )

            // LEFT Button
            DPadButton(
                icon = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Left",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .testTag("btn_left"),
                onClick = { onDirectionChanged(Direction.LEFT) }
            )

            // RIGHT Button
            DPadButton(
                icon = Icons.Default.KeyboardArrowRight,
                contentDescription = "Right",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .testTag("btn_right"),
                onClick = { onDirectionChanged(Direction.RIGHT) }
            )
        }

        // Right: Turbo Dash Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TurboBoostButton(
                isTurboActive = isTurboActive,
                onTurboChanged = onTurboChanged
            )
            Text(
                text = "TURBO DASH",
                style = MaterialTheme.typography.labelSmall,
                color = if (isTurboActive) RoyalGold else SurfaceTextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun DPadButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier
            .size(52.dp)
            .padding(2.dp),
        shape = RoundedCornerShape(14.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = JungleCard,
            contentColor = CobraGreen
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(32.dp),
            tint = CobraGreenLight
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TurboBoostButton(
    isTurboActive: Boolean,
    onTurboChanged: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(
                if (isTurboActive) RoyalGoldDark else JungleCard
            )
            .pointerInteropFilter { motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onTurboChanged(true)
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        onTurboChanged(false)
                        true
                    }
                    else -> false
                }
            }
            .testTag("turbo_boost_button"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = "Turbo Boost",
            tint = if (isTurboActive) AmberGlow else CobraGreen,
            modifier = Modifier.size(38.dp)
        )
    }
}

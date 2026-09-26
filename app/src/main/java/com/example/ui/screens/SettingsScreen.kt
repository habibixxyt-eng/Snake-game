package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameSoundManager
import com.example.model.ControlMode
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    soundManager: GameSoundManager,
    controlMode: ControlMode,
    onControlModeChanged: (ControlMode) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var soundEnabled by remember { mutableStateOf(soundManager.soundEnabled) }
    var hapticsEnabled by remember { mutableStateOf(soundManager.hapticsEnabled) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = JungleDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SETTINGS & PREFERENCES",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = RoyalGold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
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
            // Audio & Haptics Section
            item {
                Text(
                    text = "AUDIO & FEEDBACK",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                    color = CobraGreenLight
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = JungleCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = RoyalGold)
                                Column {
                                    Text("Arcade Sound FX", fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Crunchy bites, power-up chimes", style = MaterialTheme.typography.bodySmall, color = SurfaceTextMuted)
                                }
                            }
                            Switch(
                                checked = soundEnabled,
                                onCheckedChange = {
                                    soundEnabled = it
                                    soundManager.soundEnabled = it
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = CobraGreen, checkedTrackColor = CobraGreenDark)
                            )
                        }

                        Divider(color = JungleCardBorder, modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Vibration, contentDescription = null, tint = CobraGreenLight)
                                Column {
                                    Text("Haptic Vibration", fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Tactile ticks on eating & turn", style = MaterialTheme.typography.bodySmall, color = SurfaceTextMuted)
                                }
                            }
                            Switch(
                                checked = hapticsEnabled,
                                onCheckedChange = {
                                    hapticsEnabled = it
                                    soundManager.hapticsEnabled = it
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = CobraGreen, checkedTrackColor = CobraGreenDark)
                            )
                        }
                    }
                }
            }

            // Controls Section
            item {
                Text(
                    text = "SLITHER CONTROLS",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                    color = CobraGreenLight
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = JungleCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Choose your preferred primary control style. Note: Swipe gestures on the arena board are always active simultaneously!",
                            style = MaterialTheme.typography.bodySmall,
                            color = SurfaceTextMuted
                        )

                        ControlMode.values().forEach { mode ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(mode.label, color = Color.White, fontWeight = FontWeight.Medium)
                                RadioButton(
                                    selected = controlMode == mode,
                                    onClick = { onControlModeChanged(mode) },
                                    colors = RadioButtonDefaults.colors(selectedColor = CobraGreen)
                                )
                            }
                        }
                    }
                }
            }

            // About Section
            item {
                Text(
                    text = "ABOUT PAVI ENDRA PAAMBU",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                    color = CobraGreenLight
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = JungleSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Pavi Endra Paambu v1.0",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = RoyalGold
                        )
                        Text(
                            text = "A tribute to retro arcade snake games infused with high-energy Tamil gaming culture, crowned cobras, exotic fruits, and rival arena multiplayer dynamics.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SurfaceText
                        )
                        Text(
                            text = "Offline local persistence powered by Room Database.",
                            style = MaterialTheme.typography.labelSmall,
                            color = SurfaceTextMuted
                        )
                    }
                }
            }
        }
    }
}

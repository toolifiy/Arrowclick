package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArrowSkin
import com.example.ui.components.SettingsDialog
import com.example.ui.components.VibrantGoldenCoin
import java.util.Locale

@Composable
fun HomeScreen(
    coins: Int,
    hearts: Int,
    bestTimeMs: Long,
    totalHits: Int,
    equippedSkin: ArrowSkin,
    soundEnabled: Boolean,
    hapticEnabled: Boolean,
    onSoundToggle: (Boolean) -> Unit,
    onHapticToggle: (Boolean) -> Unit,
    onResetStats: () -> Unit,
    onStartGame: () -> Unit,
    onOpenShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var showSettingsDialog by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "home_arrow_spin")
    val rotationDeg by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 330f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    if (showSettingsDialog) {
        SettingsDialog(
            soundEnabled = soundEnabled,
            hapticEnabled = hapticEnabled,
            onSoundToggle = onSoundToggle,
            onHapticToggle = onHapticToggle,
            onResetStats = onResetStats,
            onDismiss = { showSettingsDialog = false }
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("home_screen")
    ) {
        val screenHeight = maxHeight
        val isCompactScreen = screenHeight < 640.dp
        val previewCircleSize = if (isCompactScreen) 118.dp else 156.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isCompactScreen) 16.dp else 22.dp, vertical = if (isCompactScreen) 8.dp else 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Header: Title, Coins Badge and Top Right Settings Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ARROW",
                        fontSize = if (isCompactScreen) 20.sp else 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.5.sp,
                        color = Color(0xFF111111)
                    )
                    Text(
                        text = "REFLEX",
                        fontSize = if (isCompactScreen) 11.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.5.sp,
                        color = Color(0xFF888888)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Hearts Badge / Chip (halka dark curved box)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF222226), // halka dark curved box
                        shadowElevation = 0.dp,
                        modifier = Modifier.testTag("home_hearts_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "❤️",
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$hearts/5",
                                fontSize = if (isCompactScreen) 13.sp else 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF4081) // Beautiful bright pink for high contrast on dark
                            )
                        }
                    }

                    // Vibrant Golden Coins Chip (halka dark curved box)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF222226), // halka dark curved box
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                onOpenShop()
                            }
                            .testTag("home_coins_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VibrantGoldenCoin(size = 18.dp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = com.example.util.FormatUtils.formatCoins(coins),
                                fontSize = if (isCompactScreen) 14.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White // White text for contrast on dark
                            )
                        }
                    }

                    // Settings Button in Top-Right Corner
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            showSettingsDialog = true
                        },
                        modifier = Modifier
                            .size(if (isCompactScreen) 44.dp else 48.dp)
                            .background(Color(0xFFF5F5F7), CircleShape)
                            .testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF222222),
                            modifier = Modifier.size(if (isCompactScreen) 26.dp else 30.dp)
                        )
                    }
                }
            }

            // 2. Middle Hero Showcase: Rotating Arrow & Record Stats
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFEBEBEF)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = if (isCompactScreen) 4.dp else 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(if (isCompactScreen) 12.dp else 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Arrow Preview
                        Box(
                            modifier = Modifier
                                .size(previewCircleSize)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            SingleArrowStaticCanvas(
                                skin = equippedSkin,
                                angleDeg = rotationDeg,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(if (isCompactScreen) 8.dp else 12.dp))

                        Text(
                            text = equippedSkin.name.uppercase(Locale.US),
                            fontSize = if (isCompactScreen) 13.sp else 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                            color = Color(0xFF222222)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "EQUIPPED SKIN",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.8.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (isCompactScreen) 6.dp else 10.dp))

                // Stats Row (Best Time & Total Hits)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Best Record Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFAFAFC))
                            .border(
                                width = 1.2.dp,
                                color = Color(0x44000000),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(if (isCompactScreen) 10.dp else 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9100),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "BEST SPEED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF777777)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (bestTimeMs > 0) "${bestTimeMs}ms" else "--",
                                fontSize = if (isCompactScreen) 16.sp else 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF111111)
                            )
                        }
                    }

                    // Total Hits Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFAFAFC))
                            .border(
                                width = 1.2.dp,
                                color = Color(0x44000000),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(if (isCompactScreen) 10.dp else 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrackChanges,
                                    contentDescription = null,
                                    tint = Color(0xFF00C853),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "TOTAL HITS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF777777)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalHits",
                                fontSize = if (isCompactScreen) 16.sp else 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF111111)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompactScreen) 8.dp else 12.dp))

            // 3. Bottom Action Buttons: START GAME & ARROW SKINS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isCompactScreen) 4.dp else 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        onStartGame()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF111111)
                    ),
                    contentPadding = PaddingValues(vertical = if (isCompactScreen) 12.dp else 15.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("start_game_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "START GAME",
                            fontSize = if (isCompactScreen) 14.sp else 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onOpenShop()
                    },
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.2.dp,
                        color = Color(0x44000000)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF111111)
                    ),
                    contentPadding = PaddingValues(vertical = if (isCompactScreen) 12.dp else 15.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_shop_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color(0xFF111111),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ARROW SKINS SHOP",
                            fontSize = if (isCompactScreen) 14.sp else 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = Color(0xFF111111)
                        )
                    }
                }
            }
        }
    }
}

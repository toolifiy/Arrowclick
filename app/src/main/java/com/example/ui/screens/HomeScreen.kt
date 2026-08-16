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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.screens.SingleArrowStaticCanvas
import java.util.Locale

@Composable
fun HomeScreen(
    coins: Int,
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Header: Title, Coins Badge and Top Right Settings Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ARROW",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                        color = Color(0xFF111111)
                    )
                    Text(
                        text = "REFLEX",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        color = Color(0xFF888888)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Vibrant Golden Coins Chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF5F5F7),
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                onOpenShop()
                            }
                            .testTag("home_coins_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VibrantGoldenCoin(size = 20.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$coins",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111111)
                            )
                        }
                    }

                    // Settings Button in Top-Right Corner (Enlarged by 50%)
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            showSettingsDialog = true
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFFF5F5F7), CircleShape)
                            .testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF222222),
                            modifier = Modifier.size(33.dp)
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
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEBEBEF)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Arrow Preview
                        Box(
                            modifier = Modifier
                                .size(160.dp)
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

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = equippedSkin.name.uppercase(Locale.US),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp,
                            color = Color(0xFF222222)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "EQUIPPED SKIN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stats Row (Best Time & Total Hits) with COMPLETE 4-SIDED BLACK BORDERLINE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Best Record Card (With full 4-sided crisp boundary line)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFFFAFAFC))
                            .border(
                                width = 1.2.dp,
                                color = Color(0x44000000), // Solid 4-sided boundary
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(14.dp),
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
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "BEST SPEED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF777777)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (bestTimeMs > 0) "${bestTimeMs}ms" else "--",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF111111)
                            )
                        }
                    }

                    // Total Hits Card (With full 4-sided crisp boundary line)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFFFAFAFC))
                            .border(
                                width = 1.2.dp,
                                color = Color(0x44000000), // Solid 4-sided boundary
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(14.dp),
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
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "TOTAL HITS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF777777)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$totalHits",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF111111)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Bottom Action Buttons: START GAME & ARROW SKINS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        onStartGame()
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF111111)
                    ),
                    contentPadding = PaddingValues(vertical = 16.dp),
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
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "START GAME",
                            fontSize = 16.sp,
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
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.2.dp,
                        color = Color(0x44000000)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF111111)
                    ),
                    contentPadding = PaddingValues(vertical = 16.dp),
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
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ARROW SKINS SHOP",
                            fontSize = 16.sp,
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

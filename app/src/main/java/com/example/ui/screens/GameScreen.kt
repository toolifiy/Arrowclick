package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.model.ArrowSkin
import com.example.ui.components.ArrowGameCanvas
import com.example.ui.components.VibrantGoldenCoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale

@Composable
fun GameScreen(
    skin: ArrowSkin,
    coins: Int,
    isArrowVisible: Boolean,
    lastReactionTimeMs: Long?,
    showReactionOverlay: Boolean,
    lastHitOffset: Offset?,
    soundEnabled: Boolean,
    hapticEnabled: Boolean,
    onTipClicked: (reactionTimeMs: Long, tipOffset: Offset) -> Unit,
    onMissClicked: (touchOffset: Offset) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var liveSpawnTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var liveElapsedMs by remember { mutableLongStateOf(0L) }
    var showExitConfirmation by remember { mutableStateOf(false) }

    // Fast real-time live timer loop ticking every ~16ms while the arrow is waiting for tap
    LaunchedEffect(isArrowVisible, liveSpawnTimeMs) {
        if (isArrowVisible) {
            while (isActive) {
                liveElapsedMs = (System.currentTimeMillis() - liveSpawnTimeMs).coerceAtLeast(0L)
                delay(16L)
            }
        }
    }

    if (showExitConfirmation) {
        ExitGameConfirmationDialog(
            onResume = { showExitConfirmation = false },
            onExit = {
                showExitConfirmation = false
                onBackToHome()
            }
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("game_screen")
    ) {
        val isCompactScreen = maxHeight < 640.dp

        // 1. TOP HEADER BAR: Perfectly centered horizontally using Box alignment
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = if (isCompactScreen) 8.dp else 14.dp)
                .zIndex(10f)
        ) {
            // Left: Back button
            IconButton(
                onClick = {
                    if (hapticEnabled) view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    showExitConfirmation = true
                },
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.CenterStart)
                    .testTag("back_to_home_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Home",
                    tint = Color(0xFF111111),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Middle: EXACTLY CENTERED LIVE TIMER (Not shifted left or right)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = "REACTION TIME",
                    fontSize = if (isCompactScreen) 10.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color(0xFF888888)
                )

                val displayMs = if (isArrowVisible) liveElapsedMs else (lastReactionTimeMs ?: liveElapsedMs)
                val displaySec = displayMs / 1000f

                Text(
                    text = String.format(Locale.US, "%.2fs", displaySec),
                    fontSize = if (isCompactScreen) 26.sp else 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF111111),
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "$displayMs ms",
                    fontSize = if (isCompactScreen) 11.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00C853)
                )
            }

            // Right: Coins Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF5F5F7),
                shadowElevation = 0.dp,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VibrantGoldenCoin(size = 18.dp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "$coins",
                        fontSize = if (isCompactScreen) 14.sp else 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222)
                    )
                }
            }
        }

        // 2. Playable Interactive Arrow Canvas
        if (isArrowVisible) {
            ArrowGameCanvas(
                skin = skin,
                isArrowVisible = true,
                onArrowSpawned = { spawnMs ->
                    liveSpawnTimeMs = spawnMs
                    liveElapsedMs = 0L
                },
                onTipClicked = { reactionTimeMs, tipOffset ->
                    if (hapticEnabled) view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onTipClicked(reactionTimeMs, tipOffset)
                },
                onMissClicked = { touchOffset ->
                    if (hapticEnabled) view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    onMissClicked(touchOffset)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 3. Discrete Reaction Time Details during exact 0.5s pause
        if (showReactionOverlay && !isArrowVisible && lastReactionTimeMs != null) {
            val sec = lastReactionTimeMs / 1000f
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(20.dp)
                    .zIndex(5f)
            ) {
                Text(
                    text = "HIT!",
                    fontSize = if (isCompactScreen) 16.sp else 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00C853),
                    letterSpacing = 3.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = String.format(Locale.US, "%.2fs", sec),
                    fontSize = if (isCompactScreen) 44.sp else 54.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF111111)
                )
                Text(
                    text = "$lastReactionTimeMs ms",
                    fontSize = if (isCompactScreen) 18.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00C853)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VibrantGoldenCoin(size = if (isCompactScreen) 20.dp else 24.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+1 COIN",
                        fontSize = if (isCompactScreen) 15.sp else 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color(0xFF00C853)
                    )
                }
            }
        }

        // 4. Bottom Instruction: "TAP THE ARROW TIP!"
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = if (isCompactScreen) 16.dp else 30.dp)
                .zIndex(2f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "TAP THE ARROW TIP!",
                fontSize = if (isCompactScreen) 14.sp else 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = Color(0xFF111111),
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ExitGameConfirmationDialog(
    onResume: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(
        onDismissRequest = onResume,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.2.dp, Color(0x33000000)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "EXIT GAME?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = Color(0xFF111111)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Do you want to return to the home screen?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Resume Button
                        OutlinedButton(
                            onClick = onResume,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.2.dp, Color(0xFF111111)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF111111)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text(
                                text = "RESUME",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color(0xFF111111)
                            )
                        }

                        // Exit Button
                        Button(
                            onClick = onExit,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF111111),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text(
                                text = "EXIT",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

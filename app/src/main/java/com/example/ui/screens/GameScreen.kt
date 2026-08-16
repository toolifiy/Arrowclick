package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    skin: ArrowSkin,
    dotSkin: com.example.model.DotSkin,
    coins: Int,
    hearts: Int,
    isArrowVisible: Boolean,
    lastReactionTimeMs: Long?,
    showReactionOverlay: Boolean,
    lastHitOffset: Offset?,
    soundEnabled: Boolean,
    hapticEnabled: Boolean,
    showOutPopup: Boolean,
    showMockAd: Boolean,
    onArrowSpawned: () -> Unit,
    onAdTriggered: () -> Unit,
    onAdCompleted: () -> Unit,
    onTipClicked: (reactionTimeMs: Long, tipOffset: Offset) -> Unit,
    onMissClicked: (touchOffset: Offset) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var liveElapsedMs by remember { mutableLongStateOf(0L) }
    var showExitConfirmation by remember { mutableStateOf(false) }

    // Fast real-time live timer loop ticking every ~16ms while the arrow is waiting for tap
    LaunchedEffect(isArrowVisible) {
        if (isArrowVisible) {
            val startTime = System.currentTimeMillis()
            onArrowSpawned() // Triggers sound beep on arrow spawn!
            while (isActive) {
                liveElapsedMs = (System.currentTimeMillis() - startTime).coerceAtLeast(0L)
                delay(16L)
            }
        } else {
            liveElapsedMs = 0L
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

            // Right: Hearts and Coins Badges Side-By-Side (halka dark curved boxes)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                // Hearts Badge (halka dark curved box)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF222226), // halka dark curved box
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "❤️",
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$hearts/5",
                            fontSize = if (isCompactScreen) 12.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF4081) // Beautiful bright pink for high contrast on dark
                        )
                    }
                }

                // Coins Badge (halka dark curved box)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF222226), // halka dark curved box
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VibrantGoldenCoin(size = 16.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$coins",
                            fontSize = if (isCompactScreen) 13.sp else 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White // White text for contrast on dark
                        )
                    }
                }
            }
        }

        // 2. Playable Interactive Arrow Canvas
        if (isArrowVisible) {
            ArrowGameCanvas(
                skin = skin,
                dotSkin = dotSkin,
                isArrowVisible = true,
                onArrowSpawned = { _ ->
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
        if (isArrowVisible) {
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
        // ==========================================
        // 5. OUT OF HEARTS 1.3-SECOND COUNTDOWN OVERLAY
        // ==========================================
        if (showOutPopup) {
            var countdownValue by remember { mutableFloatStateOf(1.3f) }
            var animatedScale by remember { mutableFloatStateOf(0.8f) }
            var animatedAlpha by remember { mutableFloatStateOf(0f) }

            LaunchedEffect(Unit) {
                // Animate entry scale and alpha
                val animDuration = 250f
                val animStart = System.currentTimeMillis()
                launch {
                    while (true) {
                        val elapsed = System.currentTimeMillis() - animStart
                        if (elapsed >= animDuration) {
                            animatedScale = 1.0f
                            animatedAlpha = 1.0f
                            break
                        }
                        val progress = elapsed / animDuration
                        animatedScale = 0.8f + 0.2f * progress
                        animatedAlpha = progress
                        delay(16L)
                    }
                }

                // Core countdown loop
                val startTime = System.currentTimeMillis()
                while (countdownValue > 0f) {
                    delay(16L)
                    val passed = (System.currentTimeMillis() - startTime) / 1000f
                    countdownValue = (1.3f - passed).coerceAtLeast(0f)
                }
                // When 1.3s completes -> Automatically trigger ad flow
                onAdTriggered()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x4D000000).copy(alpha = animatedAlpha * 0.3f)) // Light dark background (not very dark, just slightly dark!)
                    .zIndex(100f)
                    .clickable(enabled = false) {}, // absorb clicks
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp)
                        .graphicsLayer(
                            scaleX = animatedScale,
                            scaleY = animatedScale,
                            alpha = animatedAlpha
                        )
                        .testTag("out_of_hearts_dialog")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "YOU ARE OUT!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color(0xFFFF1744)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Heart & Sweeping Circle Progress Arc
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(140.dp)
                        ) {
                            Canvas(modifier = Modifier.size(130.dp)) {
                                // Background circle ring
                                drawCircle(
                                    color = Color(0x1FFF1744),
                                    style = Stroke(width = 6.dp.toPx())
                                )
                                // Active Sweeping progress line from Left (180 degrees) clockwise
                                val sweep = 360f * ((1.3f - countdownValue) / 1.3f)
                                drawArc(
                                    color = Color(0xFFFF1744),
                                    startAngle = 180f,
                                    sweepAngle = sweep,
                                    useCenter = false,
                                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Broken heart icon
                                Text(
                                    text = "💔",
                                    fontSize = 44.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(Locale.US, "%.1fs", countdownValue),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFF1744)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Oops! You missed the tip or touched the tail.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Watch an ad to continue playing...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF888888),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 6. PREMIUM FULLSCREEN MOCK AD OVERLAY
        // ==========================================
        if (showMockAd) {
            var adCountdown by remember { mutableIntStateOf(5) }
            val isRewardClaimable = adCountdown <= 0

            LaunchedEffect(Unit) {
                while (adCountdown > 0) {
                    delay(1000L)
                    adCountdown--
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF12121A)) // Premium gaming dark theme background
                    .zIndex(200f)
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Ad Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF333344)
                        ) {
                            Text(
                                text = "SPONSORED AD",
                                color = Color.LightGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        // Close trigger
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isRewardClaimable) Color(0xFFFFD54F) else Color(0x33FFFFFF))
                                .clickable(enabled = isRewardClaimable) {
                                    onAdCompleted()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isRewardClaimable) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = "$adCountdown",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Ad Core Banner (Visually stunning premium artwork mockup)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2C)),
                        border = BorderStroke(1.5.dp, Color(0xFF3F51B5)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Tech style vector background
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(Color(0x403F51B5), Color.Transparent),
                                        center = Offset(size.width / 2f, size.height / 2f),
                                        radius = size.width * 0.7f
                                    )
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "⚔️ RAID ⚔️",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFFD54F),
                                        letterSpacing = 4.sp
                                    )
                                    Text(
                                        text = "REFLEX LEGENDS",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 2.sp
                                    )
                                }

                                // Interactive design graphic inside ad
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676),
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            repeat(5) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFFD54F),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "10M+ Downloads",
                                            color = Color.Gray,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = "Duniya ka sabse premium, highly addictive reflex challenge game! Abhi download karein aur speed records todein.",
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Bottom Call to Action claim button
                    Button(
                        onClick = {
                            if (isRewardClaimable) onAdCompleted()
                        },
                        enabled = isRewardClaimable,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00C853),
                            disabledContainerColor = Color(0xFF1E3525)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (isRewardClaimable) {
                            Text(
                                text = "CLAIM 1 FREE HEART ❤️",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        } else {
                            Text(
                                text = "REWARD IN $adCountdown SECONDS...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
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
                .background(Color(0x99000000)) // Gorgeous dim background for contrast
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color(0xFF111111)),
                elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .widthIn(max = 460.dp)
                    .graphicsLayer(scaleX = 1.12f, scaleY = 1.12f) // Visually 25% larger!
                    .testTag("exit_confirmation_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "EXIT GAME?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.5.sp,
                        color = Color(0xFF111111)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Do you want to return to the home screen?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF555555),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Resume Button
                        OutlinedButton(
                            onClick = onResume,
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(2.dp, Color(0xFF111111)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF111111)
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(62.dp)
                        ) {
                            Text(
                                text = "RESUME",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                maxLines = 1,
                                color = Color(0xFF111111)
                            )
                        }

                        // Exit Button
                        Button(
                            onClick = onExit,
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF111111),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(62.dp)
                        ) {
                            Text(
                                text = "EXIT",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                maxLines = 1,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

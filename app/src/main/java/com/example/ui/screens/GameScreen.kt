package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.model.ArrowSkin
import com.example.ui.components.ArrowGameCanvas
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
    onTipClicked: (reactionTimeMs: Long, tipOffset: Offset) -> Unit,
    onMissClicked: (touchOffset: Offset) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var liveSpawnTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var liveElapsedMs by remember { mutableLongStateOf(0L) }

    // Fast real-time live timer loop ticking every ~16ms while the arrow is waiting for tap
    LaunchedEffect(isArrowVisible, liveSpawnTimeMs) {
        if (isArrowVisible) {
            while (isActive) {
                liveElapsedMs = (System.currentTimeMillis() - liveSpawnTimeMs).coerceAtLeast(0L)
                delay(16L)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("game_screen")
    ) {
        // 1. TOP HEADER BAR with higher Z-Index (Guarantees back button clicks are always received immediately)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .zIndex(10f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onBackToHome()
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("back_to_home_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Home",
                    tint = Color(0xFF111111),
                    modifier = Modifier.size(30.dp)
                )
            }

            // Top center live real-time reaction counter
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "REACTION TIME",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color(0xFF888888)
                )

                val displayMs = if (isArrowVisible) liveElapsedMs else (lastReactionTimeMs ?: liveElapsedMs)
                val displaySec = displayMs / 1000f

                Text(
                    text = String.format(Locale.US, "%.2fs", displaySec),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF111111),
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "$displayMs ms",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00C853) // Vibrant green indicator
                )
            }

            // Coins counter badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF5F5F7),
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$coins",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222)
                    )
                }
            }
        }

        // 2. Playable Interactive Arrow Canvas (Centered 2X Arrow)
        ArrowGameCanvas(
            skin = skin,
            isArrowVisible = isArrowVisible,
            onArrowSpawned = { spawnMs ->
                liveSpawnTimeMs = spawnMs
                liveElapsedMs = 0L
            },
            onTipClicked = { reactionTimeMs, tipOffset ->
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onTipClicked(reactionTimeMs, tipOffset)
            },
            onMissClicked = { touchOffset ->
                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                onMissClicked(touchOffset)
            },
            modifier = Modifier.fillMaxSize()
        )

        // 3. Clean Center Reaction Time Details during 0.5s pause (NO BOX, directly on screen canvas with Green text)
        AnimatedVisibility(
            visible = showReactionOverlay && lastReactionTimeMs != null,
            enter = fadeIn(tween(80)) + scaleIn(tween(120, easing = FastOutSlowInEasing)),
            exit = fadeOut(tween(100)) + scaleOut(tween(100)),
            modifier = Modifier
                .align(Alignment.Center)
                .zIndex(5f)
        ) {
            if (lastReactionTimeMs != null) {
                val sec = lastReactionTimeMs / 1000f
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "HIT!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00C853), // Green
                        letterSpacing = 3.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "%.2fs", sec),
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF111111)
                    )
                    Text(
                        text = "$lastReactionTimeMs ms",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00C853) // Green
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+1 COIN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color(0xFF00C853) // Green
                        )
                    }
                }
            }
        }

        // 4. Floating +1 Coin badge with Green accent
        if (showReactionOverlay && lastHitOffset != null) {
            val popupAlpha by animateFloatAsState(
                targetValue = if (showReactionOverlay) 1f else 0f,
                animationSpec = tween(380, easing = LinearOutSlowInEasing),
                label = "coin_alpha"
            )
            val popupOffsetY by animateFloatAsState(
                targetValue = if (showReactionOverlay) -70f else 0f,
                animationSpec = tween(400, easing = LinearOutSlowInEasing),
                label = "coin_offset"
            )

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (lastHitOffset.x - 30).toInt(),
                            y = (lastHitOffset.y + popupOffsetY - 30).toInt()
                        )
                    }
                    .alpha(popupAlpha)
                    .zIndex(6f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF00C853), // Green
                    shadowElevation = 3.dp
                ) {
                    Text(
                        text = "+1",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // 5. Bottom Instruction: "TAP THE ARROW TIP!"
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 36.dp)
                .zIndex(2f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "TAP THE ARROW TIP!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = Color(0xFF111111),
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center
            )
        }
    }
}

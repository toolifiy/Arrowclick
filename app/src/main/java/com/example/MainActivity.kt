package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable Edge-to-Edge with dark status bar icons and navigation bar buttons for clean white background
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val bestTimeMs by viewModel.bestTimeMs.collectAsState()
    val totalHits by viewModel.totalHits.collectAsState()
    val unlockedSkinIds by viewModel.unlockedSkinIds.collectAsState()
    val equippedSkin by viewModel.equippedSkin.collectAsState()
    val equippedSkinId by viewModel.equippedSkinId.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()

    // Handle back button on Android to return to Home screen
    BackHandler(enabled = uiState.screen != AppScreen.HOME) {
        viewModel.navigateTo(AppScreen.HOME)
    }

    // Auto-detect status bar, notch cutouts, and bottom navigation bar insets
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        // Super smooth non-side transition: Vertical pop & material scale-in (< 300ms)
        AnimatedContent(
            targetState = uiState.screen,
            transitionSpec = {
                if (targetState == AppScreen.GAME || targetState == AppScreen.SHOP) {
                    // Forward navigation (Entering from button tap / bottom-up zoom)
                    (fadeIn(animationSpec = tween(280, easing = EaseOutQuart)) +
                            scaleIn(initialScale = 0.92f, animationSpec = tween(280, easing = EaseOutQuart)) +
                            slideInVertically(
                                initialOffsetY = { it / 6 },
                                animationSpec = tween(280, easing = EaseOutQuart)
                            ))
                        .togetherWith(
                            fadeOut(animationSpec = tween(200, easing = EaseInOutCubic)) +
                                    scaleOut(targetScale = 0.96f, animationSpec = tween(200, easing = EaseInOutCubic))
                        )
                } else {
                    // Backward navigation (Returning to Home)
                    (fadeIn(animationSpec = tween(260, easing = EaseOutQuart)) +
                            scaleIn(initialScale = 0.96f, animationSpec = tween(260, easing = EaseOutQuart)))
                        .togetherWith(
                            fadeOut(animationSpec = tween(220, easing = EaseInOutCubic)) +
                                    slideOutVertically(
                                        targetOffsetY = { it / 6 },
                                        animationSpec = tween(220, easing = EaseInOutCubic)
                                    ) +
                                    scaleOut(targetScale = 0.92f, animationSpec = tween(220, easing = EaseInOutCubic))
                        )
                }
            },
            label = "screen_smooth_transition",
            modifier = Modifier.fillMaxSize()
        ) { targetScreen ->
            when (targetScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        coins = coins,
                        bestTimeMs = bestTimeMs,
                        totalHits = totalHits,
                        equippedSkin = equippedSkin,
                        soundEnabled = soundEnabled,
                        hapticEnabled = hapticEnabled,
                        onSoundToggle = { viewModel.setSoundEnabled(it) },
                        onHapticToggle = { viewModel.setHapticEnabled(it) },
                        onResetStats = { viewModel.resetStats() },
                        onStartGame = { viewModel.navigateTo(AppScreen.GAME) },
                        onOpenShop = { viewModel.navigateTo(AppScreen.SHOP) },
                        modifier = screenModifier
                    )
                }

                AppScreen.GAME -> {
                    GameScreen(
                        skin = equippedSkin,
                        coins = coins,
                        isArrowVisible = uiState.isArrowVisible,
                        lastReactionTimeMs = uiState.lastReactionTimeMs,
                        showReactionOverlay = uiState.showReactionOverlay,
                        lastHitOffset = uiState.lastHitOffset,
                        soundEnabled = soundEnabled,
                        hapticEnabled = hapticEnabled,
                        onTipClicked = { reactionTimeMs, tipOffset ->
                            viewModel.onTipHit(reactionTimeMs, tipOffset)
                        },
                        onMissClicked = { touchOffset ->
                            viewModel.onMissedTap(touchOffset)
                        },
                        onBackToHome = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = screenModifier
                    )
                }

                AppScreen.SHOP -> {
                    ShopScreen(
                        coins = coins,
                        unlockedSkinIds = unlockedSkinIds,
                        equippedSkinId = equippedSkinId,
                        onBuySkin = { skin -> viewModel.buySkin(skin) },
                        onEquipSkin = { skinId -> viewModel.equipSkin(skinId) },
                        onBack = { viewModel.navigateTo(AppScreen.HOME) },
                        message = uiState.message,
                        onClearMessage = { viewModel.clearMessage() },
                        modifier = screenModifier
                    )
                }
            }
        }
    }
}

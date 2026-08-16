package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.model.ArrowSkin
import com.example.model.ArrowSkinCatalog
import com.example.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    GAME,
    SHOP
}

data class GameUiState(
    val screen: AppScreen = AppScreen.HOME,
    val isArrowVisible: Boolean = true,
    val lastReactionTimeMs: Long? = null,
    val showReactionOverlay: Boolean = false,
    val lastHitOffset: Offset? = null,
    val showCoinPopup: Boolean = false,
    val message: String? = null,
    val showOutPopup: Boolean = false, // If true, triggers 3s broken heart circular progress countdown
    val showMockAd: Boolean = false     // Fullscreen mock ad overlay
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository(application)
    private val soundManager = SoundManager(application)

    val coins: StateFlow<Int> = repository.coins
    val bestTimeMs: StateFlow<Long> = repository.bestTimeMs
    val totalHits: StateFlow<Int> = repository.totalHits
    val unlockedSkinIds: StateFlow<Set<String>> = repository.unlockedSkinIds
    val equippedSkinId: StateFlow<String> = repository.equippedSkinId
    val soundEnabled: StateFlow<Boolean> = repository.soundEnabled
    val hapticEnabled: StateFlow<Boolean> = repository.hapticEnabled
    val hearts: StateFlow<Int> = repository.hearts

    val equippedSkin: StateFlow<ArrowSkin> = repository.equippedSkinId
        .combine(repository.unlockedSkinIds) { id, _ ->
            ArrowSkinCatalog.getSkinById(id)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ArrowSkinCatalog.CLASSIC)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var respawnJob: Job? = null

    init {
        // Collect sound & haptic configurations to soundManager
        viewModelScope.launch {
            soundEnabled.collect { enabled ->
                soundManager.setSoundEnabled(enabled)
            }
        }
        viewModelScope.launch {
            hapticEnabled.collect { enabled ->
                soundManager.setHapticEnabled(enabled)
            }
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        repository.setSoundEnabled(enabled)
    }

    fun setHapticEnabled(enabled: Boolean) {
        repository.setHapticEnabled(enabled)
    }

    fun resetStats() {
        repository.resetGameStats()
        _uiState.value = _uiState.value.copy(message = "Stats reset successfully!")
    }

    fun navigateTo(screen: AppScreen) {
        respawnJob?.cancel()
        if (screen == AppScreen.GAME) {
            _uiState.value = _uiState.value.copy(
                screen = screen,
                isArrowVisible = true,
                showReactionOverlay = false,
                showCoinPopup = false,
                showOutPopup = false,
                showMockAd = false
            )
        } else {
            _uiState.value = _uiState.value.copy(screen = screen)
        }
    }

    fun onArrowSpawned() {
        soundManager.playSpawnTick()
    }

    fun onTipHit(reactionTimeMs: Long, tipOffset: Offset) {
        respawnJob?.cancel()

        // 1. Play success effects
        soundManager.playSuccessTick()
        soundManager.playHitFeedback()

        // 2. Add +1 Coin
        repository.addCoins(1)

        // 3. Record reaction time & check best record
        repository.recordReactionTime(reactionTimeMs)

        // 4. Hide arrow and show reaction time details for exactly 0.5s (500ms)
        _uiState.value = _uiState.value.copy(
            isArrowVisible = false,
            lastReactionTimeMs = reactionTimeMs,
            showReactionOverlay = true,
            lastHitOffset = tipOffset,
            showCoinPopup = true
        )

        respawnJob = viewModelScope.launch {
            delay(500L) // Exact .5 second delay requested by user
            _uiState.value = _uiState.value.copy(
                isArrowVisible = true,
                showReactionOverlay = false,
                showCoinPopup = false
            )
        }
    }

    fun onMissedTap(offset: Offset) {
        respawnJob?.cancel()
        soundManager.playMissFeedback()

        // Deduct 1 Heart
        val hadHeart = repository.useHeart()
        val remainingHearts = repository.hearts.value

        if (!hadHeart || remainingHearts <= 0) {
            // Out of hearts completely! Trigger the circular red loading countdown
            _uiState.value = _uiState.value.copy(
                isArrowVisible = false,
                showReactionOverlay = false,
                showCoinPopup = false,
                showOutPopup = true
            )
        } else {
            // Heart deducted, respawn next arrow in 500ms
            _uiState.value = _uiState.value.copy(
                isArrowVisible = false,
                showReactionOverlay = false,
                showCoinPopup = false
            )
            respawnJob = viewModelScope.launch {
                delay(500L)
                _uiState.value = _uiState.value.copy(
                    isArrowVisible = true
                )
            }
        }
    }

    fun onTailHit(offset: Offset) {
        respawnJob?.cancel()
        soundManager.playMissFeedback()

        // Tail hit triggers INSTANT OUT! Deplete remaining hearts to 0 and show countdown
        repository.setHearts(0)
        _uiState.value = _uiState.value.copy(
            isArrowVisible = false,
            showReactionOverlay = false,
            showCoinPopup = false,
            showOutPopup = true
        )
    }

    fun triggerMockAd() {
        _uiState.value = _uiState.value.copy(
            showOutPopup = false,
            showMockAd = true
        )
    }

    fun onAdCompleted() {
        // Watch ad completed -> Grant exactly 1 Heart!
        repository.setHearts(1)
        _uiState.value = _uiState.value.copy(
            showMockAd = false,
            showOutPopup = false,
            isArrowVisible = true
        )
        soundManager.playSuccessTick()
    }

    fun buySkin(skin: ArrowSkin) {
        if (unlockedSkinIds.value.contains(skin.id)) {
            repository.equipSkin(skin.id)
            _uiState.value = _uiState.value.copy(message = "Equipped ${skin.name}!")
            return
        }

        if (repository.deductCoins(skin.price)) {
            repository.unlockSkin(skin.id)
            repository.equipSkin(skin.id)
            _uiState.value = _uiState.value.copy(message = "Unlocked & Equipped ${skin.name}!")
        } else {
            val needed = skin.price - coins.value
            _uiState.value = _uiState.value.copy(message = "Need $needed more coins to unlock!")
        }
    }

    fun equipSkin(skinId: String) {
        if (unlockedSkinIds.value.contains(skinId)) {
            repository.equipSkin(skinId)
            val skin = ArrowSkinCatalog.getSkinById(skinId)
            _uiState.value = _uiState.value.copy(message = "Equipped ${skin.name}!")
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}

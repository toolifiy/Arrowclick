package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.model.ArrowSkin
import com.example.model.ArrowSkinCatalog
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
    val message: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository(application)

    val coins: StateFlow<Int> = repository.coins
    val bestTimeMs: StateFlow<Long> = repository.bestTimeMs
    val totalHits: StateFlow<Int> = repository.totalHits
    val unlockedSkinIds: StateFlow<Set<String>> = repository.unlockedSkinIds
    val equippedSkinId: StateFlow<String> = repository.equippedSkinId

    val equippedSkin: StateFlow<ArrowSkin> = repository.equippedSkinId
        .combine(repository.unlockedSkinIds) { id, _ ->
            ArrowSkinCatalog.getSkinById(id)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ArrowSkinCatalog.CLASSIC)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var respawnJob: Job? = null

    fun navigateTo(screen: AppScreen) {
        respawnJob?.cancel()
        if (screen == AppScreen.GAME) {
            _uiState.value = _uiState.value.copy(
                screen = screen,
                isArrowVisible = true,
                showReactionOverlay = false,
                showCoinPopup = false
            )
        } else {
            _uiState.value = _uiState.value.copy(screen = screen)
        }
    }

    fun onTipHit(reactionTimeMs: Long, tipOffset: Offset) {
        // Cancel any pending respawn
        respawnJob?.cancel()

        // 1. Add +1 Coin
        repository.addCoins(1)

        // 2. Record reaction time & check best record
        repository.recordReactionTime(reactionTimeMs)

        // 3. Hide arrow and show reaction time details for exactly 0.5s (500ms)
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
        // Optional miss feedback or sound trigger
    }

    fun buySkin(skin: ArrowSkin) {
        if (unlockedSkinIds.value.contains(skin.id)) {
            // Already owned, just equip
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
}

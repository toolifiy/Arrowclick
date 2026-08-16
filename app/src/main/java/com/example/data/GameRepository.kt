package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("arrow_reflex_prefs", Context.MODE_PRIVATE)

    private val _coins = MutableStateFlow(prefs.getInt(KEY_COINS, 0))
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _bestTimeMs = MutableStateFlow(prefs.getLong(KEY_BEST_TIME, 0L))
    val bestTimeMs: StateFlow<Long> = _bestTimeMs.asStateFlow()

    private val _totalHits = MutableStateFlow(prefs.getInt(KEY_TOTAL_HITS, 0))
    val totalHits: StateFlow<Int> = _totalHits.asStateFlow()

    private val _equippedSkinId = MutableStateFlow(prefs.getString(KEY_EQUIPPED_SKIN, "skin_classic") ?: "skin_classic")
    val equippedSkinId: StateFlow<String> = _equippedSkinId.asStateFlow()

    private val _unlockedSkinIds = MutableStateFlow(
        prefs.getStringSet(KEY_UNLOCKED_SKINS, setOf("skin_classic")) ?: setOf("skin_classic")
    )
    val unlockedSkinIds: StateFlow<Set<String>> = _unlockedSkinIds.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND_ENABLED, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(prefs.getBoolean(KEY_HAPTIC_ENABLED, true))
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
        _soundEnabled.value = enabled
    }

    fun setHapticEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC_ENABLED, enabled).apply()
        _hapticEnabled.value = enabled
    }

    fun resetGameStats() {
        prefs.edit()
            .putLong(KEY_BEST_TIME, 0L)
            .putInt(KEY_TOTAL_HITS, 0)
            .apply()
        _bestTimeMs.value = 0L
        _totalHits.value = 0
    }

    fun addCoins(amount: Int = 1) {
        val newCoins = _coins.value + amount
        prefs.edit().putInt(KEY_COINS, newCoins).apply()
        _coins.value = newCoins
    }

    fun deductCoins(amount: Int): Boolean {
        if (_coins.value >= amount) {
            val newCoins = _coins.value - amount
            prefs.edit().putInt(KEY_COINS, newCoins).apply()
            _coins.value = newCoins
            return true
        }
        return false
    }

    fun recordReactionTime(timeMs: Long) {
        val currentBest = _bestTimeMs.value
        if (currentBest == 0L || timeMs < currentBest) {
            prefs.edit().putLong(KEY_BEST_TIME, timeMs).apply()
            _bestTimeMs.value = timeMs
        }
        val newHits = _totalHits.value + 1
        prefs.edit().putInt(KEY_TOTAL_HITS, newHits).apply()
        _totalHits.value = newHits
    }

    fun unlockSkin(skinId: String): Boolean {
        val current = _unlockedSkinIds.value.toMutableSet()
        if (!current.contains(skinId)) {
            current.add(skinId)
            prefs.edit().putStringSet(KEY_UNLOCKED_SKINS, current).apply()
            _unlockedSkinIds.value = current
            return true
        }
        return false
    }

    fun equipSkin(skinId: String) {
        prefs.edit().putString(KEY_EQUIPPED_SKIN, skinId).apply()
        _equippedSkinId.value = skinId
    }

    companion object {
        private const val KEY_COINS = "user_coins"
        private const val KEY_BEST_TIME = "user_best_time_ms"
        private const val KEY_TOTAL_HITS = "user_total_hits"
        private const val KEY_EQUIPPED_SKIN = "equipped_skin_id"
        private const val KEY_UNLOCKED_SKINS = "unlocked_skin_ids_set"
        private const val KEY_SOUND_ENABLED = "sound_effects_enabled"
        private const val KEY_HAPTIC_ENABLED = "haptic_feedback_enabled"
    }
}

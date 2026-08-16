package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    // Hearts state flow (Max 5, restored daily to 5)
    private val _hearts = MutableStateFlow(5)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private val _heartsDepletedTime = MutableStateFlow(0L)
    val heartsDepletedTime: StateFlow<Long> = _heartsDepletedTime.asStateFlow()

    init {
        checkDailyHeartsReset()
    }

    private fun checkDailyHeartsReset() {
        // Auto restore if 24 hours passed since depletion
        val depletedTime = prefs.getLong(KEY_HEARTS_DEPLETED_TIME, 0L)
        if (depletedTime > 0L) {
            val elapsed = System.currentTimeMillis() - depletedTime
            val twentyFourHoursMs = 24L * 60 * 60 * 1000L
            if (elapsed >= twentyFourHoursMs) {
                prefs.edit()
                    .putInt(KEY_HEARTS, 5)
                    .putLong(KEY_HEARTS_DEPLETED_TIME, 0L)
                    .apply()
                _hearts.value = 5
                _heartsDepletedTime.value = 0L
                return
            }
        }

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val lastResetDate = prefs.getString(KEY_LAST_HEART_RESET_DATE, "") ?: ""
        
        if (today != lastResetDate) {
            // New day: Grant 5 fresh hearts!
            prefs.edit()
                .putInt(KEY_HEARTS, 5)
                .putString(KEY_LAST_HEART_RESET_DATE, today)
                .putLong(KEY_HEARTS_DEPLETED_TIME, 0L)
                .apply()
            _hearts.value = 5
            _heartsDepletedTime.value = 0L
        } else {
            // Same day: Read remaining saved hearts
            _hearts.value = prefs.getInt(KEY_HEARTS, 5)
            _heartsDepletedTime.value = prefs.getLong(KEY_HEARTS_DEPLETED_TIME, 0L)
        }
    }

    fun useHeart(): Boolean {
        val current = _hearts.value
        if (current > 0) {
            val next = current - 1
            prefs.edit().putInt(KEY_HEARTS, next).apply()
            _hearts.value = next
            if (next == 0) {
                val now = System.currentTimeMillis()
                prefs.edit().putLong(KEY_HEARTS_DEPLETED_TIME, now).apply()
                _heartsDepletedTime.value = now
            }
            return true
        }
        return false
    }

    fun setHearts(amount: Int) {
        val next = amount.coerceIn(0, 5)
        prefs.edit().putInt(KEY_HEARTS, next).apply()
        _hearts.value = next
        if (next == 0) {
            val now = System.currentTimeMillis()
            prefs.edit().putLong(KEY_HEARTS_DEPLETED_TIME, now).apply()
            _heartsDepletedTime.value = now
        } else {
            prefs.edit().putLong(KEY_HEARTS_DEPLETED_TIME, 0L).apply()
            _heartsDepletedTime.value = 0L
        }
    }

    fun addHeart(amount: Int = 1) {
        val next = (_hearts.value + amount).coerceAtMost(5)
        prefs.edit().putInt(KEY_HEARTS, next).apply()
        _hearts.value = next
        if (next > 0) {
            prefs.edit().putLong(KEY_HEARTS_DEPLETED_TIME, 0L).apply()
            _heartsDepletedTime.value = 0L
        }
    }

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
        private const val KEY_HEARTS = "user_hearts_count"
        private const val KEY_LAST_HEART_RESET_DATE = "last_heart_reset_date"
        private const val KEY_HEARTS_DEPLETED_TIME = "hearts_depleted_timestamp"
    }
}

package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Clean Sound & Haptic Manager using Android SoundPool with synthesized audio beeps
 * and system vibrations.
 */
class SoundManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var isMuted: Boolean = false
    private var isHapticDisabled: Boolean = false

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    fun setSoundEnabled(enabled: Boolean) {
        isMuted = !enabled
    }

    fun setHapticEnabled(enabled: Boolean) {
        isHapticDisabled = !enabled
    }

    fun playHitFeedback() {
        if (!isHapticDisabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(35L, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(35L)
                }
            } catch (_: Exception) {}
        }
    }

    fun playMissFeedback() {
        if (!isHapticDisabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(15L, 80))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(15L)
                }
            } catch (_: Exception) {}
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}

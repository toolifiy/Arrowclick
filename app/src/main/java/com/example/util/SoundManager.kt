package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.concurrent.thread

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

    fun playSpawnTick() {
        // Silent as requested - no sounds or sound effects
    }

    fun playSuccessTick() {
        if (isMuted) return
        thread(start = true) {
            synthesizeAndPlaySuccessClick()
        }
    }

    private fun synthesizeAndPlaySuccessClick() {
        val sampleRate = 44100
        val durationMs = 120
        val numSamples = (durationMs * sampleRate / 1000)
        val sample = DoubleArray(numSamples)
        val generatedSnd = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            
            // Sweet arcade metallic bell / high quality ping tick
            // Sweeps UP slightly from 880Hz to 1100Hz (highly rewarding and satisfying)
            val freq1 = 880.0 + (220.0 * progress)
            // Harmonious overtone at 1.5x (perfect fifth) and 2.0x (perfect octave) for rich timbre
            val freq2 = freq1 * 1.5
            val freq3 = freq1 * 2.0

            val phase1 = 2.0 * Math.PI * freq1 * t
            val phase2 = 2.0 * Math.PI * freq2 * t
            val phase3 = 2.0 * Math.PI * freq3 * t

            // Exponential decay for clean, bell-like ping
            val envelope = if (progress < 0.05) {
                progress / 0.05 // ultra fast attack (2ms)
            } else {
                Math.exp(-6.5 * (progress - 0.05)) // beautiful bell decay
            }

            // Mix frequencies for a crisp, high-quality, metallic-wooden chime (Perfect Tap Sound)
            val wave = 0.5 * Math.sin(phase1) + 0.35 * Math.sin(phase2) + 0.15 * Math.sin(phase3)
            sample[i] = wave * envelope
        }

        for (i in 0 until numSamples) {
            generatedSnd[i] = (sample[i] * 32767).toInt().toShort()
        }

        try {
            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                generatedSnd.size * 2,
                AudioTrack.MODE_STATIC
            )
            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play()

            Thread.sleep(durationMs + 40L)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {}
    }

    fun playErrorTick() {
        // Silent as requested - no sounds or sound effects
    }

    private fun synthesizeAndPlay(frequency: Double, durationMs: Int) {
        val sampleRate = 44100
        val numSamples = (durationMs * sampleRate / 1000)
        val sample = DoubleArray(numSamples)
        val generatedSnd = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val envelope = if (i < numSamples * 0.15) {
                i / (numSamples * 0.15)
            } else if (i > numSamples * 0.70) {
                (numSamples - i) / (numSamples * 0.30)
            } else {
                1.0
            }
            sample[i] = Math.sin(2.0 * Math.PI * i / (sampleRate / frequency)) * envelope
        }

        for (i in 0 until numSamples) {
            generatedSnd[i] = (sample[i] * 32767).toInt().toShort()
        }

        try {
            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                generatedSnd.size * 2,
                AudioTrack.MODE_STATIC
            )
            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play()

            // Wait until done and release
            Thread.sleep(durationMs + 60L)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {}
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

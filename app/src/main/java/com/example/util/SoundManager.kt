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
 * Premium synthesized Sound & Haptic Manager utilizing Android AudioTrack
 * for low-latency, responsive gameplay sounds without external file dependencies.
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

    /**
     * Completely silent on arrow spawn as explicitly requested.
     */
    fun playSpawnTick() {
        // Silent - no sounds on arrow spawn!
    }

    /**
     * Crisp, ultra-short, highly satisfying "tik/tak" click sound.
     * Only plays on correct target clicks.
     */
    fun playSuccessTick() {
        if (isMuted) return
        thread(start = true) {
            synthesizeAndPlaySuccessClick()
        }
    }

    private fun synthesizeAndPlaySuccessClick() {
        val sampleRate = 44100
        val durationMs = 45 // Super responsive and short
        val numSamples = (durationMs * sampleRate / 1000)
        val sample = DoubleArray(numSamples)
        val generatedSnd = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples

            // Frequency sweeps downward from 1200Hz to 800Hz for a crisp woodblock "tik" sound
            val freq = 1200.0 - (400.0 * progress)
            val phase = 2.0 * Math.PI * freq * t

            // Exponential decay envelope for snappy attack and instant release
            val envelope = Math.exp(-9.0 * progress)

            sample[i] = Math.sin(phase) * envelope
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

            Thread.sleep(durationMs + 10L)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {}
    }

    /**
     * Loud, harsh, dissonant "BZZZT" warning sound played only on wrong clicks
     * to signal heart loss.
     */
    fun playWrongClick() {
        if (isMuted) return
        thread(start = true) {
            synthesizeAndPlayWrongClick()
        }
    }

    private fun synthesizeAndPlayWrongClick() {
        val sampleRate = 44100
        val durationMs = 220
        val numSamples = (durationMs * sampleRate / 1000)
        val sample = DoubleArray(numSamples)
        val generatedSnd = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples

            // Combine two dissonant frequencies (135Hz and 185Hz) to make a harsh buzz/error sound
            val f1 = 135.0
            val f2 = 185.0
            
            // Generate raw harsh square wave-like signal using sinusoids
            val w1 = Math.sin(2.0 * Math.PI * f1 * t)
            val w2 = Math.sin(2.0 * Math.PI * f2 * t)
            
            // Mix with some high-frequency buzzing harmonics
            val w3 = 0.3 * Math.sin(2.0 * Math.PI * (f1 * 3) * t)

            // Linear decay envelope for warning impact
            val envelope = (1.0 - progress) * 0.85

            sample[i] = (0.45 * w1 + 0.45 * w2 + 0.1 * w3) * envelope
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

            Thread.sleep(durationMs + 20L)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {}
    }

    /**
     * Custom 0.5s multi-stage retro-arcade Game Over drama crash sound.
     * Sounds like "dhidhid dhudhum tadak!"
     * Divided into:
     * - Phase 1 (0 to 150ms): "dhidhid" rapid pitch roll
     * - Phase 2 (150ms to 320ms): "dhudhum" deep heavy bass boom
     * - Phase 3 (320ms to 500ms): "tadak" sharp metallic crunch smash
     */
    fun playGameOverSound() {
        if (isMuted) return
        thread(start = true) {
            synthesizeAndPlayGameOver()
        }
    }

    private fun synthesizeAndPlayGameOver() {
        val sampleRate = 44100
        val durationMs = 500 // Exactly 0.5 seconds
        val numSamples = (durationMs * sampleRate / 1000)
        val sample = DoubleArray(numSamples)
        val generatedSnd = ShortArray(numSamples)

        val cut1 = (150 * sampleRate / 1000)
        val cut2 = (320 * sampleRate / 1000)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate

            if (i < cut1) {
                // 1. "dhidhid": Rapid pulsating low-frequency roll
                val rollProgress = i.toDouble() / cut1
                // Pulsate volume every 35ms (using absolute sine)
                val pulse = Math.abs(Math.sin(2.0 * Math.PI * 28.0 * t))
                val freq = 90.0 + (50.0 * rollProgress)
                val wave = Math.sin(2.0 * Math.PI * freq * t)
                sample[i] = wave * pulse * 0.75
            } else if (i < cut2) {
                // 2. "dhudhum": Deep heavy bass sweep boom
                val boomProgress = (i - cut1).toDouble() / (cut2 - cut1)
                // Sweeps down from 140Hz to 45Hz
                val freq = 140.0 - (95.0 * boomProgress)
                val envelope = Math.exp(-4.5 * boomProgress)
                val wave = Math.sin(2.0 * Math.PI * freq * t)
                sample[i] = wave * envelope * 0.9
            } else {
                // 3. "tadak": Sharp metallic high-pitched smash/crunch
                val smashProgress = (i - cut2).toDouble() / (numSamples - cut2)
                // Sweeps down from 1600Hz to 300Hz with noise crackle
                val freq = 1600.0 - (1300.0 * smashProgress)
                val envelope = (1.0 - smashProgress) * 0.8
                // Add pseudo-random metallic crackle noise
                val noise = Math.sin(2.0 * Math.PI * freq * t) * (1.0 + 0.35 * Math.sin(2.0 * Math.PI * 4500.0 * t))
                sample[i] = noise * envelope * 0.65
            }
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

            Thread.sleep(durationMs + 30L)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {}
    }

    fun playErrorTick() {
        // Kept for backward compatibility, completely silent.
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

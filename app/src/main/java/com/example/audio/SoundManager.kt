package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

object SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 22050
    var isMuted = false
    var sfxVolume = 0.85f

    fun playGunshot(isHeavy: Boolean = false, isSniper: Boolean = false) {
        if (isMuted) return
        scope.launch {
            try {
                val durationMs = if (isSniper) 320 else if (isHeavy) 180 else 130
                val numSamples = (SAMPLE_RATE * durationMs / 1000)
                val buffer = ShortArray(numSamples)
                val random = Random(System.nanoTime())

                val baseFreq = if (isSniper) 75.0 else if (isHeavy) 95.0 else 140.0
                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    val envelope = (1f - progress) * (1f - progress)
                    // Mix of low punch sine wave and white noise explosion
                    val sine = sin(2.0 * Math.PI * (baseFreq * (1.0 - progress * 0.5)) * i / SAMPLE_RATE).toFloat()
                    val noise = (random.nextFloat() * 2f - 1f) * (1f - progress * 0.8f)
                    val mixed = (sine * 0.6f + noise * 0.4f) * envelope * sfxVolume
                    buffer[i] = (mixed.coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort()
                }
                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playExplosion() {
        if (isMuted) return
        scope.launch {
            try {
                val durationMs = 450
                val numSamples = (SAMPLE_RATE * durationMs / 1000)
                val buffer = ShortArray(numSamples)
                val random = Random(System.nanoTime())
                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    val envelope = (1f - progress)
                    val subBass = sin(2.0 * Math.PI * 48.0 * (1.0 - progress * 0.4) * i / SAMPLE_RATE).toFloat()
                    val noise = (random.nextFloat() * 2f - 1f) * (1f - progress * 0.6f)
                    val sample = (subBass * 0.7f + noise * 0.5f) * envelope * sfxVolume
                    buffer[i] = (sample.coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort()
                }
                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playReload() {
        if (isMuted) return
        scope.launch {
            try {
                val durationMs = 120
                val numSamples = (SAMPLE_RATE * durationMs / 1000)
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    val env = (1f - progress)
                    val tone = sin(2.0 * Math.PI * 1800.0 * i / SAMPLE_RATE) * 0.5f +
                            sin(2.0 * Math.PI * 900.0 * i / SAMPLE_RATE) * 0.5f
                    buffer[i] = (tone * env * 0.4f * sfxVolume * Short.MAX_VALUE).toInt().toShort()
                }
                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playVehicleHorn() {
        if (isMuted) return
        scope.launch {
            try {
                val durationMs = 200
                val numSamples = (SAMPLE_RATE * durationMs / 1000)
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val tone = sin(2.0 * Math.PI * 440.0 * i / SAMPLE_RATE) * 0.6f +
                            sin(2.0 * Math.PI * 554.0 * i / SAMPLE_RATE) * 0.4f
                    buffer[i] = (tone * 0.5f * sfxVolume * Short.MAX_VALUE).toInt().toShort()
                }
                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playZoneWarning() {
        if (isMuted) return
        scope.launch {
            try {
                val durationMs = 300
                val numSamples = (SAMPLE_RATE * durationMs / 1000)
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val freq = 600.0 + 300.0 * sin(2.0 * Math.PI * 4.0 * t)
                    val sample = sin(2.0 * Math.PI * freq * t) * 0.4f * sfxVolume
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
                }
                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playButtonClick() {
        if (isMuted) return
        scope.launch {
            try {
                val durationMs = 40
                val numSamples = (SAMPLE_RATE * durationMs / 1000)
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    val tone = sin(2.0 * Math.PI * 1200.0 * i / SAMPLE_RATE) * (1f - progress)
                    buffer[i] = (tone * 0.3f * sfxVolume * Short.MAX_VALUE).toInt().toShort()
                }
                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playVictoryFanfare() {
        if (isMuted) return
        scope.launch {
            try {
                val durationMs = 650
                val numSamples = (SAMPLE_RATE * durationMs / 1000)
                val buffer = ShortArray(numSamples)
                val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50) // C-E-G-C high
                val segLen = numSamples / 4
                for (i in 0 until numSamples) {
                    val noteIdx = (i / segLen).coerceIn(0, 3)
                    val freq = notes[noteIdx]
                    val sample = sin(2.0 * Math.PI * freq * i / SAMPLE_RATE) * 0.5f * sfxVolume
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
                }
                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    private fun playPcm(buffer: ShortArray) {
        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2.coerceAtLeast(minBufferSize))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        // AudioTrack will play static buffer and clean up
        scope.launch {
            kotlinx.coroutines.delay((buffer.size * 1000L / SAMPLE_RATE) + 150)
            try {
                track.stop()
                track.release()
            } catch (_: Exception) {}
        }
    }
}

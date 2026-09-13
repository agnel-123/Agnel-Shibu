package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SpaceAudioEngine {
    private var isPlaying = false
    private var audioJob: Job? = null
    private var track: AudioTrack? = null
    private val sampleRate = 44100

    fun toggle(): Boolean {
        if (isPlaying) {
            stop()
            return false
        } else {
            start()
            return true
        }
    }

    fun isAudioPlaying(): Boolean = isPlaying

    fun start() {
        if (isPlaying) return
        isPlaying = true

        val minBufSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = maxOf(minBufSize, sampleRate / 2)

        try {
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            track?.play()

            audioJob = CoroutineScope(Dispatchers.Default).launch {
                val shortBuffer = ShortArray(2048)
                var phase1 = 0.0
                var phase2 = 0.0
                var phase3 = 0.0
                var chimePhase = 0.0
                var chimeEnv = 0.0
                var chimeFreq = 528.0

                val chimeNotes = doubleArrayOf(432.0, 528.0, 648.0, 720.0, 864.0)
                var tickCounter = 0

                while (isActive && isPlaying) {
                    tickCounter++
                    if (tickCounter % 80 == 0 && Math.random() < 0.35) {
                        chimeFreq = chimeNotes[(Math.random() * chimeNotes.size).toInt()]
                        chimeEnv = 1.0
                    }

                    for (i in shortBuffer.indices) {
                        // Ambient cosmic drone chords (110Hz A2 + 165Hz E3 + 220Hz A3)
                        phase1 += 2 * PI * 110.0 / sampleRate
                        phase2 += 2 * PI * 164.8 / sampleRate
                        phase3 += 2 * PI * 220.0 / sampleRate
                        chimePhase += 2 * PI * chimeFreq / sampleRate

                        val drone = (sin(phase1) * 0.25 + sin(phase2) * 0.18 + sin(phase3) * 0.12)
                        val chime = sin(chimePhase) * chimeEnv * 0.35
                        chimeEnv = maxOf(0.0, chimeEnv - 0.00002)

                        val totalSample = (drone + chime) * 0.3
                        val clamped = totalSample.coerceIn(-1.0, 1.0)
                        shortBuffer[i] = (clamped * 32767.0).toInt().toShort()
                    }
                    track?.write(shortBuffer, 0, shortBuffer.size)
                }
            }
        } catch (_: Exception) {
            isPlaying = false
        }
    }

    fun stop() {
        isPlaying = false
        audioJob?.cancel()
        audioJob = null
        try {
            track?.stop()
            track?.release()
        } catch (_: Exception) {}
        track = null
    }

    fun playChime(freq: Double = 587.3) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val durationMs = 600
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                var phase = 0.0
                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val env = (1.0 - progress) * (1.0 - progress)
                    phase += 2 * PI * freq / sampleRate
                    val sample = sin(phase) * env * 0.25
                    buffer[i] = (sample * 32767.0).toInt().toShort()
                }
                val quickTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                quickTrack.write(buffer, 0, buffer.size)
                quickTrack.play()
                delay(durationMs.toLong() + 50)
                quickTrack.release()
            } catch (_: Exception) {}
        }
    }
}

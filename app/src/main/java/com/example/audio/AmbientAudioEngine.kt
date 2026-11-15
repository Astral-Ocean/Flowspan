package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.model.AmbientTrack
import com.example.model.TransitionChime
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Zero-dependency procedural ambient soundscape synthesizer & transition chime generator.
 * Separates serialized control commands from blocking PCM stream generation for instant,
 * stutter-free soundscape switching and crisp phase change cues.
 */
class AmbientAudioEngine(@Suppress("UNUSED_PARAMETER") context: Context) {

  private val controlScope =
    CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1))
  private val synthScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  private var playbackJob: Job? = null
  private var chimeJob: Job? = null

  @Volatile private var playbackToken: Long = 0L
  @Volatile private var targetVolume: Float = 0.65f
  @Volatile private var currentTrack: AmbientTrack = AmbientTrack.NONE
  @Volatile private var isPlayingState: Boolean = false

  fun startOrUpdate(track: AmbientTrack, volume: Float, shouldPlay: Boolean) {
    val clampedVol = volume.coerceIn(0f, 1f)
    controlScope.launch {
      targetVolume = clampedVol
      if (!shouldPlay || track == AmbientTrack.NONE) {
        stopInternal()
        return@launch
      }

      if (isPlayingState && currentTrack == track) {
        return@launch
      }

      stopInternal()
      currentTrack = track
      isPlayingState = true
      val myToken = ++playbackToken
      startProceduralSynth(track, myToken)
    }
  }

  fun previewBrief(track: AmbientTrack, volume: Float) {
    startOrUpdate(track, volume, shouldPlay = track != AmbientTrack.NONE)
  }

  fun stop() {
    controlScope.launch { stopInternal() }
  }

  private suspend fun stopInternal() {
    playbackToken++
    isPlayingState = false
    currentTrack = AmbientTrack.NONE
    val oldJob = playbackJob
    playbackJob = null
    oldJob?.cancelAndJoin()
  }

  /**
   * Preview a selected transition chime immediately from Settings.
   */
  fun previewTransitionChime(chime: TransitionChime) {
    playTransitionChime(chime, isBreakStarting = true)
  }

  /**
   * Play the selected transition sound prompt when timer phase changes.
   */
  fun playTransitionChime(chime: TransitionChime, isBreakStarting: Boolean) {
    if (chime == TransitionChime.NONE) return

    chimeJob?.cancel()
    chimeJob = synthScope.launch {
      val sampleRate = 44100
      val durationSec =
        when (chime) {
          TransitionChime.WOODBLOCK -> 0.38
          TransitionChime.DROPLET -> 0.55
          TransitionChime.HARMONIC -> 1.85
          TransitionChime.BELL -> 1.65
          TransitionChime.NONE -> return@launch
        }
      val totalFrames = (sampleRate * durationSec).toInt()
      val pcm = ShortArray(totalFrames * 2)
      val vol = targetVolume.coerceAtLeast(0.40f).toDouble()

      val baseFreq = if (isBreakStarting) 523.25 else 587.33 // C5 or D5

      for (i in 0 until totalFrames) {
        val t = i.toDouble() / sampleRate
        var sigL = 0.0
        var sigR = 0.0

        when (chime) {
          TransitionChime.BELL -> {
            val fifthFreq = baseFreq * 1.5
            val octFreq = baseFreq * 2.0
            val env = exp(-t * 2.5) * (t / 0.015).coerceAtMost(1.0)
            val bell =
              (sin(2.0 * PI * baseFreq * t) * 0.60 +
                sin(2.0 * PI * fifthFreq * t) * 0.28 +
                sin(2.0 * PI * octFreq * t) * 0.12) * env * vol * 0.48
            sigL = bell
            sigR = bell
          }

          TransitionChime.WOODBLOCK -> {
            // Meditative percussive knock: hollow drop from 440Hz -> 330Hz with rapid decay
            val pitch = 330.0 + 120.0 * exp(-t * 35.0)
            val env = exp(-t * 14.0) * (t / 0.003).coerceAtMost(1.0)
            val knock =
              (sin(2.0 * PI * pitch * t) * 0.75 +
                sin(2.0 * PI * pitch * 2.08 * t) * 0.25) * env * vol * 0.55
            sigL = knock
            sigR = knock
          }

          TransitionChime.DROPLET -> {
            // Spring water droplet: subtle initial dip then smooth upward glide with crystalline decay
            val pitch = 1150.0 + 480.0 * (1.0 - exp(-t * 40.0))
            val env = exp(-t * 7.5) * (t / 0.008).coerceAtMost(1.0)
            val droplet = sin(2.0 * PI * pitch * t) * env * vol * 0.42
            sigL = droplet * 0.95
            sigR = droplet * 1.05
          }

          TransitionChime.HARMONIC -> {
            // Gentle 3-note peaceful triad (Root, Major Third, Fifth) with slow smooth swell
            val f1 = baseFreq
            val f2 = baseFreq * 1.2599 // Major 3rd
            val f3 = baseFreq * 1.4983 // Perfect 5th
            val attack = (t / 0.08).coerceAtMost(1.0)
            val env = exp(-t * 1.6) * attack
            val harm =
              (sin(2.0 * PI * f1 * t) * 0.45 +
                sin(2.0 * PI * f2 * t) * 0.32 +
                sin(2.0 * PI * f3 * t) * 0.23) * env * vol * 0.45
            sigL = harm
            sigR = harm
          }

          TransitionChime.NONE -> {}
        }

        val sampleL = (sigL * Short.MAX_VALUE).toInt()
          .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        val sampleR = (sigR * Short.MAX_VALUE).toInt()
          .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        pcm[i * 2] = sampleL
        pcm[i * 2 + 1] = sampleR
      }

      var track: AudioTrack? = null
      try {
        track =
          AudioTrack.Builder()
            .setAudioAttributes(
              AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            )
            .setAudioFormat(
              AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .build()
            )
            .setBufferSizeInBytes(pcm.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(pcm, 0, pcm.size)
        track.play()
        kotlinx.coroutines.delay((durationSec * 1000 + 100).toLong())
      } catch (_: Throwable) {
        // Safe for Robolectric / headless environments
      } finally {
        try {
          track?.release()
        } catch (_: Throwable) {}
      }
    }
  }

  fun release() {
    playbackToken++
    isPlayingState = false
    currentTrack = AmbientTrack.NONE
    playbackJob?.cancel()
    playbackJob = null
    chimeJob?.cancel()
    chimeJob = null
    controlScope.cancel()
    synthScope.cancel()
  }

  private fun startProceduralSynth(track: AmbientTrack, myToken: Long) {
    playbackJob =
      synthScope.launch {
        val sampleRate = 44100
        val minBuf =
          AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT,
          )
        if (minBuf <= 0) return@launch
        val bufferFrames = 1024
        val trackBufferBytes = maxOf(minBuf, bufferFrames * 4 * 2)
        val pcmBuffer = ShortArray(bufferFrames * 2)

        var audioTrack: AudioTrack? = null
        try {
          audioTrack =
            AudioTrack.Builder()
              .setAudioAttributes(
                AudioAttributes.Builder()
                  .setUsage(AudioAttributes.USAGE_MEDIA)
                  .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                  .build()
              )
              .setAudioFormat(
                AudioFormat.Builder()
                  .setSampleRate(sampleRate)
                  .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                  .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                  .build()
              )
              .setBufferSizeInBytes(trackBufferBytes)
              .setTransferMode(AudioTrack.MODE_STREAM)
              .build()

          if (!isActive || !isPlayingState || playbackToken != myToken) return@launch
          audioTrack.play()

          val rng = Random(System.nanoTime())
          var b0 = 0.0
          var b1 = 0.0
          var b2 = 0.0
          var lpL = 0.0
          var lpR = 0.0
          var brown = 0.0

          // Kellet 3-pole filters for stereo Pink Noise
          var pk0L = 0.0; var pk1L = 0.0; var pk2L = 0.0; var pk3L = 0.0; var pk4L = 0.0; var pk5L = 0.0; var pk6L = 0.0
          var pk0R = 0.0; var pk1R = 0.0; var pk2R = 0.0; var pk3R = 0.0; var pk4R = 0.0; var pk5R = 0.0; var pk6R = 0.0

          var fadeGain = 0.0
          var sampleIndex = 0L

          val bowlFreqs = doubleArrayOf(146.83, 220.0, 293.66, 369.99, 440.0)
          val bowlPhases = DoubleArray(bowlFreqs.size)

          while (isActive && isPlayingState && playbackToken == myToken) {
            val vol = targetVolume.toDouble()
            for (i in 0 until bufferFrames) {
              if (fadeGain < 1.0) {
                fadeGain = (fadeGain + 1.0 / (sampleRate * 0.45)).coerceAtMost(1.0)
              }
              val t = sampleIndex.toDouble() / sampleRate.toDouble()
              sampleIndex++

              var left = 0.0
              var right = 0.0

              when (track) {
                AmbientTrack.PINK_NOISE -> {
                  // True 1/f Pink Noise with stereo spatial decorrelation
                  val whiteL = rng.nextDouble(-1.0, 1.0)
                  val whiteR = rng.nextDouble(-1.0, 1.0)

                  pk0L = 0.99886 * pk0L + whiteL * 0.0555179
                  pk1L = 0.99332 * pk1L + whiteL * 0.0750759
                  pk2L = 0.96900 * pk2L + whiteL * 0.1538520
                  pk3L = 0.86650 * pk3L + whiteL * 0.3104856
                  pk4L = 0.55000 * pk4L + whiteL * 0.5329522
                  pk5L = -0.7616 * pk5L - whiteL * 0.0168980
                  left = (pk0L + pk1L + pk2L + pk3L + pk4L + pk5L + pk6L + whiteL * 0.5362) * 0.115
                  pk6L = whiteL * 0.115926

                  pk0R = 0.99886 * pk0R + whiteR * 0.0555179
                  pk1R = 0.99332 * pk1R + whiteR * 0.0750759
                  pk2R = 0.96900 * pk2R + whiteR * 0.1538520
                  pk3R = 0.86650 * pk3R + whiteR * 0.3104856
                  pk4R = 0.55000 * pk4R + whiteR * 0.5329522
                  pk5R = -0.7616 * pk5R - whiteR * 0.0168980
                  right = (pk0R + pk1R + pk2R + pk3R + pk4R + pk5R + pk6R + whiteR * 0.5362) * 0.115
                  pk6R = whiteR * 0.115926
                }

                AmbientTrack.SOFT_RAIN -> {
                  val whiteL = rng.nextDouble(-1.0, 1.0)
                  val whiteR = rng.nextDouble(-1.0, 1.0)
                  b0 = 0.997 * b0 + whiteL * 0.029
                  b1 = 0.985 * b1 + whiteL * 0.032
                  b2 = 0.950 * b2 + whiteR * 0.045
                  val rainL = b0 + b1 + whiteL * 0.025
                  val rainR = b0 * 0.85 + b2 + whiteR * 0.025
                  lpL += 0.14 * (rainL - lpL)
                  lpR += 0.14 * (rainR - lpR)
                  val swell = 0.85 + 0.15 * sin(2.0 * PI * 0.08 * t)
                  left = lpL * 1.45 * swell
                  right = lpR * 1.45 * swell
                }

                AmbientTrack.WARM_STUDY -> {
                  val white = rng.nextDouble(-1.0, 1.0)
                  brown = (brown + white * 0.02).coerceIn(-1.0, 1.0) * 0.996
                  val crackle =
                    if (rng.nextDouble() < 0.0007) rng.nextDouble(-0.45, 0.45) else 0.0
                  lpL += 0.08 * (brown * 1.6 + crackle * 0.7 - lpL)
                  lpR += 0.08 * (brown * 1.6 + crackle * 0.5 - lpR)
                  left = lpL
                  right = lpR
                }

                AmbientTrack.MORNING_BREEZE -> {
                  val whiteL = rng.nextDouble(-1.0, 1.0)
                  val whiteR = rng.nextDouble(-1.0, 1.0)
                  b0 = 0.994 * b0 + whiteL * 0.025
                  b1 = 0.994 * b1 + whiteR * 0.025
                  val gustEnv =
                    0.35 + 0.65 * (0.5 + 0.5 * sin(2.0 * PI * 0.065 * t + sin(2.0 * PI * 0.017 * t)))
                  lpL += 0.06 * (b0 * 2.2 - lpL)
                  lpR += 0.06 * (b1 * 2.2 - lpR)
                  val pan = 0.5 + 0.25 * sin(2.0 * PI * 0.05 * t)
                  left = lpL * gustEnv * (1.2 - pan * 0.4)
                  right = lpR * gustEnv * (0.8 + pan * 0.4)
                }

                AmbientTrack.SINGING_BOWL -> {
                  var sumL = 0.0
                  var sumR = 0.0
                  for (k in bowlFreqs.indices) {
                    val f = bowlFreqs[k]
                    val breath =
                      0.35 + 0.65 * (0.5 + 0.5 * sin(2.0 * PI * (0.04 + k * 0.013) * t + k * 1.1))
                    bowlPhases[k] += 2.0 * PI * f / sampleRate
                    if (bowlPhases[k] > 2.0 * PI) bowlPhases[k] -= 2.0 * PI
                    val partial = sin(bowlPhases[k]) + 0.12 * sin(bowlPhases[k] * 2.0)
                    val amp = (0.14 / (1.0 + k * 0.35)) * breath
                    val stereoOffset = if (k % 2 == 0) 0.58 else 0.42
                    sumL += partial * amp * stereoOffset
                    sumR += partial * amp * (1.0 - stereoOffset)
                  }
                  left = sumL * 1.5
                  right = sumR * 1.5
                }

                AmbientTrack.NONE -> {}
              }

              val outL = (left * vol * fadeGain * 0.55 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
              val outR = (right * vol * fadeGain * 0.55 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())

              pcmBuffer[i * 2] = outL.toShort()
              pcmBuffer[i * 2 + 1] = outR.toShort()
            }

            if (isActive && isPlayingState && playbackToken == myToken) {
              audioTrack.write(pcmBuffer, 0, pcmBuffer.size)
            }
          }
        } catch (_: Throwable) {
          // Gracefully ignore in environments without audio hardware (e.g., Robolectric)
        } finally {
          try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
          } catch (_: Throwable) {}
        }
      }
  }
}

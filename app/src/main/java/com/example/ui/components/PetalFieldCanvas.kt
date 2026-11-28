package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.example.ui.theme.LocalFlowspanPalette
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private const val PI_F = PI.toFloat()
private const val TWO_PI_F = (2.0 * PI).toFloat()

internal class PetalParticle {
  var x = 0f
  var y = 0f
  var vx = 0f
  var vy = 0f
  var theta = 0f // Pitch/tilt angle of the petal plane in radians
  var omega = 0f // Angular velocity (rad/s)
  var radiusPx = 12f
  var wingLoading = 1f // Mass-to-area ratio controlling terminal speed & wind responsiveness
  var colorIndex = 0
}

private enum class SpawnOrigin {
  TOP,
  UPPER_LEFT,
  UPPER_RIGHT,
}

/**
 * Physically grounded 2D falling-petal aerodynamic simulation:
 * - Analytical divergence-free 2D Curl Noise fluid wind field (∇ × Ψ) for coherent eddies.
 * - Coupled translational-rotational falling-plate ODE (Andersen, Pesavento & Wang model)
 *   solved via zero-allocation vector trig identities for rock-solid 60-120fps performance.
 * - Traveling raised-cosine wind gusts with spatial phase propagation across the viewport.
 */
internal class PetalSimulationEngine(private val dpToPx: Float) {
  private val rng = Random(System.nanoTime())
  private val minRadiusPx = 5.4f * dpToPx
  private val maxRadiusPx = 10.0f * dpToPx

  val poolSize = 18
  private val particles = Array(poolSize) { PetalParticle() }

  var canvasWidth: Float = 0f
  var canvasHeight: Float = 0f
  var initialized: Boolean = false
    private set

  private var simTime: Float = 0f

  // Traveling macro wind gust state
  private var nextGustCountdown = 3.5f + rng.nextFloat() * 4.0f
  private var gustActive = false
  private var gustElapsed = 0f
  private var gustDuration = 4.2f
  private var gustDirection = 1f
  private var gustPeakSpeed = 0f
  private var gustLiftRatio = 0.22f
  private var nextFlurryCountdown = 12f + rng.nextFloat() * 10f

  // Interactive localized touch breeze
  private var touchX = -1000f
  private var touchY = -1000f
  private var touchVx = 0f
  private var touchVy = 0f
  private var touchStrength = 0f

  fun ensureDimensions(width: Float, height: Float) {
    if (width <= 1f || height <= 1f) return
    canvasWidth = width
    canvasHeight = height
    if (!initialized) {
      for (i in 0 until poolSize) {
        spawnPetal(particles[i], width, height, initialSeed = true)
      }
      initialized = true
    }
  }

  fun triggerTapBreeze(x: Float, y: Float) {
    touchX = x
    touchY = y
    touchVx = (if (rng.nextBoolean()) 1f else -1f) * 190f * dpToPx
    touchVy = -110f * dpToPx
    touchStrength = 1f
  }

  fun triggerDragBreeze(x: Float, y: Float, dx: Float, dy: Float) {
    touchX = x
    touchY = y
    touchVx = (dx * 18f).coerceIn(-360f * dpToPx, 360f * dpToPx)
    touchVy = (dy * 12f).coerceIn(-240f * dpToPx, 240f * dpToPx)
    touchStrength = 1f
  }

  fun step(dtRaw: Float, windEventsEnabled: Boolean) {
    val w = canvasWidth
    val h = canvasHeight
    if (w <= 1f || h <= 1f) return
    if (!initialized) {
      ensureDimensions(w, h)
    }

    val totalDt = dtRaw.coerceIn(0.001f, 0.045f)
    val dt = totalDt

    updateMacroEvents(totalDt, w, h, windEventsEnabled)

    if (touchStrength > 0.005f) {
      touchStrength = (touchStrength * (1f - 2.6f * totalDt)).coerceAtLeast(0f)
    } else {
      touchStrength = 0f
    }

    // Physical constants scaled to dp-space
    val gravity = 225f * dpToPx
    val dragBase = 0.042f / dpToPx
    val dragAngleGain = 0.078f / dpToPx
    val liftCoeff = 0.056f / dpToPx
    val torqueCoeff = 0.0024f / (dpToPx * dpToPx)
    val rotDamping = 2.6f
    val maxSpeed = 420f * dpToPx
    val exitMargin = 68f * dpToPx
    val touchRadius = 128f * dpToPx
    val touchRadiusSq = touchRadius * touchRadius
    val invTouchRadiusSq = 1f / touchRadiusSq
    val baseDriftX = 6f * dpToPx

    // Precompute spatial wave scales for divergence-free 2D curl noise
    val invScale1 = 1f / (210f * dpToPx)
    val invScale2 = 1f / (125f * dpToPx)
    val curlAmp1 = 36f * dpToPx
    val curlAmp2 = 20f * dpToPx
    val invWidth = 1f / w

    val hasTouch = touchStrength > 0.02f
    val gustRatio = if (gustActive) (gustElapsed / gustDuration).coerceIn(0f, 1f) else 0f

    simTime += dt
    val t1x = simTime * 0.42f
    val t1y = simTime * 0.27f
    val t2 = simTime * 0.68f
    val t2y = t2 * 0.7f

      for (i in 0 until poolSize) {
        val p = particles[i]

        // 1. Divergence-free 2D Curl Noise wind field: u = ∂Ψ/∂y, v = -∂Ψ/∂x
        val ax1 = p.x * invScale1 + t1x
        val ay1 = p.y * invScale1 - t1y
        val sinAx1 = sin(ax1)
        val cosAx1 = cos(ax1)
        val sinAy1 = sin(ay1)
        val cosAy1 = cos(ay1)

        val x2 = p.x * invScale2
        val y2 = p.y * invScale2
        val uRot = 0.8f * x2 + 0.6f * y2 - t2
        val vRot = -0.6f * x2 + 0.8f * y2 + t2y
        val sinU = sin(uRot)
        val cosU = cos(uRot)
        val sinV = sin(vRot)
        val cosV = cos(vRot)
        val cosUV = cosU * cosV
        val sinUV = sinU * sinV

        var windX =
          -curlAmp1 * sinAx1 * sinAy1 +
            curlAmp2 * (0.6f * cosUV - 0.8f * sinUV) +
            baseDriftX
        var windY =
          -curlAmp1 * cosAx1 * cosAy1 -
            curlAmp2 * (0.8f * cosUV + 0.6f * sinUV)

        // 2. Traveling macro wind gust front (smooth raised-cosine wave propagating across x)
        if (gustActive) {
          val xNorm = (p.x * invWidth).coerceIn(-0.2f, 1.2f)
          val spatialLag = if (gustDirection > 0f) xNorm else (1f - xNorm)
          val localPhase = (gustRatio * 1.28f - spatialLag * 0.28f).coerceIn(0f, 1f)
          val envelope = 0.5f * (1f - cos(TWO_PI_F * localPhase))
          windX += gustDirection * gustPeakSpeed * envelope
          windY -= gustPeakSpeed * gustLiftRatio * envelope
        }

        // 3. Localized touch breeze impulse (compact C1 smooth falloff without hypot/exp)
        if (hasTouch) {
          val dx = p.x - touchX
          val dy = p.y - touchY
          val d2 = dx * dx + dy * dy
          if (d2 < touchRadiusSq) {
            val wFalloff = 1f - d2 * invTouchRadiusSq
            val falloff = wFalloff * wFalloff * touchStrength
            windX += touchVx * falloff
            windY += touchVy * falloff
          }
        }

        // 4. Relative velocity & exact vector trig identities (avoids atan2, sin(alpha), sin(2*alpha))
        val relVx = p.vx - windX
        val relVy = p.vy - windY
        val relSpeedSq = relVx * relVx + relVy * relVy
        val relSpeed = sqrt(relSpeedSq.coerceAtLeast(1e-6f))
        val invSpeed = 1f / relSpeed

        val cosPhi = relVx * invSpeed
        val sinPhi = relVy * invSpeed
        val sinTheta = sin(p.theta)
        val cosTheta = cos(p.theta)

        // α = θ - φ
        val sinAlpha = sinTheta * cosPhi - cosTheta * sinPhi
        val cosAlpha = cosTheta * cosPhi + sinTheta * sinPhi
        val sin2Alpha = 2f * sinAlpha * cosAlpha

        val invWing = 1f / p.wingLoading

        // Quadratic aerodynamic drag: F_d ∝ -(Cd0 + Cd1·sin²α)·|v_rel|·v_rel
        val cd = dragBase + dragAngleGain * (sinAlpha * sinAlpha)
        val dragFactor = cd * relSpeed * invWing
        val axDrag = -dragFactor * relVx
        val ayDrag = -dragFactor * relVy

        // Aerodynamic lift perpendicular to relative flow: F_L ∝ CL·sin(2α)·v_rel²·n⊥
        val liftMag = liftCoeff * sin2Alpha * relSpeedSq * invWing
        val axLift = liftMag * (-sinPhi)
        val ayLift = liftMag * cosPhi

        // Total translational acceleration
        val ax = axDrag + axLift
        val ay = gravity + ayDrag + ayLift

        // 5. Rotational flutter torque: center-of-pressure restoring moment + damping
        val aeroTorque = -torqueCoeff * relSpeedSq * sin2Alpha * invWing
        val flutterDrive = -1.6f * sinTheta - rotDamping * p.omega
        val alphaRot = aeroTorque + flutterDrive

        // Symplectic Euler integration
        p.omega = (p.omega + alphaRot * dt).coerceIn(-6.5f, 6.5f)
        p.theta += p.omega * dt
        if (p.theta > PI_F) p.theta -= TWO_PI_F
        if (p.theta < -PI_F) p.theta += TWO_PI_F

        p.vx = (p.vx + ax * dt).coerceIn(-maxSpeed, maxSpeed)
        p.vy = (p.vy + ay * dt).coerceIn(-maxSpeed, maxSpeed)
        p.x += p.vx * dt
        p.y += p.vy * dt
      }

    // Recycle only once completely outside viewport bounds
    for (i in 0 until poolSize) {
      val p = particles[i]
      val offBottom = p.y > h + exitMargin
      val offLeft = p.x < -exitMargin && p.vx <= 0f
      val offRight = p.x > w + exitMargin && p.vx >= 0f
      val offTopFar = p.y < -h * 0.28f && p.vy < 0f
      if (offBottom || offLeft || offRight || offTopFar) {
        spawnPetal(p, w, h, initialSeed = false)
      }
    }
  }

  private fun updateMacroEvents(dt: Float, w: Float, h: Float, windEventsEnabled: Boolean) {
    if (!windEventsEnabled) {
      gustActive = false
      return
    }

    if (!gustActive) {
      nextGustCountdown -= dt
      if (nextGustCountdown <= 0f) {
        gustActive = true
        gustElapsed = 0f
        gustDuration = 3.6f + rng.nextFloat() * 2.4f
        gustDirection = if (rng.nextBoolean()) 1f else -1f
        gustPeakSpeed = (115f + rng.nextFloat() * 95f) * dpToPx
        gustLiftRatio = 0.16f + rng.nextFloat() * 0.16f
      }
    } else {
      gustElapsed += dt
      if (gustElapsed >= gustDuration) {
        gustActive = false
        nextGustCountdown = 7.0f + rng.nextFloat() * 8.0f
      }
    }

    // Occasional upper-side blossom cluster entering coherently on a gentle current
    nextFlurryCountdown -= dt
    if (nextFlurryCountdown <= 0f) {
      nextFlurryCountdown = 15f + rng.nextFloat() * 11f
      val fromLeft = rng.nextBoolean()
      val clusterY = h * (0.04f + rng.nextFloat() * 0.22f)
      var triggered = 0
      for (i in 0 until poolSize) {
        val p = particles[i]
        if (p.y < 0f || p.y > h * 0.90f) {
          p.x =
            if (fromLeft) -p.radiusPx * 2.2f - triggered * 18f * dpToPx
            else w + p.radiusPx * 2.2f + triggered * 18f * dpToPx
          p.y = clusterY + (rng.nextFloat() - 0.5f) * 40f * dpToPx
          p.vx = (if (fromLeft) 1f else -1f) * (44f + rng.nextFloat() * 26f) * dpToPx
          p.vy = (18f + rng.nextFloat() * 22f) * dpToPx
          p.theta = (rng.nextFloat() - 0.5f) * 0.9f
          p.omega = (rng.nextFloat() - 0.5f) * 1.4f
          triggered++
          if (triggered >= 4) break
        }
      }
    }
  }

  fun particleAt(index: Int): PetalParticle = particles[index]

  private fun spawnPetal(p: PetalParticle, width: Float, height: Float, initialSeed: Boolean) {
    val r = minRadiusPx + rng.nextFloat() * (maxRadiusPx - minRadiusPx)
    p.radiusPx = r
    val sizeRatio = (r - minRadiusPx) / (maxRadiusPx - minRadiusPx)
    // Wing loading scales moderately with size: larger discs settle slightly faster
    p.wingLoading = 0.78f + sizeRatio * 0.44f
    p.colorIndex = rng.nextInt(4)
    p.theta = (rng.nextFloat() - 0.5f) * 1.1f
    p.omega = (rng.nextFloat() - 0.5f) * 1.6f

    val margin = 48f * dpToPx
    if (initialSeed) {
      p.x = rng.nextFloat() * (width + margin) - margin * 0.5f
      p.y = rng.nextFloat() * (height * 0.94f) - margin * 0.25f
      p.vx = (rng.nextFloat() - 0.5f) * 28f * dpToPx
      p.vy = (28f + rng.nextFloat() * 26f) * dpToPx
      return
    }

    val roll = rng.nextFloat()
    val origin =
      when {
        roll < 0.58f -> SpawnOrigin.TOP
        roll < 0.79f -> SpawnOrigin.UPPER_LEFT
        else -> SpawnOrigin.UPPER_RIGHT
      }

    when (origin) {
      SpawnOrigin.TOP -> {
        p.x = rng.nextFloat() * (width * 1.14f) - width * 0.07f
        p.y = -r * 2.2f - rng.nextFloat() * 42f * dpToPx
        p.vx = (rng.nextFloat() - 0.5f) * 22f * dpToPx
        p.vy = (24f + rng.nextFloat() * 20f) * dpToPx
      }
      SpawnOrigin.UPPER_LEFT -> {
        p.x = -r * 2.2f - rng.nextFloat() * 22f * dpToPx
        p.y = height * (0.02f + rng.nextFloat() * 0.38f)
        p.vx = (28f + rng.nextFloat() * 32f) * dpToPx
        p.vy = (18f + rng.nextFloat() * 18f) * dpToPx
      }
      SpawnOrigin.UPPER_RIGHT -> {
        p.x = width + r * 2.2f + rng.nextFloat() * 22f * dpToPx
        p.y = height * (0.02f + rng.nextFloat() * 0.38f)
        p.vx = -(28f + rng.nextFloat() * 32f) * dpToPx
        p.vy = (18f + rng.nextFloat() * 18f) * dpToPx
      }
    }
  }
}

/**
 * Renders opaque circular petals driven by 2D Curl Noise wind turbulence and coupled
 * falling-plate aerodynamics. Layered ABOVE the center ring and BELOW the bottom buttons.
 */
@Composable
fun PetalFieldCanvas(
  windEventsEnabled: Boolean,
  modifier: Modifier = Modifier,
  isPaused: Boolean = false,
) {
  val palette = LocalFlowspanPalette.current
  val currentPalette by rememberUpdatedState(palette)
  val windEnabledState by rememberUpdatedState(windEventsEnabled)
  val pausedState by rememberUpdatedState(isPaused)

  val dpToPx = LocalDensity.current.density
  val engine = remember(dpToPx) { PetalSimulationEngine(dpToPx) }
  var frameNanos by remember { mutableLongStateOf(0L) }

  LaunchedEffect(engine, isPaused) {
    if (isPaused) return@LaunchedEffect
    var lastNanos = 0L
    while (true) {
      withFrameNanos { now ->
        if (!pausedState && lastNanos != 0L) {
          val dt = (now - lastNanos) / 1_000_000_000f
          engine.step(dt, windEnabledState)
        }
        lastNanos = now
        frameNanos = now
      }
    }
  }

  Canvas(
    modifier =
      modifier
        .fillMaxSize()
        .graphicsLayer()
        .pointerInput(engine) {
          awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            engine.triggerTapBreeze(down.position.x, down.position.y)
            var prev = down.position
            while (true) {
              val event = awaitPointerEvent()
              val change = event.changes.firstOrNull() ?: break
              if (!change.pressed) break
              val dx = change.position.x - prev.x
              val dy = change.position.y - prev.y
              if (dx * dx + dy * dy > 1f) {
                engine.triggerDragBreeze(change.position.x, change.position.y, dx, dy)
                prev = change.position
              }
            }
          }
        }
  ) {
    @Suppress("UNUSED_VARIABLE")
    val tick = frameNanos
    engine.ensureDimensions(size.width, size.height)

    val colors = currentPalette.petalColors
    val colorCount = colors.size
    if (engine.initialized && colorCount > 0) {
      for (i in 0 until engine.poolSize) {
        val p = engine.particleAt(i)
        drawCircle(
          color = colors[p.colorIndex % colorCount],
          radius = p.radiusPx,
          center = Offset(p.x, p.y),
        )
      }
    }
  }
}

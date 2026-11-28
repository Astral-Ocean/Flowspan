package com.example.ui.components

import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalFlowspanPalette
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

/**
 * Full-bleed living editorial wash backdrop:
 * - Covers the entire edge-to-edge viewport with recognizable light theme pigment.
 * - Optimized two-blob soft gradient wash with pre-allocated shaders, matrix translations,
 *   and cached paper fiber speckles for rock-solid 60-120fps even on low-end devices.
 */
@Composable
fun AmbientMeshBackdrop(
  isPaused: Boolean,
  modifier: Modifier = Modifier,
) {
  val palette = LocalFlowspanPalette.current

  val baseColor by animateColorAsState(
    targetValue = palette.meshBase,
    animationSpec = tween(400),
    label = "mesh_base",
  )
  val blob1Color by animateColorAsState(
    targetValue = palette.meshBlob1,
    animationSpec = tween(400),
    label = "mesh_blob_1",
  )
  val blob2Color by animateColorAsState(
    targetValue = palette.meshBlob2,
    animationSpec = tween(400),
    label = "mesh_blob_2",
  )

  val pausedState by rememberUpdatedState(isPaused)
  var timeSeconds by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(isPaused) {
    if (isPaused) return@LaunchedEffect
    var lastNanos = 0L
    while (true) {
      withFrameNanos { now ->
        if (!pausedState && lastNanos != 0L) {
          val dt = ((now - lastNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
          timeSeconds += dt
        }
        lastNanos = now
      }
    }
  }

  val paint1 = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
  val paint2 = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
  val matrix1 = remember { Matrix() }
  val matrix2 = remember { Matrix() }

  val shaderState = remember {
    object {
      var radius = -1f
      var c1 = 0
      var c2 = 0
      var shader1: RadialGradient? = null
      var shader2: RadialGradient? = null
    }
  }

  val grainCount = 36
  val normalizedGrain = remember {
    val rng = Random(20261114)
    FloatArray(grainCount * 2) { rng.nextFloat() }
  }
  val grainPaint = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
  val grainState = remember {
    object {
      var w = -1f
      var h = -1f
      var pts = FloatArray(grainCount * 2)
    }
  }

  Canvas(modifier = modifier.fillMaxSize().graphicsLayer()) {
    val w = size.width
    val h = size.height
    if (w <= 1f || h <= 1f) return@Canvas

    val t = timeSeconds
    val maxDim = max(w, h)
    val blobRadius = maxDim * 0.82f

    val argb1 = blob1Color.toArgb()
    val argb2 = blob2Color.toArgb()

    // Rebuild RadialGradient shaders only when viewport size or animated palette color changes
    if (
      shaderState.radius != blobRadius ||
        shaderState.c1 != argb1 ||
        shaderState.c2 != argb2
    ) {
      shaderState.radius = blobRadius
      shaderState.c1 = argb1
      shaderState.c2 = argb2

      val transparent1 = argb1 and 0x00FFFFFF
      val transparent2 = argb2 and 0x00FFFFFF

      shaderState.shader1 =
        RadialGradient(
            0f,
            0f,
            blobRadius * 0.90f,
            intArrayOf(argb1, (argb1 and 0x00FFFFFF) or 0x98000000.toInt(), transparent1),
            floatArrayOf(0f, 0.52f, 1f),
            Shader.TileMode.CLAMP,
          )
          .also { paint1.shader = it }

      shaderState.shader2 =
        RadialGradient(
            0f,
            0f,
            blobRadius,
            intArrayOf(argb2, (argb2 and 0x00FFFFFF) or 0x8A000000.toInt(), transparent2),
            floatArrayOf(0f, 0.50f, 1f),
            Shader.TileMode.CLAMP,
          )
          .also { paint2.shader = it }
    }

    // 1. Full-bleed solid theme base so every corner of the screen carries theme color
    drawRect(color = baseColor)

    // 2. Smoothly drifting large color washes bounded tightly to each blob radius
    val x1 = w * (0.36f + 0.32f * sin(t * 0.22f + 0.4f))
    val y1 = h * (0.28f + 0.24f * cos(t * 0.18f))
    val x2 = w * (0.64f + 0.32f * cos(t * 0.20f + 1.3f))
    val y2 = h * (0.66f + 0.24f * sin(t * 0.16f + 0.7f))

    drawIntoCanvas { canvas ->
      val native = canvas.nativeCanvas
      shaderState.shader1?.let { s ->
        matrix1.setTranslate(x1, y1)
        s.setLocalMatrix(matrix1)
        native.drawCircle(x1, y1, blobRadius * 0.90f, paint1)
      }
      shaderState.shader2?.let { s ->
        matrix2.setTranslate(x2, y2)
        s.setLocalMatrix(matrix2)
        native.drawCircle(x2, y2, blobRadius, paint2)
      }

      // 3. Subtle cached paper fiber speckles with zero per-frame object allocation
      if (grainState.w != w || grainState.h != h) {
        grainState.w = w
        grainState.h = h
        for (idx in 0 until grainCount) {
          grainState.pts[idx * 2] = normalizedGrain[idx * 2] * w
          grainState.pts[idx * 2 + 1] = normalizedGrain[idx * 2 + 1] * h
        }
      }
      grainPaint.color = palette.inkPrimary.copy(alpha = 0.020f).toArgb()
      grainPaint.strokeWidth = 1.5.dp.toPx()
      grainPaint.strokeCap = android.graphics.Paint.Cap.ROUND
      native.drawPoints(grainState.pts, grainPaint)
    }
  }
}

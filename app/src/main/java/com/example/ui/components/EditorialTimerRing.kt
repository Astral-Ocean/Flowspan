package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.model.TimerPhase
import com.example.ui.theme.LocalFlowspanPalette
import kotlin.math.min

/**
 * Large-diameter editorial timer ring with bold, confident stroke weight,
 * deeper theme-ink pigment, and a clean uniform arc end (no enlarged head dot).
 */
@Composable
fun EditorialTimerRing(
  phase: TimerPhase,
  progressFraction: Float,
  modifier: Modifier = Modifier,
) {
  val palette = LocalFlowspanPalette.current

  val activeColor by animateColorAsState(
    targetValue =
      when (phase) {
        TimerPhase.IDLE -> palette.accent
        TimerPhase.FOCUS -> palette.accent
        TimerPhase.BREAK -> palette.accentBreak
      },
    animationSpec = tween(260),
    label = "ring_active_color",
  )

  val animatedFraction by animateFloatAsState(
    targetValue = if (phase == TimerPhase.IDLE) 1f else progressFraction,
    animationSpec = tween(durationMillis = 120),
    label = "ring_progress_sweep",
  )

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.fillMaxSize().graphicsLayer()) {
      val ringStroke = 14.dp.toPx()
      val inset = ringStroke * 0.5f + 2.dp.toPx()
      val diameter = (min(size.width, size.height) - inset * 2f).coerceAtLeast(10f)
      val radius = diameter * 0.5f
      val center = Offset(size.width * 0.5f, size.height * 0.5f)
      val arcTopLeft = Offset(center.x - radius, center.y - radius)
      val arcSize = Size(diameter, diameter)

      // Translucent theme-tinted base track so exposed arc harmonizes directly with the living wash
      val trackColor = activeColor.copy(alpha = 0.16f)
      drawCircle(
        color = trackColor,
        radius = radius,
        center = center,
        style = Stroke(width = ringStroke),
      )

      // Smooth progress sweep matching exact track thickness with no bulge at the arc tip
      val sweepAngle = 360f * animatedFraction.coerceIn(0f, 1f)
      if (sweepAngle > 0.25f) {
        drawArc(
          color = activeColor,
          startAngle = -90f,
          sweepAngle = sweepAngle,
          useCenter = false,
          topLeft = arcTopLeft,
          size = arcSize,
          style =
            Stroke(
              width = ringStroke,
              cap = StrokeCap.Round,
            ),
        )
      }
    }
  }
}

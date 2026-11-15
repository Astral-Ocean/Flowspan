package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalFlowspanPalette

/**
 * Editorial Frosted Vellum / Tracing Paper button.
 * Uses crisp spring-scale tactile micro-interaction with zero grey Material ripple shadow.
 */
@Composable
fun VellumButton(
  label: String,
  icon: ImageVector,
  enabled: Boolean,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isAccentHint: Boolean = false,
) {
  val palette = LocalFlowspanPalette.current
  val shape = RoundedCornerShape(18.dp)
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val pressScale by animateFloatAsState(
    targetValue = if (isPressed && enabled) 0.955f else 1f,
    animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
    label = "vellum_press_scale",
  )

  val pressLiftedAlpha by animateFloatAsState(
    targetValue = if (isPressed && enabled) 1f else 0f,
    animationSpec = tween(durationMillis = 90),
    label = "vellum_press_tint",
  )

  val contentColor by animateColorAsState(
    targetValue =
      when {
        !enabled -> palette.inkMuted
        isAccentHint -> palette.accent
        else -> palette.inkPrimary
      },
    animationSpec = tween(220),
    label = "vellum_content_color",
  )

  val baseSurfaceTint by animateColorAsState(
    targetValue =
      if (enabled) palette.frostSurface
      else palette.frostSurface.copy(alpha = 0.42f),
    animationSpec = tween(220),
    label = "vellum_surface_tint",
  )

  // Luminous soft-white sheen on press instead of muddy grey shadow
  val surfaceTint = lerp(baseSurfaceTint, palette.frostHighlight, pressLiftedAlpha * 0.42f)

  val borderColor by animateColorAsState(
    targetValue =
      if (enabled) palette.frostBorder
      else palette.frostBorder.copy(alpha = 0.08f),
    animationSpec = tween(220),
    label = "vellum_border_color",
  )

  Box(
    modifier =
      modifier
        .graphicsLayer {
          scaleX = pressScale
          scaleY = pressScale
        }
        .defaultMinSize(minWidth = 48.dp, minHeight = 54.dp)
        .clip(shape)
        .background(
          brush =
            Brush.verticalGradient(
              colors =
                listOf(
                  surfaceTint,
                  surfaceTint.copy(alpha = (surfaceTint.alpha - 0.06f).coerceAtLeast(0.32f)),
                )
            )
        )
        .drawBehind {
          drawLine(
            color = palette.frostHighlight,
            start = Offset(16.dp.toPx(), 1.dp.toPx()),
            end = Offset(size.width - 16.dp.toPx(), 1.dp.toPx()),
            strokeWidth = 0.75.dp.toPx(),
          )
        }
        .border(width = 0.75.dp, color = borderColor, shape = shape)
        .clickable(
          enabled = enabled,
          role = Role.Button,
          interactionSource = interactionSource,
          indication = null,
          onClick = onClick,
        )
        .padding(horizontal = 12.dp, vertical = 14.dp)
        .testTag(testTag),
    contentAlignment = Alignment.Center,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = contentColor,
        modifier = Modifier.size(18.dp),
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
        color = contentColor,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

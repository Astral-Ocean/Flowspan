package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Anthropic-inspired editorial pairing:
// - Newsreader (literary publication serif) for Latin masthead, English editorial titles, and lining numerals
// - FontFamily.Serif (system Noto Serif CJK) for Chinese editorial titles, section headings, and palette/track titles
// - Plus Jakarta Sans for quiet secondary descriptions, dense UI captions, and micro-labels so small CJK text stays crisp
val NewsreaderFontFamily = FontFamily(Font(R.font.newsreader))
val PlusJakartaSansFontFamily = FontFamily(Font(R.font.plus_jakarta_sans))

fun editorialSerifFamily(language: com.example.model.AppLanguage): FontFamily =
  when (language) {
    com.example.model.AppLanguage.ZH_CN,
    com.example.model.AppLanguage.ZH_TW,
    com.example.model.AppLanguage.JA,
    com.example.model.AppLanguage.KO,
    com.example.model.AppLanguage.RU -> FontFamily.Serif
    else -> NewsreaderFontFamily
  }

fun editorialSerifFamily(isZh: Boolean): FontFamily =
  if (isZh) FontFamily.Serif else NewsreaderFontFamily

val FlowspanTypography =
  Typography(
    // Editorial masthead "Flowspan"
    headlineLarge =
      TextStyle(
        fontFamily = NewsreaderFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.6).sp,
      ),
    // Editorial sheet title
    headlineSmall =
      TextStyle(
        fontFamily = NewsreaderFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.2).sp,
      ),
    // Real-time countdown readout: Newsreader serif with tabular equal-height lining numerals
    displayMedium =
      TextStyle(
        fontFamily = NewsreaderFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 54.sp,
        lineHeight = 58.sp,
        letterSpacing = 0.5.sp,
        fontFeatureSettings = "lnum, tnum",
      ),
    // Numeric editorial steppers & inline figures
    headlineMedium =
      TextStyle(
        fontFamily = NewsreaderFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        fontFeatureSettings = "lnum, tnum",
      ),
    // Settings section titles & crisp editorial item titles
    titleMedium =
      TextStyle(
        fontFamily = NewsreaderFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 23.sp,
        letterSpacing = 0.sp,
      ),
    titleSmall =
      TextStyle(
        fontFamily = NewsreaderFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.5.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.15.sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.6.sp,
      ),
  )

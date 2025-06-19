package com.omar.musica.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.omar.musica.ui.R


val QuickSandFontFamily = FontFamily(
    Font(R.font.quicksand_light, FontWeight.Light),
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_medium, FontWeight.Medium),
    Font(R.font.quicksand_semibold, FontWeight.Bold)
)

val ManropeFontFamily = FontFamily(
    Font(R.font.manrope_light, FontWeight.Light),
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_bold, FontWeight.Bold),
    Font(R.font.manrope_extrabold, FontWeight.ExtraBold),
    Font(R.font.manrope_extralight, FontWeight.ExtraLight),
    Font(R.font.manrope_semibold, FontWeight.SemiBold)
)

val font = ManropeFontFamily//FontFamily.Default

// Set of Material typography styles to start with
private val defaultTypography = Typography()

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        platformStyle = PlatformTextStyle(true)
    ),
    bodyMedium = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp,
        platformStyle = PlatformTextStyle(true)
    ),
    titleLarge = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        platformStyle = PlatformTextStyle(true)
    ),
    labelSmall = TextStyle(
        fontFamily = font,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        platformStyle = PlatformTextStyle(true)
    ),
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = font),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = font),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = font),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = font),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = font),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = font),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = font),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = font),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = font),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = font),

    )
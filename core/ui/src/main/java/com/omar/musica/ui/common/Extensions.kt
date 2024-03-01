package com.omar.musica.ui.common

import androidx.compose.ui.graphics.Color


fun Color.toInt(): Int {
    return (0xFF shl 24) or ((red * 255.0f).toInt() shl 16) or ((green * 255.0f).toInt() shl 8) or ((blue * 255.0f).toInt())
}

fun Int.fromIntToAccentColor() =
    Color(
        red = this.shr(16).and(0x0000FF),
        green = this.shr(8).and(0x0000FF),
        blue = this.and(0x0000FF)
    )
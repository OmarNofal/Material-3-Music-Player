package com.omar.musica.ui



fun Long.millisToTime(): String {

    val seconds = this / 1000
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    var result = ""
    if (hours.toInt() != 0) result += String.format("%02d:", hours)
    result += String.format("%02d:", minutes)
    result += String.format("%02d", remainingSeconds)

    return result
}
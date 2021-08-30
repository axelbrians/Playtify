package com.machina.playtify.core

object Helper {

    fun millisToMMSS(timeInMillis: Long): String {
        val totalSecs = timeInMillis / 1000
        val minutes = (totalSecs % 3600) / 60
        val seconds = totalSecs % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
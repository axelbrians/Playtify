package com.machina.playtify.view

import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.palette.graphics.Palette
import timber.log.Timber

fun View.loadBitMapAsDarkMutedBackground(
    bitmap: Bitmap
) {
    // Palette is used to generate color based on the Bitmap
    // it can generate several color which some option can be null
    // Read more about Palette in official docs
    val paletteBuilder = Palette.Builder(bitmap).maximumColorCount(16)
    var palette = paletteBuilder.generate().getDarkMutedColor(0)
    if (palette == 0) palette = paletteBuilder.generate().getMutedColor(0)
    if (palette == 0) palette = paletteBuilder.generate().getLightMutedColor(0)
    if (palette == 0) palette = paletteBuilder.generate().getDominantColor(0)

    this.setBackgroundColor(palette)
}


fun View.loadBitMapAsDarkMutedGradientBackground(
    bitmap: Bitmap,
    orientation: GradientDrawable.Orientation,
    corner: Float
) {
    // Palette is used to generate color based on the Bitmap
    // it can generate several color which some option can be null
    // Read more about Palette in official docs
    val paletteBuilder = Palette.Builder(bitmap).maximumColorCount(16)
    var palette = paletteBuilder.generate().getDarkMutedColor(0)
    if (palette == 0) palette = paletteBuilder.generate().getMutedColor(0)
    if (palette == 0) palette = paletteBuilder.generate().getLightMutedColor(0)
    if (palette == 0) palette = paletteBuilder.generate().getDominantColor(0)

    // this color Array used to create GradientDrawable effect that later will
    // be applied to image header
    val color = IntArray(2)
    color[0] = palette
    color[1] = 0xFF121212.toInt()
//    Timber.d("palette: $palette")

    // Generate GradientDrawable based on the color that has been extracted
    // from the Bitmap above with Palette
    val gradientDrawable = GradientDrawable(
        orientation,
        color
    )
    gradientDrawable.cornerRadius = corner

    this.background = gradientDrawable
}
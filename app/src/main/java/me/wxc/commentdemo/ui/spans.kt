package me.wxc.commentdemo.ui

import android.graphics.Color
import androidx.core.text.buildSpannedString
import androidx.core.text.color

fun CharSequence.makeHot(): CharSequence {
    return buildSpannedString {
        color(Color.RED) {
            append("热评  ")
        }
        append(this@makeHot)
    }
}
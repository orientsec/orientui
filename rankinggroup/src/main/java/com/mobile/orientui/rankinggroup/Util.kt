package com.mobile.orientui.rankinggroup

import android.content.Context

fun px2sp(context: Context, pxValue: Float): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

fun dp2px(context: Context, dp: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    return (dp * displayMetrics.density + 0.5).toInt()
}
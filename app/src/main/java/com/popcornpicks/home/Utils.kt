
package com.popcornpicks.home

import android.content.Context

/**
 * Extension function to convert dp to pixels
 */
fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

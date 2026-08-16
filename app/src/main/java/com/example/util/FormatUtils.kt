package com.example.util

import java.util.Locale

object FormatUtils {
    /**
     * Formats coin amount dynamically. E.g.:
     * - 950 -> "950"
     * - 1000 -> "1k"
     * - 1500 -> "1.5k"
     * - 10500 -> "10.5k"
     * - 100000 -> "100k"
     * - 1000000 -> "1M"
     * - 10500000 -> "10.5M"
     */
    fun formatCoins(coins: Int): String {
        return when {
            coins < 1000 -> coins.toString()
            coins < 1000000 -> {
                val kValue = coins / 1000f
                if (coins % 1000 == 0 || kValue >= 100f) {
                    String.format(Locale.US, "%.0fk", kValue)
                } else {
                    String.format(Locale.US, "%.1fk", kValue).replace(".0", "")
                }
            }
            coins < 1000000000 -> {
                val mValue = coins / 1000000f
                if (coins % 1000000 == 0 || mValue >= 100f) {
                    String.format(Locale.US, "%.0fM", mValue)
                } else {
                    String.format(Locale.US, "%.1fM", mValue).replace(".0", "")
                }
            }
            else -> {
                val bValue = coins / 1000000000f
                if (coins % 1000000000 == 0 || bValue >= 100f) {
                    String.format(Locale.US, "%.0fB", bValue)
                } else {
                    String.format(Locale.US, "%.1fB", bValue).replace(".0", "")
                }
            }
        }
    }
}

package com.crazycoderz.dailychecks.domain

import java.time.LocalDate

object StreakCalculator {

    fun calculate(
        previousDate: LocalDate?,
        previousStreak: Int,
        today: LocalDate,
    ): StreakResult {
        return when {
            previousDate == null -> StreakResult(today, 1)
            previousDate == today -> StreakResult(today, previousStreak.coerceAtLeast(1))
            previousDate.plusDays(1) == today -> StreakResult(today, previousStreak + 1)
            else -> StreakResult(today, 1)
        }
    }
}

data class StreakResult(
    val checkedDate: LocalDate,
    val streakCount: Int,
)


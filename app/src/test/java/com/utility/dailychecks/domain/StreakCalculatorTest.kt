package com.crazycoderz.dailychecks.domain

import com.crazycoderz.dailychecks.domain.StreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {

    @Test
    fun `first check starts streak at one`() {
        val result = StreakCalculator.calculate(
            previousDate = null,
            previousStreak = 0,
            today = LocalDate.of(2026, 4, 18),
        )

        assertEquals(1, result.streakCount)
    }

    @Test
    fun `same day check does not increase streak`() {
        val result = StreakCalculator.calculate(
            previousDate = LocalDate.of(2026, 4, 18),
            previousStreak = 3,
            today = LocalDate.of(2026, 4, 18),
        )

        assertEquals(3, result.streakCount)
    }

    @Test
    fun `next day check increments streak`() {
        val result = StreakCalculator.calculate(
            previousDate = LocalDate.of(2026, 4, 17),
            previousStreak = 3,
            today = LocalDate.of(2026, 4, 18),
        )

        assertEquals(4, result.streakCount)
    }

    @Test
    fun `missed day resets streak`() {
        val result = StreakCalculator.calculate(
            previousDate = LocalDate.of(2026, 4, 15),
            previousStreak = 5,
            today = LocalDate.of(2026, 4, 18),
        )

        assertEquals(1, result.streakCount)
    }
}


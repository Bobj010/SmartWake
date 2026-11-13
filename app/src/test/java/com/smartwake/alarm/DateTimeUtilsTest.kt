package com.smartwake.alarm

import com.smartwake.alarm.core.util.DateTimeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateTimeUtilsTest {

    @Test
    fun formatTime_formatsHoursAndMinutesWithLeadingZeros() {
        assertEquals("07:05", DateTimeUtils.formatTime(7, 5))
        assertEquals("00:00", DateTimeUtils.formatTime(0, 0))
        assertEquals("23:59", DateTimeUtils.formatTime(23, 59))
    }

    @Test
    fun calculateNextTriggerMillis_schedulesForTomorrowIfTimeHasPassed() {
        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // Будильник на 08:00 (уже прошел сегодня)
        val trigger = DateTimeUtils.calculateNextTriggerMillis(
            hour = 8,
            minute = 0,
            daysOfWeekMask = 0,
            nowMillis = now
        )

        val targetCal = Calendar.getInstance().apply { timeInMillis = trigger }
        val nowCal = Calendar.getInstance().apply { timeInMillis = now }

        assertEquals(8, targetCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, targetCal.get(Calendar.MINUTE))
        assertTrue(trigger > now)
        assertEquals(nowCal.get(Calendar.DAY_OF_YEAR) + 1, targetCal.get(Calendar.DAY_OF_YEAR))
    }

    @Test
    fun formatDaysOfWeek_formatsCorrectLabels() {
        assertEquals("Однократно", DateTimeUtils.formatDaysOfWeek(0))
        assertEquals("Каждый день", DateTimeUtils.formatDaysOfWeek(127))
        assertEquals("По будням", DateTimeUtils.formatDaysOfWeek(31))
        assertEquals("По выходным", DateTimeUtils.formatDaysOfWeek(96))
        assertEquals("Пн, Ср, Пт", DateTimeUtils.formatDaysOfWeek(1 or 4 or 16))
    }
}

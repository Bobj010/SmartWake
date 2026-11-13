package com.smartwake.alarm.core.util

import java.util.Calendar
import java.util.Locale

object DateTimeUtils {

    /**
     * Форматирует часы и минуты в вид "07:05"
     */
    fun formatTime(hour: Int, minute: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

    /**
     * Рассчитывает точный таймстемп в миллисекундах для следующего срабатывания
     * @param hour час (0..23)
     * @param minute минута (0..59)
     * @param daysOfWeekMask битовая маска дней недели (1 = Пн, 2 = Вт, ..., 64 = Вс). 0 = разовый.
     */
    fun calculateNextTriggerMillis(
        hour: Int,
        minute: Int,
        daysOfWeekMask: Int = 0,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {
        val now = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val target = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Если разовый будильник (дни не выбраны)
        if (daysOfWeekMask == 0) {
            if (target.timeInMillis <= now.timeInMillis) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis
        }

        // Если есть повтор по дням недели
        for (i in 0..7) {
            val dayOfWeek = target.get(Calendar.DAY_OF_WEEK)
            val bitMask = calendarDayToBit(dayOfWeek)

            if ((daysOfWeekMask and bitMask) != 0) {
                if (target.timeInMillis > now.timeInMillis) {
                    return target.timeInMillis
                }
            }
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis
    }

    /**
     * Формирует человекочитаемую строку: "Через 6 ч 45 мин"
     */
    fun formatTimeRemaining(targetMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
        val diffMillis = targetMillis - nowMillis
        if (diffMillis <= 0) return "Менее 1 мин"

        val totalMinutes = diffMillis / (1000 * 60)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {
            hours > 0 && minutes > 0 -> "Через $hours ч $minutes мин"
            hours > 0 -> "Через $hours ч"
            else -> "Через $minutes мин"
        }
    }

    /**
     * Преобразует Calendar.DAY_OF_WEEK (1=Sunday, 2=Monday, ...) в нашу битовую маску:
     * 1 = Пн, 2 = Вт, 4 = Ср, 8 = Чт, 16 = Пт, 32 = Сб, 64 = Вс
     */
    fun calendarDayToBit(calendarDay: Int): Int {
        return when (calendarDay) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 8
            Calendar.FRIDAY -> 16
            Calendar.SATURDAY -> 32
            Calendar.SUNDAY -> 64
            else -> 0
        }
    }

    /**
     * Преобразует битовую маску в строку ("Пн, Вт, Ср" или "Будни" или "Каждый день")
     */
    fun formatDaysOfWeek(mask: Int): String {
        if (mask == 0) return "Однократно"
        if (mask == 127) return "Каждый день"
        if (mask == 31) return "По будням"
        if (mask == 96) return "По выходным"

        val days = mutableListOf<String>()
        if ((mask and 1) != 0) days.add("Пн")
        if ((mask and 2) != 0) days.add("Вт")
        if ((mask and 4) != 0) days.add("Ср")
        if ((mask and 8) != 0) days.add("Чт")
        if ((mask and 16) != 0) days.add("Пт")
        if ((mask and 32) != 0) days.add("Сб")
        if ((mask and 64) != 0) days.add("Вс")

        return days.joinToString(", ")
    }
}

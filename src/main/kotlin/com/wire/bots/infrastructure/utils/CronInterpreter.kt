package com.wire.bots.infrastructure.utils

object CronInterpreter {
    private const val CRON_PARTS_SIZE = 6
    private const val CRON_SECOND_INDEX = 0
    private const val CRON_MINUTE_INDEX = 1
    private const val CRON_HOUR_INDEX = 2
    private const val CRON_DAY_INDEX = 3
    private const val CRON_MONTH_INDEX = 4
    private const val CRON_WEEKDAY_INDEX = 5

    fun interpretCron(cron: String): String {
        val parts = cron.trim().split("\\s+".toRegex())
        if (parts.size < CRON_PARTS_SIZE) return "(invalid cron)"

        return when {
            isEveryHour(parts) -> "every hour"
            isEveryDayAtHour(parts) -> "every day at ${formatHour(parts)}:00"
            isEveryDay(parts) -> "every day at ${formatHour(parts)}:${formatMinute(parts)}"
            isEveryMonth(parts) -> "every month on day ${parts[CRON_DAY_INDEX]} at " +
                "${formatHour(parts)}:${formatMinute(parts)}"
            isEveryWeek(parts) -> "every week on ${parts[CRON_WEEKDAY_INDEX]} at " +
                "${formatHour(parts)}:${formatMinute(parts)}"
            else -> "every (custom cron: $cron)"
        }
    }

    private fun isEveryHour(parts: List<String>) =
        parts[CRON_HOUR_INDEX] == "*" &&
            parts[CRON_DAY_INDEX] == "*" &&
            parts[CRON_MONTH_INDEX] == "*" &&
            parts[CRON_WEEKDAY_INDEX] == "*"

    private fun isEveryDayAtHour(parts: List<String>) =
        parts[CRON_SECOND_INDEX] == "0" &&
            parts[CRON_MINUTE_INDEX] == "0" &&
            parts[CRON_DAY_INDEX] == "?" &&
            parts[CRON_MONTH_INDEX] == "*" &&
            parts[CRON_WEEKDAY_INDEX] == "*"

    private fun isEveryDay(parts: List<String>) =
        parts[CRON_DAY_INDEX] == "*" &&
            parts[CRON_MONTH_INDEX] == "*" &&
            parts[CRON_WEEKDAY_INDEX] == "*"

    private fun isEveryMonth(parts: List<String>) =
        parts[CRON_MONTH_INDEX] == "*" &&
            parts[CRON_WEEKDAY_INDEX] == "*"

    private fun isEveryWeek(parts: List<String>) = parts[CRON_WEEKDAY_INDEX] != "*"

    private fun formatHour(parts: List<String>) = parts[CRON_HOUR_INDEX].padStart(2, '0')

    private fun formatMinute(parts: List<String>) = parts[CRON_MINUTE_INDEX].padStart(2, '0')
}

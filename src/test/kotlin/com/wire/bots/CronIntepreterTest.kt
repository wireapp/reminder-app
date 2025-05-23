package com.wire.bots

import com.wire.bots.infrastructure.utils.CronInterpreter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class CronInterpreterTest {

    @Test
    fun `should return every hour for valid hourly cron`() {
        val cron = "* * * * * *"
        val result = CronInterpreter.interpretCron(cron)
        assertEquals("every hour", result)
    }

    @Test
    fun `should return every day at specific hour for valid daily cron`() {
        val cron = "0 0 10 ? * *"
        val result = CronInterpreter.interpretCron(cron)
        assertEquals("every day at 10:00", result)
    }

    @Test
    fun `should return every day at specific time for valid daily cron`() {
        val cron = "0 30 14 * * *"
        val result = CronInterpreter.interpretCron(cron)
        assertEquals("every day at 14:30", result)
    }

    @Test
    fun `should return every month on specific day for valid monthly cron`() {
        val cron = "0 15 8 5 * *"
        val result = CronInterpreter.interpretCron(cron)
        assertEquals("every month on day 5 at 08:15", result)
    }

    @Test
    fun `should return every week on specific day for valid weekly cron`() {
        val cron = "0 45 16 ? * MON"
        val result = CronInterpreter.interpretCron(cron)
        assertEquals("every week on MON at 16:45", result)
    }

    @Test
    fun `should return custom cron for unhandled patterns`() {
        val cron = "0 0 12 1 1 ?"
        val result = CronInterpreter.interpretCron(cron)
        assertEquals("every (custom cron: $cron)", result)
    }

    @Test
    fun `should return invalid cron for malformed input`() {
        val cron = "0 0 12"
        val result = CronInterpreter.interpretCron(cron)
        assertEquals("(invalid cron)", result)
    }
}

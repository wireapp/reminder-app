package com.wire.bots

import com.mdimension.jchronic.Chronic
import com.mdimension.jchronic.Options
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JChronicTest {

    @Test
    fun testJChronic() {
        // given
        val now = Calendar.getInstance()

        // when
        val span = Chronic.parse("tomorrow", Options(now))

        // then
        assertEquals(
            LocalDate.ofInstant(now.toInstant(), ZoneId.systemDefault()).plusDays(1),
            LocalDate.ofInstant(span.endCalendar.toInstant(), ZoneId.systemDefault())
        )
    }
}
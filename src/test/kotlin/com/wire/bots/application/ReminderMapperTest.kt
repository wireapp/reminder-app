package com.wire.bots.application

import com.wire.bots.domain.event.BotError
import com.wire.bots.shouldFail
import com.wire.integrations.jvm.model.QualifiedId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import java.util.UUID

class ReminderMapperTest
{
    private val TEST_CONVERSATION_ID = QualifiedId(
        UUID.fromString("00000000-000-0000-0000-000000000001"),
        "domain"
    )

    @Test
    fun givenASchedule_whenIsRecurringByTimeIncrement_ThenRaiseError() {
        // given - when
        val result = ReminderMapper.parseReminder(TEST_CONVERSATION_ID, "task", "every hour")

        // then
        result.shouldFail {
            assertInstanceOf(BotError.ReminderError::class.java, it)
            assertEquals(
                BotError.ErrorType.INCREMENT_IN_TIMEUNIT.message,
                (it as BotError.ReminderError).reason
            )
        }
    }

    @Test
    fun givenASchedule_whenIsInThePast_ThenRaiseError() {
        // given - when
        val result = ReminderMapper.parseReminder(
            TEST_CONVERSATION_ID,
            "task",
            "on 1990-01-01"
        )

        // then
        result.shouldFail {
            assertInstanceOf(BotError.ReminderError::class.java, it)
            assertEquals(
                BotError.ErrorType.DATE_IN_PAST.message,
                (it as BotError.ReminderError).reason
            )
        }
    }

    @Test
    fun givenASchedule_whenContainsNotBeingAbleToParse_ThenRaiseError() {
        // given - when
        val result = ReminderMapper.parseReminder(
            TEST_CONVERSATION_ID,
            "task",
            "SOME INVALID SCHEDULE"
        )

        // then
        result.shouldFail {
            assertInstanceOf(BotError.ReminderError::class.java, it)
            assertEquals(
                BotError.ErrorType.PARSE_ERROR.message,
                (it as BotError.ReminderError).reason
            )
        }
    }

    @Test
    fun givenASchedule_whenContainsNotBeingAbleToParseExpression_ThenRaiseError() {
        // given - when
        val result = ReminderMapper.parseReminder(
            TEST_CONVERSATION_ID,
            "task",
            "not/valid/expression"
        )

        // then
        result.shouldFail {
            assertInstanceOf(BotError.ReminderError::class.java, it)
            assertEquals(
                BotError.ErrorType.PARSE_ERROR.message,
                (it as BotError.ReminderError).reason
            )
        }
    }
}

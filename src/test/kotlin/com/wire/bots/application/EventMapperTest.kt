package com.wire.bots.application

import com.wire.bots.domain.event.BotError
import com.wire.bots.domain.event.Command
import com.wire.bots.domain.reminder.Reminder
import com.wire.bots.shouldFail
import com.wire.bots.shouldSucceed
import com.wire.integrations.jvm.model.QualifiedId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import java.util.UUID

internal val TEST_CONVERSATION_ID = QualifiedId(
    UUID.fromString("00000000-000-0000-0000-000000000001"),
    "domain"
)

class EventMapperTest {
    @Test
    fun givenNotRelevantEvent_whenMapping_ThenReturnSkip() {
        // given
        val eventDTO =
            EventDTO(
                type = EventTypeDTO.NEW_TEXT,
                conversationId = TEST_CONVERSATION_ID,
                botId = "botId",
                text = TextContent("not relevant")
            )

        // when
        val event = EventMapper.fromEvent(eventDTO)

        // then
        event.shouldFail {
            assertInstanceOf(BotError.Skip::class.java, it)
        }
    }

    @Test
    fun givenTextEvent_whenTextIsHelp_ThenReturnLegacyHelpCommand() {
        // given
        val eventDTO =
            EventDTO(
                type = EventTypeDTO.NEW_TEXT,
                conversationId = TEST_CONVERSATION_ID,
                botId = "botId",
                text = TextContent("/help")
            )

        // when
        val event = EventMapper.fromEvent(eventDTO)

        // then
        event.shouldSucceed {
            assertEquals(Command.LegacyHelp(TEST_CONVERSATION_ID), it)
        }
    }

    @Test
    fun givenTextEvent_whenTextIsHelp_ThenReturnHelpCommand() {
        // given
        val eventDTO =
            EventDTO(
                type = EventTypeDTO.NEW_TEXT,
                conversationId = TEST_CONVERSATION_ID,
                botId = "botId",
                text = TextContent("/remind help")
            )

        // when
        val event = EventMapper.fromEvent(eventDTO)

        // then
        event.shouldSucceed {
            assertEquals(Command.Help(TEST_CONVERSATION_ID), it)
        }
    }

    @Test
    fun givenTextEvent_whenTextIsOneTimeRemind_ThenReturnRemindCommandSingle() {
        // given
        val eventDTO =
            EventDTO(
                type = EventTypeDTO.NEW_TEXT,
                conversationId = TEST_CONVERSATION_ID,
                botId = "botId",
                text = TextContent(
                    """/remind to "join the refinement session" "tomorrow at 11:00"""".trimIndent()
                )
            )

        // when
        val event = EventMapper.fromEvent(eventDTO)

        // then
        event.shouldSucceed {
            assertInstanceOf(Command.NewReminder::class.java, it)
            assertInstanceOf(
                Reminder.SingleReminder::class.java,
                (it as Command.NewReminder).reminder
            )
            val reminder = it.reminder as Reminder.SingleReminder
            assertEquals("join the refinement session", reminder.task)
        }
    }

    @Test
    fun givenTextEvent_whenTextIsRecurringRemind_ThenReturnRemindCommandRecurring() {
        // given
        val eventDTO =
            EventDTO(
                type = EventTypeDTO.NEW_TEXT,
                conversationId = TEST_CONVERSATION_ID,
                botId = "botId",
                text = TextContent(
                    """/remind to "join the daily stand up" "every monday at 10:00"""".trimIndent()
                )
            )

        // when
        val event = EventMapper.fromEvent(eventDTO)

        // then
        event.shouldSucceed {
            assertInstanceOf(Command.NewReminder::class.java, it)
            assertInstanceOf(
                Reminder.RecurringReminder::class.java,
                (it as Command.NewReminder).reminder
            )
            val reminder = it.reminder as Reminder.RecurringReminder
            assertEquals("join the daily stand up", reminder.task)
        }
    }

    @Test
    fun givenTextEvent_whenTextIsRecurringByTimeIncrementRemind_ThenRaiseError() {
        // given
        val eventDTO =
            EventDTO(
                type = EventTypeDTO.NEW_TEXT,
                conversationId = TEST_CONVERSATION_ID,
                botId = "botId",
                text = TextContent(
                    """/remind to "drink water" "every 1 hours"""".trimIndent()
                )
            )

        // when
        val event = EventMapper.fromEvent(eventDTO)

        // then
        event.shouldFail {
            assertInstanceOf(BotError.ReminderError::class.java, it)
            assertEquals(
                BotError.ErrorType.INCREMENT_IN_TIMEUNIT,
                (it as BotError.ReminderError).errorType
            )
        }
    }

    @Test
    fun givenTextEvent_whenTextTargetDayInPast_ThenRaiseError() {
        // given
        val eventDTO =
            EventDTO(
                type = EventTypeDTO.NEW_TEXT,
                conversationId = TEST_CONVERSATION_ID,
                botId = "botId",
                text = TextContent("""/remind to "drink water" "yesterday" """.trimIndent())
            )

        // when
        val event = EventMapper.fromEvent(eventDTO)

        // then
        event.shouldFail {
            assertInstanceOf(BotError.ReminderError::class.java, it)
            assertEquals(BotError.ErrorType.DATE_IN_PAST, (it as BotError.ReminderError).errorType)
        }
    }
}

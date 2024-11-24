package com.wire.bots.infrastructure.repository

import arrow.core.Either
import arrow.core.right
import com.wire.bots.domain.reminder.Reminder
import com.wire.bots.domain.reminder.ReminderJobRepository
import com.wire.bots.domain.reminder.ReminderNextSchedule
import com.wire.bots.infrastructure.jobs.ReminderJob
import jakarta.enterprise.context.ApplicationScoped
import java.util.*
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder


@ApplicationScoped
class DefaultReminderJobRepository(private val quartz: Scheduler) : ReminderJobRepository {

    override fun scheduleReminderJob(reminder: Reminder): Either<Throwable, ReminderNextSchedule> {
        val job = JobBuilder.newJob(ReminderJob::class.java)
            .withIdentity(JobKey.jobKey(reminder.taskId, reminder.conversationId))
            .build()

        val trigger = buildTrigger(reminder)
        quartz.scheduleJob(job, trigger)

        return getNextReminderNextRun(reminder, trigger).right()
    }

    /**
     * Build the trigger for the reminder, simple trigger for single reminder and cron trigger for recurring reminder.
     * @param reminder the reminder to build the trigger for.
     */
    private fun buildTrigger(reminder: Reminder): Trigger {
        return when (reminder) {
            is Reminder.SingleReminder -> {
                TriggerBuilder.newTrigger()
                    .withIdentity("mySingleTriggerFor_${reminder.taskId}", reminder.conversationId)
                    .startAt(Date.from(reminder.scheduledAt.minusSeconds(SECONDS_BEFORE_WARMUP)))
                    .withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(SINGLE_TIME_COUNT_JOB))
                    .build()
            }

            is Reminder.RecurringReminder -> {
                TriggerBuilder.newTrigger()
                    .withIdentity("myRecurringTriggerFor_${reminder.taskId}", reminder.conversationId)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(reminder.scheduledCron))
                    .build()
            }
        }
    }

    /**
     * Get the next reminder next run.
     * @param reminder the reminder to get the next run for.
     * @param trigger the trigger to get the next run for.
     */
    private fun getNextReminderNextRun(reminder: Reminder, trigger: Trigger): ReminderNextSchedule {
        return when (reminder) {
            is Reminder.SingleReminder -> {
                ReminderNextSchedule(
                    reminder,
                    listOf(trigger.nextFireTime)
                )
            }

            is Reminder.RecurringReminder -> {
                ReminderNextSchedule(
                    reminder,
                    getNextFireTimeForTrigger(trigger, 3)
                )
            }
        }
    }


    /**
     * Get the next fire time for the trigger.
     * @param trigger the trigger to get the next fire time for.
     * @param maxNextFireSize the maximum number of next fire times to get.
     */
    private fun getNextFireTimeForTrigger(trigger: Trigger, maxNextFireSize: Int = 3): List<Date> {
        val runs: MutableList<Date> = ArrayList()
        var next = trigger.nextFireTime

        while (next != null && runs.size < maxNextFireSize) {
            runs += next
            next = trigger.getFireTimeAfter(next)
        }
        return runs
    }

    companion object {
        private const val SECONDS_BEFORE_WARMUP = 10L
        private const val SINGLE_TIME_COUNT_JOB = 1
    }
}
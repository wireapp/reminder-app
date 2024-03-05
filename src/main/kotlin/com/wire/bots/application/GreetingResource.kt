package com.wire.bots.application

import com.wire.bots.domain.reminder.Reminder
import com.wire.bots.domain.reminder.ReminderRepository
import com.wire.bots.infrastructure.jobs.ReminderJob
import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import java.time.Instant
import java.util.*
import org.quartz.JobBuilder
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.quartz.impl.matchers.GroupMatcher
import org.slf4j.LoggerFactory

@Path("/hello")
@ApplicationScoped
class GreetingResource {

    val logger = LoggerFactory.getLogger(GreetingResource::class.java)

    @Inject
    lateinit var quartz: Scheduler

    @Inject
    lateinit var reminderRepository: ReminderRepository // todo change to usecase


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Blocking
    fun hello(): Uni<String> {
        // first, persist in db the task (todo, save token)
        val newTaskId = UUID.randomUUID().toString()
        val reminder = Reminder(
            createdAt = Instant.now(),
            conversationId = "ConvoId",
            task = "Join the daily stand-up at 10:00 AM",
            scheduledAt = Instant.now().plusSeconds(3600L),
            taskId = newTaskId,
            isEternal = false
        )
        reminderRepository.persistReminder(reminder)

        // schedule the job for the task
        val job = JobBuilder.newJob(ReminderJob::class.java)
            .withIdentity(JobKey.jobKey(newTaskId, "ConvoId"))
            .build()

        val trigger: Trigger = TriggerBuilder.newTrigger()
            .withIdentity("myTriggerFor_$newTaskId", "ConvoId")
            .startAt(Date.from(Instant.now().plusSeconds(10)))
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(10)
                    .withRepeatCount(2)
//                    .repeatForever()
            )
            .build()
        quartz.scheduleJob(job, trigger)

        logger.info("Scheduled jobs: ${quartz.getJobKeys(GroupMatcher.anyGroup())}")
        return "Hello RESTEasy".let { Uni.createFrom().item(it) }
    }

}

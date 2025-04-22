package com.wire.bots.infrastructure.jobs

import jakarta.inject.Inject
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.slf4j.LoggerFactory

class ReminderJob(@Inject val reminderTaskExecutor: ReminderTaskExecutor) : Job {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext?) {
        logger.info("Executing job on ${Thread.currentThread().name} with context $context")

        val taskId = context?.jobDetail?.key?.name
        taskId.let { logger.info("Executing task? : $it") }
        taskId?.let { reminderTaskExecutor.doWork(it) }
    }
}

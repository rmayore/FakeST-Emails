package fake.st.emails.config.batch

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Job Launcher for email jobs. They run on the following schedules:
 * 1. High priority -> Every 10 seconds
 * 2. Medium priority -> Every minute
 * 3. Low priority -> Every 5 minutes
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 07-05-2023.
 */
@Component
class EmailJobScheduler {

    @Autowired
    lateinit var jobLauncher: JobLauncher

    @Autowired
    @Qualifier(HighPriorityEmailJobConfiguration.JOB_NAME)
    lateinit var highPriorityEmailJob: Job

    @Autowired
    @Qualifier(MediumPriorityEmailJobConfiguration.JOB_NAME)
    lateinit var mediumPriorityEmailJob: Job

    @Autowired
    @Qualifier(LowPriorityEmailJobConfiguration.JOB_NAME)
    lateinit var lowPriorityEmailJob: Job

    @Scheduled(cron = "*/10 * * * * *")
    @Throws(Exception::class)
    fun launchHighPriorityEmailJob() {
        jobLauncher.run(
            highPriorityEmailJob,
            JobParametersBuilder().addString("JobID", System.currentTimeMillis().toString()).toJobParameters()
        )
    }

    @Scheduled(cron = "0 * * * * *")
    @Throws(Exception::class)
    fun launchMediumPriorityEmailJob() {
        jobLauncher.run(
            mediumPriorityEmailJob,
            JobParametersBuilder().addString("JobID", System.currentTimeMillis().toString()).toJobParameters()
        )
    }

    @Scheduled(cron = "0 */5 * * * *")
    @Throws(Exception::class)
    fun launchLowPriorityEmailJob() {
        jobLauncher.run(
            lowPriorityEmailJob,
            JobParametersBuilder().addString("JobID", System.currentTimeMillis().toString()).toJobParameters()
        )
    }
}

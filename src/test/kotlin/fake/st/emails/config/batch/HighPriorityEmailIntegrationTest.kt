package fake.st.emails.config.batch

import com.redis.testcontainers.RedisContainer
import fake.st.emails.entity.redis.Email
import fake.st.emails.entity.redis.Priority
import fake.st.emails.entity.redis.Status
import fake.st.emails.entity.request.EmailDetails
import fake.st.emails.repository.EmailRepository
import fake.st.emails.service.EmailService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.io.File
import java.time.Duration
import java.util.*

/**
 * End-to-End test for high priority emails.
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 07-05-2023.
 */

@Testcontainers
@SpringBatchTest
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class HighPriorityEmailIntegrationTest {
    companion object {

        private const val redisPassword = "password"

        @Container
        private val kafka: DockerComposeContainer<*> =
            DockerComposeContainer(File("src/test/resources/kafka-docker-compose.yml")).withExposedService(
                "kafka",
                9092,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60))
            )

        @Container
        private val redis = RedisContainer(DockerImageName.parse("redis:5.0.3-alpine")).apply {
            withExposedPorts(6379)
            withCommand("--requirepass $redisPassword")
        }

        @Container
        private val mailHog = GenericContainer<Nothing>("mailhog/mailhog").apply {
            withExposedPorts(1025)
        }

        @JvmStatic
        @DynamicPropertySource
        private fun properties(registry: DynamicPropertyRegistry) {
            registry.add("redis.host") { redis.host }
            registry.add("redis.port") { redis.getMappedPort(6379).toString() }
            registry.add("redis.password") { redisPassword }

            registry.add("spring.mail.host") { mailHog.host }
            registry.add("spring.mail.port") { mailHog.getMappedPort(1025).toString() }

            val host = kafka.getServiceHost("kafka", 9092)
            val port = kafka.getServicePort("kafka", 9092)
            registry.add("spring.kafka.bootstrap-servers") { "$host:$port" }
        }
    }

    @Autowired
    lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    lateinit var jobRepositoryTestUtils: JobRepositoryTestUtils

    @Autowired
    lateinit var emailService: EmailService

    @Autowired
    lateinit var emailRepository: EmailRepository

    @Autowired
    @Qualifier(MediumPriorityEmailJobConfiguration.JOB_NAME)
    lateinit var mediumPriorityEmailJob: Job

    @Autowired
    @Qualifier(LowPriorityEmailJobConfiguration.JOB_NAME)
    lateinit var lowPriorityEmailJob: Job

    @BeforeEach
    fun setup() {
        val email = getMockEmail(Priority.HIGH)
        emailService.save(email)
    }

    @Test
    fun `high priority email send job executes correctly`(
        @Autowired
        @Qualifier(HighPriorityEmailJobConfiguration.JOB_NAME)
        highPriorityEmailJob: Job,
    ) {
        jobLauncherTestUtils.job = highPriorityEmailJob

        // launch job
        val params = JobParametersBuilder().addString("JobID", System.currentTimeMillis().toString()).toJobParameters()

        val jobExecution = jobLauncherTestUtils.launchJob(params)

        // assert job runs to completion
        assertEquals("COMPLETED", jobExecution.exitStatus.exitCode)
    }

    @Test
    fun `medium priority email send job executes correctly`() {
        jobLauncherTestUtils.job = mediumPriorityEmailJob

        // save email
        val email = getMockEmail(Priority.MEDIUM)
        emailService.save(email)

        // launch job
        val jobExecution = jobLauncherTestUtils.launchJob()

        // assert job runs to completion
        assertEquals("COMPLETED", jobExecution.exitStatus.exitCode)
    }

    @Test
    fun `low priority email send job executes correctly`() {
        jobLauncherTestUtils.job = lowPriorityEmailJob

        // save email
        val email = getMockEmail(Priority.LOW)
        emailService.save(email)

        // launch job
        val jobExecution = jobLauncherTestUtils.launchJob()

        // assert job runs to completion
        assertEquals("COMPLETED", jobExecution.exitStatus.exitCode)
    }

    fun getMockEmail(priority: Priority) = Email(
        id = UUID.randomUUID().toString(),
        emailDetails = EmailDetails("mayorerobert@gmail.com", "Subject", "Body"),
        emailDetailsWithAttachment = null,
        priority = priority,
        status = Status.PENDING,
        date = Date()
    )
}

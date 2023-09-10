package fake.st.emails.component

import fake.st.emails.config.kafka.TOPIC_EMAIL_HIGH_PRIORITY
import fake.st.emails.config.kafka.TOPIC_EMAIL_LOW_PRIORITY
import fake.st.emails.config.kafka.TOPIC_EMAIL_MEDIUM_PRIORITY
import fake.st.emails.entity.redis.Email
import fake.st.emails.entity.redis.Priority
import fake.st.emails.entity.request.EmailDetails
import fake.st.emails.service.EmailService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.time.Duration
import java.util.*

@Testcontainers
@SpringBootTest
class KafkaIntegrationTest(@Autowired val producer: KafkaProducer) {

    @SpyBean
    val emailService: EmailService? = null

    companion object {
        @Container
        var kafka: DockerComposeContainer<*> =
            DockerComposeContainer(File("src/test/resources/kafka-docker-compose.yml"))
                .withExposedService("kafka", 9092, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
        // .withLocalCompose(true)

        @JvmStatic
        @DynamicPropertySource
        fun kafkaProperties(registry: DynamicPropertyRegistry) {
            val host = kafka.getServiceHost("kafka", 9092)
            val port = kafka.getServicePort("kafka", 9092)

            registry.add("spring.kafka.bootstrap-servers") { "$host:$port" }
        }
    }

    @Test
    fun `kafka & zookeper test containers configured with docker compose container should be running`() {
        Assertions.assertTrue(kafka.getContainerByServiceName("kafka").get().isRunning)
        Assertions.assertTrue(kafka.getContainerByServiceName("zookeeper").get().isRunning)
    }

    @Test
    @Throws(Exception::class)
    fun `sending message to topic email_priority_high - message should be received by consumer and saved`() {
        val captor = argumentCaptor<Email>()
        val email = EmailDetails("mayorerobert@gmail.com", "Test", "Test Body")

        producer.send(TOPIC_EMAIL_HIGH_PRIORITY, email)

        verify(emailService, timeout(5000).times(1))!!.save(captor.capture())
        assertNotNull(captor.firstValue)
        assertEquals(captor.firstValue.emailDetails, email)
        assertEquals(captor.firstValue.priority, Priority.HIGH)
    }

    @Test
    @Throws(Exception::class)
    fun `sending message to topic email_priority_medium - message should be received by consumer and saved`() {
        val captor = argumentCaptor<Email>()
        val email = EmailDetails("mayorerobert@gmail.com", "Test", "Test Body")

        producer.send(TOPIC_EMAIL_MEDIUM_PRIORITY, email)

        verify(emailService, timeout(5000).times(1))!!.save(captor.capture())
        assertNotNull(captor.firstValue)
        assertEquals(captor.firstValue.emailDetails, email)
        assertEquals(captor.firstValue.priority, Priority.MEDIUM)
    }

    @Test
    @Throws(Exception::class)
    fun `sending message to topic email_priority_low - message should be received by consumer and saved`() {
        val captor = argumentCaptor<Email>()
        val email = EmailDetails("mayorerobert@gmail.com", "Test", "Test Body")

        producer.send(TOPIC_EMAIL_LOW_PRIORITY, email)

        verify(emailService, timeout(5000).times(1))!!.save(captor.capture())
        assertNotNull(captor.firstValue)
        assertEquals(captor.firstValue.emailDetails, email)
        assertEquals(captor.firstValue.priority, Priority.LOW)
    }
}

package fake.st.emails.controller

import com.redis.testcontainers.RedisContainer
import fake.st.emails.WebIntegrationTest
import fake.st.emails.entity.request.EmailDetails
import fake.st.emails.entity.request.EmailDetailsWithAttachment
import fake.st.emails.service.SendEmailService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmailControllerTest : WebIntegrationTest() {

    /* Start Test containers setup */
    companion object {

        private const val redisPassword = "password"

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
        private fun registerRedisProperties(registry: DynamicPropertyRegistry) {
            registry.add("redis.host") { redis.host }
            registry.add("redis.port") { redis.getMappedPort(6379).toString() }
            registry.add("redis.password") { redisPassword }

            registry.add("spring.mail.host") { mailHog.host }
            registry.add("spring.mail.port") { mailHog.getMappedPort(1025).toString() }
        }
    }
    /* End Test containers setup */

    private val scheduleEmailEndpoint = "/email/schedule"
    private val scheduleEmailWithAttachmentEndpoint = "/email/schedule-with-attachment"
    private val sendEmailEndpoint = "/email/send"

    @Autowired
    lateinit var sendEmailService: SendEmailService

    @BeforeEach
    fun beforeMethod() {
        redis.execInContainer("redis-cli", "-a", redisPassword, "FLUSHALL")
    }

    @Test
    fun `redis test container configured with dynamic properties - should be running`() {
        assertTrue(redis.isRunning)
    }

    @Test
    fun `mail-hog test container configured with dynamic properties - should be running`() {
        assertTrue(mailHog.isRunning)
    }

    @Test
    @Throws(Exception::class)
    fun `scheduling simple email - should return 200 response`() {
        mvc!!.perform(
            MockMvcRequestBuilders.post(scheduleEmailEndpoint).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(
                    Json.encodeToString(
                        EmailDetails(
                            "mayorerobert@gmail.com",
                            "Subject",
                            "Body",
                        )
                    )
                )
        ).andExpect(MockMvcResultMatchers.status().isOk).andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message",
                Matchers.equalTo("Email added to queue successfully"),
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun `scheduling simple email - with wrong input - should return 400 response`() {
        mvc!!.perform(
            MockMvcRequestBuilders.post(scheduleEmailEndpoint).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content("")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun `scheduling mail with attachment - should return 200 response`() {
        mvc!!.perform(
            MockMvcRequestBuilders.post(scheduleEmailWithAttachmentEndpoint).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(
                    Json.encodeToString(
                        EmailDetailsWithAttachment(
                            "mayorerobert@gmail.com",
                            "Subject",
                            "Body",
                            "download.png",
                        )
                    )
                )
        ).andExpect(MockMvcResultMatchers.status().isOk).andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message",
                Matchers.equalTo("Email added to queue successfully"),
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun `scheduling mail with attachment - with wrong input - should return 400 response`() {
        mvc!!.perform(
            MockMvcRequestBuilders.post(scheduleEmailWithAttachmentEndpoint).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(Json.encodeToString(""))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun `sending email - with no email scheduled - should return 200 response and correct message`() {
        mvc!!.perform(
            MockMvcRequestBuilders.get(sendEmailEndpoint)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo("0 successful, 0 failed")))
    }

    @Test
    @Throws(Exception::class)
    fun `sending email - with emails scheduled - should return 200 response and correct message`() {
        mvc!!.perform(
            MockMvcRequestBuilders.post(scheduleEmailEndpoint).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(
                    Json.encodeToString(
                        EmailDetails(
                            "mayorerobert@gmail.com",
                            "Subject",
                            "Body",
                        )
                    )
                )
        )

        mvc!!.perform(
            MockMvcRequestBuilders.get(sendEmailEndpoint)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo("1 successful, 0 failed")))
    }

    @Test
    @Throws(Exception::class)
    fun `sending email - with mail settings error - should return 200 response and correct message`() {
        val email = ReflectionTestUtils.getField(sendEmailService, "sender")
        ReflectionTestUtils.setField(sendEmailService, "sender", "")

        mvc!!.perform(
            MockMvcRequestBuilders.post(scheduleEmailEndpoint).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(
                    Json.encodeToString(
                        EmailDetails(
                            "mayorerobert@gmail.com",
                            "Subject",
                            "Body",
                        )
                    )
                )
        )

        mvc!!.perform(
            MockMvcRequestBuilders.get(sendEmailEndpoint)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo("0 successful, 1 failed")))

        ReflectionTestUtils.setField(sendEmailService, "sender", email)
    }
}

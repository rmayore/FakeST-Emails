package fake.st.emails.controller

import fake.st.emails.WebIntegrationTest
import fake.st.emails.entity.request.EmailDetails
import fake.st.emails.entity.request.EmailDetailsWithAttachment
import fake.st.emails.service.SendEmailService
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class EmailControllerTest : WebIntegrationTest() {

    @Autowired
    lateinit var sendEmailService : SendEmailService

    @Test
    @Throws(Exception::class)
    fun `sending simple email - should return 200 response`() {
        mvc!!.perform(
            MockMvcRequestBuilders
                .post("/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                    Json.encodeToString(
                        EmailDetails(
                            "mayorerobert@gmail.com",
                            "Subject",
                            "Body"
                        )
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo("Email sent successfully")))
    }

    @Test
    @Throws(Exception::class)
    fun `sending simple email - with wrong input - should return 400 response`() {
        mvc!!.perform(
            MockMvcRequestBuilders
                .post("/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("")
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun `sending simple email - with mail settings error - should return 500 response`() {
        val email = ReflectionTestUtils.getField(sendEmailService, "sender")
        ReflectionTestUtils.setField(sendEmailService, "sender", "invalid")

        mvc!!.perform(
            MockMvcRequestBuilders
                .post("/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                    Json.encodeToString(
                        EmailDetails(
                            "mayorerobert@gmail.com",
                            "Subject",
                            "Body"
                        )
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isInternalServerError)

        ReflectionTestUtils.setField(sendEmailService, "sender", email)
    }

    @Test
    @Throws(Exception::class)
    fun `sending mail with attachment - should return 200 response`() {
        mvc!!.perform(
            MockMvcRequestBuilders
                .post("/email/send-with-attachment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                    Json.encodeToString(
                        EmailDetailsWithAttachment(
                            "mayorerobert@gmail.com",
                            "Subject",
                            "Body",
                            "download.png"
                        )
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo("Email sent successfully")))
    }

    @Test
    @Throws(Exception::class)
    fun `sending mail with attachment - with wrong input - should return 400 response`() {
        mvc!!.perform(
            MockMvcRequestBuilders
                .post("/email/send-with-attachment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(Json.encodeToString(""))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun `sending mail with attachment - with mail settings error - should return 500 response`() {
        val email = ReflectionTestUtils.getField(sendEmailService, "sender")
        ReflectionTestUtils.setField(sendEmailService, "sender", "invalid")
        
        mvc!!.perform(
            MockMvcRequestBuilders
                .post("/email/send-with-attachment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                    Json.encodeToString(
                        EmailDetailsWithAttachment(
                            "mayorerobert@gmail.com",
                            "Subject",
                            "Body",
                            "download.png"
                        )
                    )
                )
        )
            .andExpect(MockMvcResultMatchers.status().isInternalServerError)

        ReflectionTestUtils.setField(sendEmailService, "sender", email)
    }
}
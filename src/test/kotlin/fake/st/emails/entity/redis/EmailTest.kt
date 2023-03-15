package fake.st.emails.entity.redis

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*


class EmailTest {

    @Test
    fun `date serializer serializes date as expected`() {
        val date = Date()

        val email = Email(
            id = UUID.randomUUID().toString(),
            emailDetails = null,
            emailDetailsWithAttachment = null,
            priority = null,
            status = null,
            date = date
        )

        val encodedEmail: String = Json.encodeToString(email)
        val mapper: ObjectMapper = JsonMapper()
        val json: JsonNode = mapper.readTree(encodedEmail)

        assertEquals(json.get("date").asLong(), date.time)


    }
}
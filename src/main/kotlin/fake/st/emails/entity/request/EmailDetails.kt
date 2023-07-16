package fake.st.emails.entity.request

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

@Serializable
data class EmailDetails(
    @JsonProperty("recipient") val recipient: String,
    @JsonProperty("subject") val subject: String,
    @JsonProperty("body") val body: String
)

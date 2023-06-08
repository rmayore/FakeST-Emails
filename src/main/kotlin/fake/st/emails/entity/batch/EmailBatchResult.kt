package fake.st.emails.entity.batch

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

@Serializable
data class EmailBatchResult(
    @JsonProperty("emailId") val emailId: String,
    @JsonProperty("success") val success: Boolean,
)
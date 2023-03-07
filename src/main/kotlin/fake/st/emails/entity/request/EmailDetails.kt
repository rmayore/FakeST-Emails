package fake.st.emails.entity.request

import kotlinx.serialization.Serializable

@Serializable
data class EmailDetails(
    val recipient: String,
    val subject: String,
    val body: String
)
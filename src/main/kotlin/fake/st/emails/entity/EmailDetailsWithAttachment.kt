package fake.st.emails.entity

import kotlinx.serialization.Serializable

@Serializable
data class EmailDetailsWithAttachment(
    val recipient: String,
    val subject: String,
    val body: String,
    val attachment: String
)
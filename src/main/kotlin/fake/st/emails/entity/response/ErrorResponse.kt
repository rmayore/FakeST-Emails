package fake.st.emails.entity.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse (var status : String, var message : String)
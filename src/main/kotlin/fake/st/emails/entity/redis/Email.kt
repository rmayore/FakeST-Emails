package fake.st.emails.entity.redis

import fake.st.emails.entity.request.EmailDetails
import fake.st.emails.entity.request.EmailDetailsWithAttachment
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.util.Date

@RedisHash("Email")
@Serializable
data class Email(
    @Id
    val id: String,
    val emailDetails: EmailDetails?,
    val emailDetailsWithAttachment: EmailDetailsWithAttachment?,
    @Indexed
    var priority: Priority?,
    @Indexed
    var status: Status?,
    @Indexed
    @Serializable(with = DateSerializer::class)
    var date: Date?
)

enum class Priority {
    LOW, MEDIUM, HIGH
}

enum class Status {
    PENDING, IN_PROGRESS, SENT
}

object DateSerializer : KSerializer<Date> {
    override val descriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)
    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeLong(value.time)
    }
}

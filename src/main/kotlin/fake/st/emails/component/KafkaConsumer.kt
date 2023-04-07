package fake.st.emails.component

import fake.st.emails.config.kafka.KAFKA_DEFAULT_GROUP
import fake.st.emails.config.kafka.TOPIC_EMAIL_HIGH_PRIORITY
import fake.st.emails.config.kafka.TOPIC_EMAIL_LOW_PRIORITY
import fake.st.emails.config.kafka.TOPIC_EMAIL_MEDIUM_PRIORITY
import fake.st.emails.entity.redis.Email
import fake.st.emails.entity.redis.Priority
import fake.st.emails.entity.redis.Status
import fake.st.emails.entity.request.EmailDetails
import fake.st.emails.service.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.*


/**
 * Kafka listener configuration
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 07-04-2023.
 */

@Component
class KafkaConsumer(@Autowired val emailService: EmailService) {

    @KafkaListener(topics = [TOPIC_EMAIL_HIGH_PRIORITY], groupId = KAFKA_DEFAULT_GROUP)
    fun listenHighPriorityEmail(details: EmailDetails): Boolean {
        return emailService.save(
            Email(
                id = UUID.randomUUID().toString(),
                emailDetails = details,
                emailDetailsWithAttachment = null,
                priority = Priority.HIGH,
                status = Status.PENDING,
                date = Date()
            )
        )
    }

    @KafkaListener(topics = [TOPIC_EMAIL_MEDIUM_PRIORITY], groupId = KAFKA_DEFAULT_GROUP)
    fun listenMediumPriorityEmail(details: EmailDetails): Boolean {
        return emailService.save(
            Email(
                id = UUID.randomUUID().toString(),
                emailDetails = details,
                emailDetailsWithAttachment = null,
                priority = Priority.MEDIUM,
                status = Status.PENDING,
                date = Date()
            )
        )
    }

    @KafkaListener(topics = [TOPIC_EMAIL_LOW_PRIORITY], groupId = KAFKA_DEFAULT_GROUP)
    fun listenLowPriorityEmail(details: EmailDetails): Boolean {
        return emailService.save(
            Email(
                id = UUID.randomUUID().toString(),
                emailDetails = details,
                emailDetailsWithAttachment = null,
                priority = Priority.LOW,
                status = Status.PENDING,
                date = Date()
            )
        )
    }
}
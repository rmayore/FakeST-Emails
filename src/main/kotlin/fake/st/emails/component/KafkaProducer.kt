package fake.st.emails.component

import fake.st.emails.entity.request.EmailDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Kafka producer
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 07-04-2023.
 */

@Component
class KafkaProducer(@Autowired val template: KafkaTemplate<String, EmailDetails>) {

    fun send(topic: String, payload: EmailDetails) {
        template.send(topic, UUID.randomUUID().toString(), payload)
        template.flush()
    }
}

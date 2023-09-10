package fake.st.emails.config.kafka

import fake.st.emails.entity.request.EmailDetails
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

/**
 * Kafka Producer configuration
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 22-03-2023.
 */

@Configuration
class KafkaProducerConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var bootstrapAddress: String

    @Bean
    fun producerFactory(): ProducerFactory<String, EmailDetails> {
        return DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress
            ),
            StringSerializer(),
            JsonSerializer()
        )
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, EmailDetails> {
        return KafkaTemplate(producerFactory())
    }
}

package fake.st.emails.config.kafka

import fake.st.emails.entity.request.EmailDetails
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer


/**
 * Kafka consumer configuration
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 22-03-2023.
 */

@EnableKafka
@Configuration
class KafkaConsumerConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var bootstrapAddress: String

    @Bean
    fun consumerFactory(): ConsumerFactory<String, EmailDetails> {
        return DefaultKafkaConsumerFactory(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress,
                ConsumerConfig.GROUP_ID_CONFIG to KAFKA_DEFAULT_GROUP,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest"
            ),
            StringDeserializer(),
            JsonDeserializer(EmailDetails::class.java)
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, EmailDetails> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, EmailDetails>()
        factory.consumerFactory = consumerFactory()
        return factory
    }
}
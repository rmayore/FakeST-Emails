package fake.st.emails.config.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin


/**
 * Kafka topic configuration
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 22-03-2023.
 */

@Configuration
class KafkaTopicConfig {



    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var bootstrapAddress: String

    @Bean
    fun admin() = KafkaAdmin(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress))

    @Bean
    fun topics() = KafkaAdmin.NewTopics(
        TopicBuilder.name(TOPIC_EMAIL_HIGH_PRIORITY)
            .partitions(1)
            .replicas(1)
            .build(),
        TopicBuilder.name(TOPIC_EMAIL_MEDIUM_PRIORITY)
            .partitions(1)
            .replicas(1)
            .build(),
        TopicBuilder.name(TOPIC_EMAIL_LOW_PRIORITY)
            .partitions(1)
            .replicas(1)
            .build()
    )
}
package fake.st.emails.config

import io.lettuce.core.ClientOptions
import io.lettuce.core.protocol.ProtocolVersion
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import kotlin.Any
import kotlin.String

/**
 * Redis configuration
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 07-03-2023.
 */

@Configuration
@EnableRedisRepositories
class RedisConfig {

    @Value("\${redis.host}")
    lateinit var host: String

    @Value("\${redis.port}")
    lateinit var port: String

    @Value("\${redis.password}")
    lateinit var password: String

    @Bean
    fun lettuceConnectionFactory(): LettuceConnectionFactory {
        val builder = LettuceClientConfiguration.builder()

        val clientConfiguration = builder.clientOptions(
            ClientOptions.builder()
                .protocolVersion(ProtocolVersion.RESP2)
                .build()
        ).build()


        val standaloneConfig = RedisStandaloneConfiguration()
        standaloneConfig.hostName = host
        standaloneConfig.setPassword(password)
        standaloneConfig.port = Integer.parseInt(port)

        return LettuceConnectionFactory(standaloneConfig, clientConfiguration)
    }

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
        val template = RedisTemplate<Any, Any>()
        template.setConnectionFactory(connectionFactory)
        return template
    }
}
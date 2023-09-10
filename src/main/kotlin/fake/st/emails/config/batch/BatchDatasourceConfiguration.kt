package fake.st.emails.config.batch

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource

/**
 * H2 Datasource to store batch metadata
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 07-05-2023.
 */

@Configuration
class BatchDatasourceConfiguration {

    companion object {
        const val BATCH_DATA_SOURCE = "batchDataSource"
    }

    @Bean(BATCH_DATA_SOURCE)
    fun batchDataSource(): DataSource {
        val builder = EmbeddedDatabaseBuilder()
        return builder.setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
            .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
            .build()
    }
}

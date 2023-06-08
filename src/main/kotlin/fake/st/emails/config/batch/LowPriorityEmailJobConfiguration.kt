package fake.st.emails.config.batch

import fake.st.emails.entity.batch.EmailBatchResult
import fake.st.emails.entity.redis.Email
import fake.st.emails.entity.redis.Priority
import fake.st.emails.entity.redis.Status
import fake.st.emails.repository.EmailRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.support.IteratorItemReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource


/**
 * Batch configuration for sending low priority emails
 *
 * @author  Robert Mayore.
 * @version 1.0
 * @since    07-05-2023.
 */

@Configuration
class LowPriorityEmailJobConfiguration : DefaultBatchConfiguration() {

    companion object {
        const val STEP_NAME = "LOW_SEND_EMAIL"
        const val JOB_NAME = "LOW_PRIORITY_EMAIL_JOB"
    }

    @Autowired
    @Qualifier(BatchDatasourceConfiguration.BATCH_DATA_SOURCE)
    lateinit var batchDataSource: DataSource

    @Autowired
    lateinit var emailRepository: EmailRepository

    @Autowired
    lateinit var emailItemProcessor: EmailItemProcessor

    @Autowired
    lateinit var emailItemWriter: EmailItemWriter

    override fun getDataSource() = batchDataSource

    override fun getTransactionManager() = DataSourceTransactionManager(batchDataSource)

    @Bean(JOB_NAME)
    fun job(): Job {
        return JobBuilder(JOB_NAME, jobRepository())
            .start(step())
            .build()
    }

    fun step(): Step {
        return StepBuilder(STEP_NAME, jobRepository())
            .chunk<Email, EmailBatchResult>(10, transactionManager)
            .reader(reader())
            .processor(emailItemProcessor)
            .writer(emailItemWriter)
            .build()
    }

    fun reader() = IteratorItemReader(emailRepository.findByStatusAndPriority(Status.PENDING, Priority.LOW))

}
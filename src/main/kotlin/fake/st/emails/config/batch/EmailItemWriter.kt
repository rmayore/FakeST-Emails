package fake.st.emails.config.batch

import fake.st.emails.entity.batch.EmailBatchResult
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

/**
 * Logging ItemWriter
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 07-05-2023.
 */
@Component
@StepScope
open class EmailItemWriter : ItemWriter<EmailBatchResult> {
    override fun write(chunk: Chunk<out EmailBatchResult>) {
        chunk.items.forEach { println("Sent email ${it.emailId} with status: ${it.success}") }
    }
}

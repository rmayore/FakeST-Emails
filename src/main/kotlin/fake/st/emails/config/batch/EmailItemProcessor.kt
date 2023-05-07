package fake.st.emails.config.batch

import fake.st.emails.entity.batch.EmailBatchResult
import fake.st.emails.entity.redis.Email
import fake.st.emails.service.EmailService
import org.springframework.batch.item.ItemProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Email item processor that
 * 1. Sets email status as PENDING
 * 2. Attempts to send email, updates status to IN_PROGRESS
 * 3. Updates email status to SENT orPENDING
 *
 * @author  Robert Mayore.
 * @version 1.0
 * @since   08-04-2023.
 */

@Component
class EmailItemProcessor : ItemProcessor<Email, EmailBatchResult> {

    @Autowired
    lateinit var emailService: EmailService

    @Throws(Exception::class)
    override fun process(email: Email): EmailBatchResult {
        val processedEmail = emailService.prepareForSending(email)
        return EmailBatchResult(email.id, emailService.send(processedEmail))
    }
}
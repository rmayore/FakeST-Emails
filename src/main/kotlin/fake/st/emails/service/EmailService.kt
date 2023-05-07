package fake.st.emails.service


import fake.st.emails.entity.redis.Email
import fake.st.emails.entity.redis.Priority
import org.springframework.validation.annotation.Validated

/**
 * Service for email CRUD operations
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 26-02-2023.
 */
@Validated
interface EmailService {

    fun save(email: Email): Boolean

    fun retrievePending(priority: Priority): MutableList<Email>

    fun prepareForSending(emails: MutableList<Email>): MutableList<Email>

    fun prepareForSending(email: Email): Email

    fun send(email: Email): Boolean

}
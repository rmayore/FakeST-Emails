package fake.st.emails.service

import fake.st.emails.entity.request.EmailDetails
import fake.st.emails.entity.request.EmailDetailsWithAttachment
import jakarta.mail.internet.AddressException
import org.springframework.mail.MailException
import org.springframework.validation.annotation.Validated
import java.io.IOException

/**
 * Service for email sending operations
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 26-02-2023.
 */
@Validated
interface SendEmailService {

    @Throws(AddressException::class, MailException::class)
    fun sendSimpleMail(details: EmailDetails): Boolean

    @Throws(AddressException::class, MailException::class, IOException::class)
    fun sendMailWithAttachment(details: EmailDetailsWithAttachment): Boolean
}

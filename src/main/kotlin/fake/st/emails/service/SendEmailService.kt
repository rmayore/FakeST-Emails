package fake.st.emails.service


import org.springframework.validation.annotation.Validated;
import fake.st.emails.entity.request.EmailDetails;
import fake.st.emails.entity.request.EmailDetailsWithAttachment
import fake.st.emails.entity.response.Response

/**
 * Service for email sending operations
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 26-02-2023.
 */
@Validated
interface SendEmailService {

    fun sendSimpleMail(details: EmailDetails) : Response
 
    fun sendMailWithAttachment(details : EmailDetailsWithAttachment) : Response

}
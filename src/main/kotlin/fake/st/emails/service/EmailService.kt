package fake.st.emails.service


import org.springframework.validation.annotation.Validated;
import fake.st.emails.entity.EmailDetails;
import fake.st.emails.entity.EmailDetailsWithAttachment
import fake.st.emails.entity.response.Response


@Validated
interface EmailService {

    fun sendSimpleMail(details: EmailDetails) : Response
 
    fun sendMailWithAttachment(details : EmailDetailsWithAttachment) : Response

}
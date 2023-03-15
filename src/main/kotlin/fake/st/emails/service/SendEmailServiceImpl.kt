package fake.st.emails.service

import fake.st.emails.entity.request.EmailDetails
import fake.st.emails.entity.request.EmailDetailsWithAttachment
import jakarta.mail.internet.AddressException
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.io.IOException

/**
 * Implementation for email sending operations
 * using spring's native mail package
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 26-02-2023.
 */
@Service
class SendEmailServiceImpl(@Autowired val mailSender: JavaMailSender) : SendEmailService {

    @Value("\${spring.mail.username}")
    lateinit var sender: String

    @Throws(AddressException::class)
    override fun sendSimpleMail(details: EmailDetails): Boolean {
        val mailMessage = SimpleMailMessage()

        mailMessage.from = sender
        mailMessage.setTo(details.recipient)
        mailMessage.text = details.body
        mailMessage.subject = details.subject

        mailSender.send(mailMessage)

        return true
    }

    @Throws(AddressException::class, IOException::class)
    override fun sendMailWithAttachment(details: EmailDetailsWithAttachment): Boolean {

        val mimeMessage: MimeMessage = mailSender.createMimeMessage()

        val mimeMessageHelper = MimeMessageHelper(mimeMessage, true)
        mimeMessageHelper.setFrom(sender)
        mimeMessageHelper.setTo(details.recipient)
        mimeMessageHelper.setText(details.body)
        mimeMessageHelper.setSubject(details.subject)

        val file = ClassPathResource(details.attachment).file
        mimeMessageHelper.addAttachment(file.name, file)

        mailSender.send(mimeMessage)

        return true
    }


}
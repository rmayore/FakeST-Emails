package fake.st.emails.service

import fake.st.emails.entity.EmailDetails
import fake.st.emails.entity.EmailDetailsWithAttachment
import fake.st.emails.entity.response.Response
import jakarta.mail.internet.AddressException
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class EmailServiceImpl(@Autowired val mailSender: JavaMailSender) : EmailService {

    @Value("\${spring.mail.username}")
    lateinit var sender: String

    @Throws(AddressException::class)
    override fun sendSimpleMail(details: EmailDetails): Response {
        val mailMessage = SimpleMailMessage()

        mailMessage.from = sender
        mailMessage.setTo(details.recipient)
        mailMessage.text = details.body
        mailMessage.subject = details.subject

        mailSender.send(mailMessage)

        return Response("Email sent successfully")
    }

    @Throws(AddressException::class, IOException::class)
    override fun sendMailWithAttachment(details: EmailDetailsWithAttachment): Response {

        val mimeMessage: MimeMessage = mailSender.createMimeMessage()

        val mimeMessageHelper = MimeMessageHelper(mimeMessage, true)
        mimeMessageHelper.setFrom(sender)
        mimeMessageHelper.setTo(details.recipient)
        mimeMessageHelper.setText(details.body)
        mimeMessageHelper.setSubject(details.subject)

        val file = ClassPathResource(details.attachment).file
        mimeMessageHelper.addAttachment(file.name, file)

        mailSender.send(mimeMessage)

        return Response("Email sent successfully")
    }


}
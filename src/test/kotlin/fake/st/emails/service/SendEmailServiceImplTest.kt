package fake.st.emails.service

import fake.st.emails.entity.request.EmailDetails
import fake.st.emails.entity.request.EmailDetailsWithAttachment
import jakarta.mail.internet.MimeMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SendEmailServiceImplTest {

    @Test
    fun `send simple mail - sends email if all settings correct`() {
        // define mock behavior
        val mailSender = Mockito.mock(JavaMailSender::class.java)
        Mockito.doNothing().`when`(mailSender).send(any(SimpleMailMessage::class.java))

        // define email service under test
        val service = SendEmailServiceImpl(mailSender)
        ReflectionTestUtils.setField(service, "sender", "example@outlook.com")

        // send test email
        val email = EmailDetails("test@gmail.com", "Subject", "Body")
        val response = service.sendSimpleMail(email)

        // verify results
        val mailMessage = SimpleMailMessage()
        mailMessage.from = "example@outlook.com"
        mailMessage.setTo(email.recipient)
        mailMessage.text = email.body
        mailMessage.subject = email.subject

        Mockito.verify(mailSender, times(1)).send(mailMessage)
        assertThat(response.message).isEqualTo("Email sent successfully")

    }

    @Test
    fun `send mail with attachment - sends email if all settings correct`() {
        // define mock behavior
        val mailSender = Mockito.mock(JavaMailSender::class.java)
        Mockito.doNothing().`when`(mailSender).send(any(MimeMessage::class.java))
        Mockito.`when`(mailSender.createMimeMessage()).thenReturn(JavaMailSenderImpl().createMimeMessage())

        // define email service under test
        val service = SendEmailServiceImpl(mailSender)
        ReflectionTestUtils.setField(service, "sender", "example@outlook.com")

        // send test email
        val email = EmailDetailsWithAttachment("test@gmail.com", "Subject", "Body", "download.png")
        val response = service.sendMailWithAttachment(email)

        // verify results
        val mimeMessage = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, true)
        mimeMessageHelper.setFrom("example@outlook.com")
        mimeMessageHelper.setTo(email.recipient)
        mimeMessageHelper.setText(email.body)
        mimeMessageHelper.setSubject(email.subject)
        val file = ClassPathResource(email.attachment).file
        mimeMessageHelper.addAttachment(file.name, file)

        Mockito.verify(mailSender, times(1)).send(mimeMessage)
        assertThat(response.message).isEqualTo("Email sent successfully")

    }
}
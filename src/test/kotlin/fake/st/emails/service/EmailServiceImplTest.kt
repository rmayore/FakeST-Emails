package fake.st.emails.service

import fake.st.emails.entity.redis.Email
import fake.st.emails.entity.redis.Priority
import fake.st.emails.entity.redis.Status
import fake.st.emails.entity.request.EmailDetails
import fake.st.emails.entity.request.EmailDetailsWithAttachment
import fake.st.emails.repository.EmailRepository
import jakarta.mail.internet.AddressException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.kotlin.any
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmailServiceImplTest {

    private lateinit var emailRepository: EmailRepository
    private lateinit var sendEmailService: SendEmailService
    private lateinit var emailService: EmailServiceImpl

    @BeforeEach
    fun init() {
        emailRepository = Mockito.mock(EmailRepository::class.java)
        sendEmailService = Mockito.mock(SendEmailService::class.java)
        emailService = EmailServiceImpl(emailRepository, sendEmailService)
    }

    @Test
    fun `save() - saves email`() {
        Mockito.`when`(emailRepository.save(any())).thenReturn(null)

        val email = createEmailSimple()
        val success = emailService.save(email)

        val captor: ArgumentCaptor<Email> = ArgumentCaptor.forClass(Email::class.java)
        Mockito.verify(emailRepository).save(captor.capture())
        val argument = captor.value

        assertEquals(email, argument)
        assertTrue(success)
    }

    @Test
    fun `retrievePending() retrieves pending emails`() {
        val pendingEmails =
            mutableListOf<Email>().also { for (i in 1..10) it.add(createEmailSimple(status = Status.PENDING)) }

        Mockito.`when`(emailRepository.findByStatusAndPriority(Status.PENDING, Priority.LOW)).thenReturn(pendingEmails)

        val returnedPendingEmails = emailService.retrievePending(Priority.LOW)
        assertEquals(pendingEmails, returnedPendingEmails)
    }

    @Test
    fun `prepareForSending() updates email status to in-progress`() {
        Mockito.`when`(emailRepository.save(any())).thenReturn(null)

        val pendingEmails =
            mutableListOf<Email>().also { for (i in 1..10) it.add(createEmailSimple(status = Status.PENDING)) }

        val returnedPendingEmails = emailService.prepareForSending(pendingEmails)

        returnedPendingEmails.forEach { assertEquals(it.status, Status.IN_PROGRESS) }
    }

    @Test
    fun `send() - simple email - updates email status to SENT if successful`() {
        Mockito.`when`(emailRepository.save(any())).thenReturn(null)
        Mockito.`when`(sendEmailService.sendSimpleMail(any())).thenReturn(true)

        val email = createEmailSimple(status = Status.IN_PROGRESS)
        val success = emailService.send(email)

        val captor: ArgumentCaptor<Email> = ArgumentCaptor.forClass(Email::class.java)
        Mockito.verify(emailRepository).save(captor.capture())
        val argument2 = captor.value

        assertEquals(argument2.status, Status.SENT)
        assertTrue(success)
    }

    @Test
    fun `send() - simple email - updates email status to PENDING if exception thrown`() {
        Mockito.`when`(emailRepository.save(any())).thenReturn(null)
        Mockito.`when`(sendEmailService.sendSimpleMail(any())).thenThrow(AddressException())

        val email = createEmailSimple(status = Status.IN_PROGRESS)
        try {
            val success = emailService.send(email)
            assertEquals(success, false)
        } catch (ex: AddressException) {
            val captor: ArgumentCaptor<Email> = ArgumentCaptor.forClass(Email::class.java)
            Mockito.verify(emailRepository).save(captor.capture())
            val argument2 = captor.value
            assertEquals(argument2.status, Status.PENDING)
        }
    }

    @Test
    fun `send() - with attachment - updates email status to SENT if successful`() {
        Mockito.`when`(emailRepository.save(any())).thenReturn(null)
        Mockito.`when`(sendEmailService.sendMailWithAttachment(any())).thenReturn(true)

        val email = createEmailWithAttachment(status = Status.IN_PROGRESS)
        val success = emailService.send(email)

        val captor: ArgumentCaptor<Email> = ArgumentCaptor.forClass(Email::class.java)
        Mockito.verify(emailRepository).save(captor.capture())
        val argument2 = captor.value

        assertEquals(argument2.status, Status.SENT)
        assertTrue(success)
    }

    @Test
    fun `send() - with attachment - updates email status to PENDING if exception thrown`() {
        Mockito.`when`(emailRepository.save(any())).thenReturn(null)
        Mockito.`when`(sendEmailService.sendMailWithAttachment(any())).thenThrow(AddressException())

        val email = createEmailWithAttachment(status = Status.IN_PROGRESS)
        try {
            val success = emailService.send(email)
            assertEquals(success, false)
        } catch (ex: AddressException) {
            val captor: ArgumentCaptor<Email> = ArgumentCaptor.forClass(Email::class.java)
            Mockito.verify(emailRepository).save(captor.capture())
            val argument2 = captor.value
            assertEquals(argument2.status, Status.PENDING)
        }
    }

    private fun createEmailSimple(priority: Priority = Priority.LOW, status: Status = Status.PENDING): Email {
        val details = EmailDetails(
            "mayorerobert@gmail.com",
            "Subject",
            "Body"
        )
        return Email(
            id = UUID.randomUUID().toString(),
            emailDetails = details,
            emailDetailsWithAttachment = null,
            priority = priority,
            status = status,
            date = Date()
        )
    }

    private fun createEmailWithAttachment(priority: Priority = Priority.LOW, status: Status = Status.PENDING): Email {
        val details = EmailDetailsWithAttachment(
            "mayorerobert@gmail.com",
            "Subject",
            "Body",
            "download.png"
        )
        return Email(
            id = UUID.randomUUID().toString(),
            emailDetails = null,
            emailDetailsWithAttachment = details,
            priority = priority,
            status = status,
            date = Date()
        )
    }
}

package fake.st.emails.service

import fake.st.emails.entity.redis.Email
import fake.st.emails.entity.redis.Priority
import fake.st.emails.entity.redis.Status
import fake.st.emails.repository.EmailRepository
import jakarta.mail.internet.AddressException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.MailException
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.Date

/**
 * Implementation for email CRUD operations
 * using via a redis repository
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 26-02-2023.
 */
@Service
class EmailServiceImpl(
    @Autowired val emailRepository: EmailRepository,
    @Autowired val sendEmailService: SendEmailService
) : EmailService {
    override fun save(email: Email): Boolean {
        email.priority = email.priority ?: Priority.LOW
        email.status = email.status ?: Status.PENDING
        email.date = email.date ?: Date()

        emailRepository.save(email)

        return true
    }

    override fun retrievePending(priority: Priority): MutableList<Email> {
        return emailRepository.findByStatusAndPriority(Status.PENDING, priority)
    }

    override fun prepareForSending(emails: MutableList<Email>): MutableList<Email> {
        emails.forEach {
            it.status = Status.IN_PROGRESS
            emailRepository.save(it)
        }
        return emails
    }

    override fun send(email: Email): Boolean {
        if (email.emailDetails != null) {
            return try {
                sendEmailService.sendSimpleMail(email.emailDetails)
                email.status = Status.SENT
                save(email)
                true
            } catch (exception: AddressException) {
                exception.printStackTrace()
                email.status = Status.PENDING
                save(email)
                false
            } catch (exception: MailException) {
                exception.printStackTrace()
                email.status = Status.PENDING
                save(email)
                false
            }
        }
        if (email.emailDetailsWithAttachment != null) {
            return try {
                sendEmailService.sendMailWithAttachment(email.emailDetailsWithAttachment)
                email.status = Status.SENT
                save(email)
                true
            } catch (exception: AddressException) {
                exception.printStackTrace()
                email.status = Status.PENDING
                save(email)
                false
            } catch (exception: MailException) {
                exception.printStackTrace()
                email.status = Status.PENDING
                save(email)
                false
            } catch (exception: IOException) {
                exception.printStackTrace()
                email.status = Status.PENDING
                save(email)
                false
            }
        }
        return false
    }
}
package fake.st.emails.controller

import fake.st.emails.entity.redis.Email
import fake.st.emails.entity.redis.Priority
import fake.st.emails.entity.redis.Status
import fake.st.emails.entity.request.EmailDetails
import fake.st.emails.entity.request.EmailDetailsWithAttachment
import fake.st.emails.entity.response.ErrorResponse
import fake.st.emails.entity.response.Response
import fake.st.emails.service.EmailService
import jakarta.validation.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 * Rest controller to schedule and send emails
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 07-03-2023.
 */

@RestController
@RequestMapping(path = ["/email"], produces = [MediaType.APPLICATION_JSON_VALUE])
class EmailController(@Autowired val emailService: EmailService) {

    @GetMapping("send")
    @ResponseStatus(HttpStatus.OK)
    fun sendMail(): Response {
        var pending = mutableListOf<Email>().apply {
            addAll(emailService.retrievePending(Priority.HIGH))
            addAll(emailService.retrievePending(Priority.MEDIUM))
            addAll(emailService.retrievePending(Priority.LOW))
        }

        pending = emailService.prepareForSending(pending)
        var success = 0
        var fails = 0
        pending.forEach {
            emailService.send(it).apply {
                if (this) {
                    success++
                } else {
                    fails++
                }
            }
        }

        return Response("$success successful, $fails failed")
    }

    @PostMapping("schedule")
    @ResponseStatus(HttpStatus.OK)
    fun scheduleMail(@RequestBody details: EmailDetails): Response {
        val sent = emailService.save(
            Email(
                id = UUID.randomUUID().toString(),
                emailDetails = details,
                emailDetailsWithAttachment = null,
                priority = Priority.HIGH,
                status = Status.PENDING,
                date = Date()
            )
        )
        return if (sent) {
            Response("Email added to queue successfully")
        } else {
            Response("Email NOT added to queue")
        }
    }

    @PostMapping("schedule-with-attachment")
    fun scheduleMailWithAttachment(@RequestBody details: EmailDetailsWithAttachment): Response {
        val sent = emailService.save(
            Email(
                id = UUID.randomUUID().toString(),
                emailDetails = null,
                emailDetailsWithAttachment = details,
                priority = Priority.LOW,
                status = Status.PENDING,
                date = Date()
            )
        )
        return if (sent) {
            Response("Email added to queue successfully")
        } else {
            Response("Email NOT added to queue")
        }
    }

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationExceptions(exception: ConstraintViolationException): ErrorResponse {
        val status: String = HttpStatus.BAD_REQUEST.value().toString()
        var message: String = ""
        for (violation in exception.constraintViolations) {
            message = violation.message
            break
        }
        return ErrorResponse(status, message)
    }
}

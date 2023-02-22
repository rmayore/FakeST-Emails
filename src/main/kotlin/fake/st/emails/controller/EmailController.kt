package fake.st.emails.controller

import fake.st.emails.entity.EmailDetails
import fake.st.emails.entity.EmailDetailsWithAttachment
import fake.st.emails.entity.response.ErrorResponse
import fake.st.emails.entity.response.Response
import fake.st.emails.service.EmailService
import jakarta.validation.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.io.IOException


@RestController
@RequestMapping(path = ["/email"], produces = [MediaType.APPLICATION_JSON_VALUE])
class EmailController(@Autowired val emailService: EmailService) {

    @PostMapping("send")
    @ResponseStatus(HttpStatus.OK)
    fun sendMail(@RequestBody details: EmailDetails): Response {
        return emailService.sendSimpleMail(details)
    }

    @PostMapping("send-with-attachment")
    fun sendMailWithAttachment(@RequestBody details: EmailDetailsWithAttachment): Response {
        return emailService.sendMailWithAttachment(details)
    }

    @ExceptionHandler(MailException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleMailExceptions(exception: MailException): ErrorResponse {
        return ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value().toString(), exception.message ?: "")
    }

    @ExceptionHandler(IOException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleIOExceptions(exception: IOException): ErrorResponse {
        return ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value().toString(), exception.message ?: "")
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
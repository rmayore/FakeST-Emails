package fake.st.emails

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EmailsApplication

fun main(args: Array<String>) {
	runApplication<EmailsApplication>(*args)
}

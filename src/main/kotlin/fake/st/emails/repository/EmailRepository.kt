package fake.st.emails.repository

import fake.st.emails.entity.redis.Email
import fake.st.emails.entity.redis.Priority
import fake.st.emails.entity.redis.Status
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Email Repository
 *
 * @author Robert Mayore.
 * @version 1.0
 * @since 26-02-2023.
 */
@Repository
interface EmailRepository : CrudRepository<Email, String> {
    fun findByStatusAndPriority(status: Status, priority: Priority): MutableList<Email>
}

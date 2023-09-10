package fake.st.emails

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@AutoConfigureMockMvc
// @Transactional
abstract class WebIntegrationTest {
    @Autowired
    protected var mvc: MockMvc? = null
}

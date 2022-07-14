package uk.gov.digital.ho.hocs.queue.config.helpers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("localstack")
class QueueHelperTest {

    @Autowired
    @Qualifier("queueHelper")
    internal lateinit var queueHelper: QueueHelper

    @ParameterizedTest
    @MethodSource("getQueuePairNames")
    fun `all queues are valid`(queuePair : QueuePairName) {
        assertDoesNotThrow { queueHelper.getQueuePair(queuePair) }
    }

    @Test
    fun `get migration queue pair throws without property definition`() {
        assertThrows<IllegalArgumentException> { queueHelper.getQueuePair(QueuePairName.MIGRATION) }
    }

    fun getQueuePairNames() : Stream<Arguments> {
        return Stream.of(
            Arguments.of(QueuePairName.SEARCH),
            Arguments.of(QueuePairName.AUDIT),
            Arguments.of(QueuePairName.DOCUMENT),
            Arguments.of(QueuePairName.NOTIFY),
            Arguments.of(QueuePairName.CASECREATOR),
        )
    }

}

package uk.gov.digital.ho.hocs.queue.config.helpers

import org.springframework.stereotype.Component
import uk.gov.digital.ho.hocs.queue.domain.QueuePair
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName

@Component
class QueueHelper(
    queuePairs: List<Pair<QueuePairName, QueuePair>>
) {
    private val queuePairMap: Map<QueuePairName, QueuePair> = queuePairs.toMap()

    fun getQueuePair(client: QueuePairName): QueuePair = queuePairMap.getValue(client)
}

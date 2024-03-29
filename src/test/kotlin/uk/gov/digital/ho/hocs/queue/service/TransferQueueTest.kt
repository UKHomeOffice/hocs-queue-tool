package uk.gov.digital.ho.hocs.queue.service

import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import uk.gov.digital.ho.hocs.queue.domain.QueuePair
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName

class TransferQueueTest : BaseQueueHelper() {

  @ParameterizedTest
  @MethodSource("getQueuePairsWithDlq")
  fun `moves 0 message from DLQ to main queue`(queuePair : QueuePair, queuePairName : QueuePairName) {
    with (queuePair) {
      putMessageOnQueue(dlqClient!!, dlqEndpoint!!,0)
      webTestClient.get().uri("/transfer?queue=$queuePairName")
        .exchange()
        .expectStatus()
        .isOk
      await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(dlqClient!!, dlqEndpoint!!) } matches { it == 0 }
      await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(mainClient!!, mainEndpoint!!) } matches { it == 0 }
    }
  }

  @ParameterizedTest
  @MethodSource("getQueuePairsWithDlq")
  fun `moves 1 message from DLQ to main queue`(queuePair : QueuePair, queuePairName : QueuePairName) {
    with (queuePair) {
      putMessageOnQueue(dlqClient!!, dlqEndpoint!!,1)
      webTestClient.get().uri("/transfer?queue=$queuePairName")
        .exchange()
        .expectStatus()
        .isOk
      await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(dlqClient!!, dlqEndpoint!!) } matches { it == 0 }
      await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(mainClient!!, mainEndpoint!!) } matches { it == 1 }
    }
  }

  @ParameterizedTest
  @MethodSource("getQueuePairsWithDlq")
  fun `moves 2 messages from DLQ to main queue`(queuePair : QueuePair, queuePairName : QueuePairName) {
    with (queuePair) {
      putMessageOnQueue(dlqClient!!, dlqEndpoint!!,2)
      webTestClient.get().uri("/transfer?queue=$queuePairName")
        .exchange()
        .expectStatus()
        .isOk
      await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(dlqClient!!, dlqEndpoint!!) } matches { it == 0 }
      await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(mainClient!!, mainEndpoint!!) } matches { it == 2 }
    }
  }

  @ParameterizedTest
  @ValueSource(strings = ["","?","?queue=","?queue=search","?queue=SAUSAGES"])
  fun `throws an exception if no valid queue pair is referenced`(queryString : String){
      webTestClient.get().uri("/transfer$queryString")
        .exchange()
        .expectStatus()
        .is4xxClientError
  }

  @Test
  fun `throws exception for queue without DLQ`() {
    webTestClient.get().uri("/transfer?queue=${QueuePairName.CASEMIGRATOR}")
      .exchange()
      .expectStatus()
      .is5xxServerError
  }

}

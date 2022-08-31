package uk.gov.digital.ho.hocs.queue.service

import org.awaitility.kotlin.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.digital.ho.hocs.queue.domain.QueuePair
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName

class PrintQueueTest : BaseQueueHelper() {

  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `view 0 message from DLQ`(queuePair : QueuePair, queuePairName : QueuePairName) {
    with (queuePair) {
      putMessageOnDlq(dlqClient!!, dlqEndpoint!!, 0)
      webTestClient.get().uri("/printdlq?queue=$queuePairName")
        .exchange()
        .expectStatus()
        .isOk
      await untilCallTo { getNumberOfMessagesCurrentlyOnDeadLetterQueue(dlqClient!!, dlqEndpoint!!) } matches { it == 0 }
    }
  }

  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `view 1 message from DLQ`(queuePair : QueuePair, queuePairName : QueuePairName) {
    with (queuePair) {
      putMessageOnDlq(dlqClient!!, dlqEndpoint!!,1)
      webTestClient.get().uri("/printdlq?queue=$queuePairName")
        .exchange()
        .expectStatus()
        .isOk
      await untilCallTo { getNumberOfMessagesCurrentlyOnDeadLetterQueue(dlqClient!!, dlqEndpoint!!) } matches { it == 1 }
    }
  }

  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `view 2 messages from DLQ`(queuePair : QueuePair, queuePairName : QueuePairName) {
    with (queuePair) {
      putMessageOnDlq(dlqClient!!, dlqEndpoint!!,2)
      webTestClient.get().uri("/printdlq?queue=$queuePairName")
        .exchange()
        .expectStatus()
        .isOk
      await untilCallTo { getNumberOfMessagesCurrentlyOnDeadLetterQueue(dlqClient!!, dlqEndpoint!!) } matches { it == 2 }
    }
  }

  @Test
  fun `throws exception for queue without DLQ`() {
    webTestClient.get().uri("/printdlq?queue=${QueuePairName.MIGRATION}")
      .exchange()
      .expectStatus()
      .is5xxServerError
  }

}

package uk.gov.digital.ho.hocs.queue.service

import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import uk.gov.digital.ho.hocs.queue.domain.QueuePair
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName

class SendQueueTest : BaseQueueHelper() {

  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `send 1 message to main queue`(queuePair : QueuePair, queuePairName : QueuePairName) {
    with (queuePair) {
      webTestClient.post().uri("/send?queue=$queuePairName")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue("{}"))
        .exchange()
        .expectStatus()
        .isOk

      await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(mainClient!!, mainEndpoint!!) } matches { it == 1 }
    }
  }

  @ParameterizedTest
  @ValueSource(strings = ["","?","?queue=","?queue=search","?queue=SAUSAGES"])
  fun `throws an exception if no valid queue pair is referenced`(queryString : String){
    webTestClient.get().uri("/send$queryString")
      .exchange()
      .expectStatus()
      .is4xxClientError
  }

  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `throws exception for no message in request body`(queuePair : QueuePair, queuePairName : QueuePairName) {
    webTestClient.get().uri("/send?queue=$queuePairName")
      .exchange()
      .expectStatus()
      .is4xxClientError
  }

}

package uk.gov.digital.ho.hocs.queue.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.test.web.reactive.server.returnResult
import uk.gov.digital.ho.hocs.queue.domain.QueuePair
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName

class AttributesQueueTest : BaseQueueHelper() {

  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `view attributes of main queues`(queuePair : QueuePair, queuePairName : QueuePairName) {
    with (queuePair) {
      putMessageOnQueue(mainClient!!, mainEndpoint!!, 2)

      val response = webTestClient.get().uri("/attributes?queue=$queuePairName")
        .exchange();

      response.expectStatus().isOk;

      Assertions.assertTrue(
        response
          .returnResult<Map<String, String>>()
          .responseBody
          .blockFirst()?.getOrDefault(MESSAGE_COUNT_ATTRIBUTE, "-1") == "2"
      )
    }
  }

  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `view attributes of dlq`(queuePair : QueuePair, queuePairName : QueuePairName) {
    with (queuePair) {
      putMessageOnQueue(dlqClient!!, dlqEndpoint!!, 2)

      val response = webTestClient.get().uri("/attributes?queue=$queuePairName&dlq=true")
        .exchange();

      response.expectStatus().isOk;

      Assertions.assertTrue(
        response
          .returnResult<Map<String, String>>()
          .responseBody
          .blockFirst()?.getOrDefault(MESSAGE_COUNT_ATTRIBUTE, "-1") == "2"
      )
    }
  }

  @ParameterizedTest
  @ValueSource(strings = ["","?","?queue=","?queue=search","?queue=SAUSAGES"])
  fun `throws an exception if no valid queue pair is referenced`(queryString : String){
    webTestClient.get().uri("/attributes$queryString")
      .exchange()
      .expectStatus()
      .is4xxClientError
  }

  @Test
  fun `throws exception with non-boolean dlq value`() {
    webTestClient.get().uri("/attributes?queue=${QueuePairName.MIGRATION}&dlq=test")
      .exchange()
      .expectStatus()
      .is4xxClientError
  }

  @Test
  fun `throws exception for queue without DLQ`() {
    webTestClient.get().uri("/attributes?queue=${QueuePairName.MIGRATION}&dlq=true")
      .exchange()
      .expectStatus()
      .is5xxServerError
  }

}

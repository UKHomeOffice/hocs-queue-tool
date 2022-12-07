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

class PurgeQueueTest : BaseQueueHelper() {

    @ParameterizedTest
    @MethodSource("getQueuePairs")
    fun `purge 0 messages from main queue`(queuePair: QueuePair, queuePairName: QueuePairName) {
        with(queuePair) {
            webTestClient.get().uri("/purge?queue=$queuePairName&dlq=${false}")
                .exchange()
                .expectStatus()
                .isOk
            await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(mainClient!!, mainEndpoint!!) } matches { it == 0 }
        }
    }

    @ParameterizedTest
    @MethodSource("getQueuePairsWithDlq")
    fun `purge 0 message from DLQ`(queuePair: QueuePair, queuePairName: QueuePairName) {
        with(queuePair) {
            webTestClient.get().uri("/purge?queue=$queuePairName&dlq=${true}")
                .exchange()
                .expectStatus()
                .isOk
            await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(dlqClient!!, dlqEndpoint!!) } matches { it == 0 }
        }
    }

    @ParameterizedTest
    @MethodSource("getQueuePairs")
    fun `purge 1 messages from main queue`(queuePair: QueuePair, queuePairName: QueuePairName) {
        with(queuePair) {
            putMessageOnQueue(mainClient!!, mainEndpoint!!, 1)
            webTestClient.get().uri("/purge?queue=$queuePairName&dlq=${false}")
                .exchange()
                .expectStatus()
                .isOk
            await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(mainClient!!, mainEndpoint!!) } matches { it == 0 }
        }
    }

    @ParameterizedTest
    @MethodSource("getQueuePairsWithDlq")
    fun `purge 1 message from DLQ`(queuePair: QueuePair, queuePairName: QueuePairName) {
        with(queuePair) {
            putMessageOnQueue(dlqClient!!, dlqEndpoint!!, 1)
            webTestClient.get().uri("/purge?queue=$queuePairName&dlq=${true}")
                .exchange()
                .expectStatus()
                .isOk
            await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(dlqClient!!, dlqEndpoint!!) } matches { it == 0 }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "?", "?queue=", "?queue=search", "?queue=SAUSAGES"])
    fun `throws an exception if no valid queue pair is referenced`(queryString: String) {
        webTestClient.get().uri("/purge$queryString&dlq=${false}")
            .exchange()
            .expectStatus()
            .is4xxClientError
    }

    @Test
    fun `throws exception for request without dlq param`() {
        webTestClient.get().uri("/purge?queue=${QueuePairName.CASEMIGRATOR}")
            .exchange()
            .expectStatus()
            .is4xxClientError
    }

}

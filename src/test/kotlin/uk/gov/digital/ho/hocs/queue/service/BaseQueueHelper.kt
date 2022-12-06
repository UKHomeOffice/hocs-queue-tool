package uk.gov.digital.ho.hocs.queue.service

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.PurgeQueueRequest
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.provider.Arguments
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.digital.ho.hocs.queue.domain.QueuePair
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["localstack"])
abstract class BaseQueueHelper {

  @Autowired
  internal lateinit var queueClients: List<Pair<QueuePairName, QueuePair>>

  @Autowired
  lateinit var webTestClient: WebTestClient

  @BeforeEach
  fun `purge Queues`() {
    queueClients.forEach {
      val queuePair = it.second;

      queuePair.mainClient?.purgeQueue(PurgeQueueRequest(queuePair.mainEndpoint))
      queuePair.dlqClient?.purgeQueue(PurgeQueueRequest(queuePair.dlqEndpoint))
    }
  }

  fun getQueuePairsWithDlq() : Stream<Arguments> {
    return queueClients
      .filterNot { it.second.dlqClient == null }
      .map {
        Arguments.of(it.second, it.first)
    }.stream()
  }

  fun getQueuePairs() : Stream<Arguments> {
    return queueClients.map {
      Arguments.of(it.second, it.first)
    }.stream()
  }

  fun putMessageOnQueue(queueClient: AmazonSQSAsync, queueUrl: String, msgNumber : Int) {
    await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(queueClient, queueUrl) } matches { it == 0 }

    repeat(msgNumber) {
      queueClient.sendMessage(queueUrl, "{\"content\": \"irrelevant\"}")
    }

    await untilCallTo { getNumberOfMessagesCurrentlyOnQueue(queueClient, queueUrl) } matches { it == msgNumber }
  }

  fun getNumberOfMessagesCurrentlyOnQueue(queueClient: AmazonSQSAsync, queueUrl: String): Int? {
    val queueAttributes = queueClient.getQueueAttributes(queueUrl, listOf(MESSAGE_COUNT_ATTRIBUTE))
    return queueAttributes.attributes[MESSAGE_COUNT_ATTRIBUTE]?.toInt()
  }

  companion object {
    const val MESSAGE_COUNT_ATTRIBUTE = "ApproximateNumberOfMessages"
  }
}

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
@ActiveProfiles(profiles = ["localstack", "migration"])
abstract class BaseQueueHelper {

  @Autowired
  internal lateinit var searchAwsSqsClient: AmazonSQSAsync

  @Autowired
  internal lateinit var searchAwsSqsDlqClient: AmazonSQSAsync

  @Autowired
  internal lateinit var auditAwsSqsClient: AmazonSQSAsync

  @Autowired
  internal lateinit var auditAwsSqsDlqClient: AmazonSQSAsync

  @Autowired
  internal lateinit var documentAwsSqsClient: AmazonSQSAsync

  @Autowired
  internal lateinit var documentAwsSqsDlqClient: AmazonSQSAsync

  @Autowired
  internal lateinit var notifyAwsSqsClient: AmazonSQSAsync

  @Autowired
  internal lateinit var notifyAwsSqsDlqClient: AmazonSQSAsync

  @Autowired
  internal lateinit var caseCreatorAwsSqsClient: AmazonSQSAsync

  @Autowired
  internal lateinit var caseCreatorAwsSqsDlqClient: AmazonSQSAsync

  @Autowired
  lateinit var webTestClient: WebTestClient

  @Value("\${search-queue.sqs-queue}")
  lateinit var searchQueueUrl: String

  @Value("\${search-dlq.sqs-queue}")
  lateinit var searchDlqUrl: String

  @Value("\${audit-queue.sqs-queue}")
  lateinit var auditQueueUrl: String

  @Value("\${audit-dlq.sqs-queue}")
  lateinit var auditDlqUrl: String

  @Value("\${document-queue.sqs-queue}")
  lateinit var documentQueueUrl: String

  @Value("\${document-dlq.sqs-queue}")
  lateinit var documentDlqUrl: String

  @Value("\${notify-queue.sqs-queue}")
  lateinit var notifyQueueUrl: String

  @Value("\${notify-dlq.sqs-queue}")
  lateinit var notifyDlqUrl: String

  @Value("\${case-creator-queue.sqs-queue}")
  lateinit var caseCreatorQueueUrl: String

  @Value("\${case-creator-dlq.sqs-queue}")
  lateinit var caseCreatorDlqUrl: String

  @BeforeEach
  fun `purge Queues`() {
    searchAwsSqsClient.purgeQueue(PurgeQueueRequest(searchQueueUrl))
    searchAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(searchDlqUrl))
    auditAwsSqsClient.purgeQueue(PurgeQueueRequest(auditQueueUrl))
    auditAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(auditDlqUrl))
    documentAwsSqsClient.purgeQueue(PurgeQueueRequest(documentQueueUrl))
    documentAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(documentDlqUrl))
    notifyAwsSqsClient.purgeQueue(PurgeQueueRequest(notifyQueueUrl))
    notifyAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(notifyDlqUrl))
    caseCreatorAwsSqsClient.purgeQueue(PurgeQueueRequest(caseCreatorQueueUrl))
    caseCreatorAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(caseCreatorDlqUrl))
  }

  fun getQueuePairs() : Stream<Arguments> {
    return Stream.of(
      Arguments.of(QueuePair(searchAwsSqsClient, searchQueueUrl, searchAwsSqsDlqClient, searchDlqUrl), QueuePairName.SEARCH),
      Arguments.of(QueuePair(auditAwsSqsClient, auditQueueUrl, auditAwsSqsDlqClient, auditDlqUrl),  QueuePairName.AUDIT),
      Arguments.of(QueuePair(documentAwsSqsClient, documentQueueUrl, documentAwsSqsDlqClient, documentDlqUrl), QueuePairName.DOCUMENT),
      Arguments.of(QueuePair(notifyAwsSqsClient, notifyQueueUrl, notifyAwsSqsDlqClient, notifyDlqUrl), QueuePairName.NOTIFY),
      Arguments.of(QueuePair(caseCreatorAwsSqsClient, caseCreatorQueueUrl, caseCreatorAwsSqsDlqClient, caseCreatorDlqUrl), QueuePairName.CASECREATOR),
    )
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

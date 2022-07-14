package uk.gov.digital.ho.hocs.queue.service

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.PurgeQueueRequest
import org.awaitility.kotlin.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
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
class PrintQueueTest {

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

  @Value("\${search-dlq.sqs-queue}")
  lateinit var searchDlqUrl: String

  @Value("\${audit-dlq.sqs-queue}")
  lateinit var auditDlqUrl: String

  @Value("\${document-dlq.sqs-queue}")
  lateinit var documentDlqUrl: String

  @Value("\${notify-dlq.sqs-queue}")
  lateinit var notifyDlqUrl: String

  @Value("\${case-creator-dlq.sqs-queue}")
  lateinit var caseCreatorDlqUrl: String

  @BeforeEach
  fun `purge Queues`() {
    searchAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(searchDlqUrl))
    auditAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(auditDlqUrl))
    documentAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(documentDlqUrl))
    notifyAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(notifyDlqUrl))
    caseCreatorAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(caseCreatorDlqUrl))
  }


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

  fun getQueuePairs() : Stream<Arguments> {
    return Stream.of(
      Arguments.of(QueuePair(searchAwsSqsClient, "", searchAwsSqsDlqClient, searchDlqUrl), QueuePairName.SEARCH),
      Arguments.of(QueuePair(auditAwsSqsClient, "", auditAwsSqsDlqClient, auditDlqUrl),  QueuePairName.AUDIT),
      Arguments.of(QueuePair(documentAwsSqsClient, "", documentAwsSqsDlqClient, documentDlqUrl), QueuePairName.DOCUMENT),
      Arguments.of(QueuePair(notifyAwsSqsClient, "", notifyAwsSqsDlqClient, notifyDlqUrl), QueuePairName.NOTIFY),
      Arguments.of(QueuePair(caseCreatorAwsSqsClient, "", caseCreatorAwsSqsDlqClient, caseCreatorDlqUrl), QueuePairName.CASECREATOR),
    )
  }
}

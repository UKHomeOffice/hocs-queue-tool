package uk.gov.digital.ho.hocs.queue

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.PurgeQueueRequest
import org.awaitility.kotlin.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.digital.ho.hocs.queue.domain.QueuePair
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("localstack")
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
  lateinit var webTestClient: WebTestClient

  @Value("\${search-dlq.sqs-queue}")
  lateinit var searchDlqUrl: String

  @Value("\${audit-dlq.sqs-queue}")
  lateinit var auditDlqUrl: String

  @Value("\${document-dlq.sqs-queue}")
  lateinit var documentDlqUrl: String

  @Value("\${notify-dlq.sqs-queue}")
  lateinit var notifyDlqUrl: String

  @BeforeEach
  fun `purge Queues`() {
    searchAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(searchDlqUrl))
    auditAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(auditDlqUrl))
    documentAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(documentDlqUrl))
    notifyAwsSqsDlqClient.purgeQueue(PurgeQueueRequest(notifyDlqUrl))
  }


  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `view 0 message from DLQ`(queuePair : QueuePair, queuePairName : String) {
    with (queuePair) {
      putMessageOnDlq(dlqClient, dlqEndpoint,0)
      webTestClient.get().uri("/printdlq?queue=$queuePairName")
        .exchange()
        .expectStatus()
        .isOk
      await untilCallTo { getNumberOfMessagesCurrentlyOnDeadLetterQueue(dlqClient, dlqEndpoint) } matches { it == 0 }
    }
  }

  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `view 1 message from DLQ`(queuePair : QueuePair, queuePairName : String) {
    with (queuePair) {
      putMessageOnDlq(dlqClient, dlqEndpoint,1)
      webTestClient.get().uri("/printdlq?queue=$queuePairName")
        .exchange()
        .expectStatus()
        .isOk
      await untilCallTo { getNumberOfMessagesCurrentlyOnDeadLetterQueue(dlqClient, dlqEndpoint) } matches { it == 1 }
    }
  }

  @ParameterizedTest
  @MethodSource("getQueuePairs")
  fun `view 2 messages from DLQ`(queuePair : QueuePair, queuePairName : String) {
    with (queuePair) {
      putMessageOnDlq(dlqClient, dlqEndpoint,2)
      webTestClient.get().uri("/printdlq?queue=$queuePairName")
        .exchange()
        .expectStatus()
        .isOk
      await untilCallTo { getNumberOfMessagesCurrentlyOnDeadLetterQueue(dlqClient, dlqEndpoint) } matches { it == 2 }
    }
  }

  @ParameterizedTest
  @ValueSource(strings = ["","&","&count=","&count=five","&count=-1"])
  fun `ignores if count number is badly referenced`(queryString : String){
    webTestClient.get().uri("/transfer?queue=DOCUMENT&$queryString")
      .exchange()
      .expectStatus()
      .is2xxSuccessful
  }

  fun getQueuePairs() : Stream<Arguments> {
    return Stream.of(
      Arguments.of(QueuePair(searchAwsSqsClient, "", searchAwsSqsDlqClient, searchDlqUrl), "SEARCH"),
      Arguments.of(QueuePair(auditAwsSqsClient, "", auditAwsSqsDlqClient, auditDlqUrl), "AUDIT"),
      Arguments.of(QueuePair(documentAwsSqsClient, "", documentAwsSqsDlqClient, documentDlqUrl),"DOCUMENT"),
      Arguments.of(QueuePair(notifyAwsSqsClient, "", notifyAwsSqsDlqClient, notifyDlqUrl), "NOTIFY")
    )
  }
}

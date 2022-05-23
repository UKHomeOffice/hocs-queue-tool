package uk.gov.digital.ho.hocs.queue.config

import com.amazonaws.services.sqs.AmazonSQSAsync
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uk.gov.digital.ho.hocs.queue.domain.QueuePair
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName

@Component
class QueueHelper(@Qualifier(value = "searchAwsSqsClient") private val searchAwsSqsClient: AmazonSQSAsync,
                  @Qualifier(value = "searchAwsSqsDlqClient") val searchAwsSqsDlqClient: AmazonSQSAsync,
                  @Qualifier(value = "auditAwsSqsClient") val auditAwsSqsClient: AmazonSQSAsync,
                  @Qualifier(value = "auditAwsSqsDlqClient") val auditAwsSqsDlqClient: AmazonSQSAsync,
                  @Qualifier(value = "documentAwsSqsClient") val documentAwsSqsClient: AmazonSQSAsync,
                  @Qualifier(value = "documentAwsSqsDlqClient") val documentAwsSqsDlqClient: AmazonSQSAsync,
                  @Qualifier(value = "notifyAwsSqsClient") val notifyAwsSqsClient: AmazonSQSAsync,
                  @Qualifier(value = "notifyAwsSqsDlqClient") val notifyAwsSqsDlqClient: AmazonSQSAsync,
                  @Qualifier(value = "caseCreatorAwsSqsClient") val caseCreatorAwsSqsClient: AmazonSQSAsync,
                  @Qualifier(value = "caseCreatorAwsSqsDlqClient") val caseCreatorAwsSqsDlqClient: AmazonSQSAsync,
                  @Value("\${search-queue.sqs-queue}") val searchQueueUrl: String,
                  @Value("\${search-dlq.sqs-queue}") val searchDlqUrl: String,
                  @Value("\${audit-queue.sqs-queue}") val auditQueueUrl: String,
                  @Value("\${audit-dlq.sqs-queue}") val auditDlqUrl: String,
                  @Value("\${document-queue.sqs-queue}") val documentQueueUrl: String,
                  @Value("\${document-dlq.sqs-queue}") val documentDlqUrl: String,
                  @Value("\${notify-queue.sqs-queue}") val notifyQueueUrl: String,
                  @Value("\${notify-dlq.sqs-queue}") val notifyDlqUrl: String,
                  @Value("\${case-creator-queue.sqs-queue}") val caseCreatorQueueUrl: String,
                  @Value("\${case-creator-dlq.sqs-queue}") val caseCreatorDlqUrl: String
) {
    fun getQueuePair(client : QueuePairName) : QueuePair {
        return when (client) {
            QueuePairName.SEARCH -> QueuePair(searchAwsSqsClient, searchQueueUrl, searchAwsSqsDlqClient, searchDlqUrl)
            QueuePairName.AUDIT -> QueuePair(auditAwsSqsClient, auditQueueUrl, auditAwsSqsDlqClient, auditDlqUrl)
            QueuePairName.DOCUMENT -> QueuePair(documentAwsSqsClient, documentQueueUrl, documentAwsSqsDlqClient, documentDlqUrl)
            QueuePairName.NOTIFY -> QueuePair(notifyAwsSqsClient, notifyQueueUrl, notifyAwsSqsDlqClient, notifyDlqUrl)
            QueuePairName.CASECREATOR -> QueuePair(caseCreatorAwsSqsClient, caseCreatorQueueUrl, caseCreatorAwsSqsDlqClient, caseCreatorDlqUrl)
        }
    }
}

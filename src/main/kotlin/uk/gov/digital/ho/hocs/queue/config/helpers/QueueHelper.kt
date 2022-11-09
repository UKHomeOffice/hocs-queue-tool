package uk.gov.digital.ho.hocs.queue.config.helpers

import com.amazonaws.services.sqs.AmazonSQSAsync
import io.micrometer.core.lang.Nullable
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uk.gov.digital.ho.hocs.queue.domain.QueuePair
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName

@Component
class QueueHelper(@Qualifier(value = "searchAwsSqsClient") private val searchAwsSqsClient: AmazonSQSAsync?,
                  @Qualifier(value = "searchAwsSqsDlqClient") private val searchAwsSqsDlqClient: AmazonSQSAsync?,
                  @Qualifier(value = "auditAwsSqsClient") private val auditAwsSqsClient: AmazonSQSAsync?,
                  @Qualifier(value = "auditAwsSqsDlqClient") private val auditAwsSqsDlqClient: AmazonSQSAsync?,
                  @Qualifier(value = "documentAwsSqsClient") private val documentAwsSqsClient: AmazonSQSAsync?,
                  @Qualifier(value = "documentAwsSqsDlqClient") private val documentAwsSqsDlqClient: AmazonSQSAsync?,
                  @Qualifier(value = "notifyAwsSqsClient") private val notifyAwsSqsClient: AmazonSQSAsync?,
                  @Qualifier(value = "notifyAwsSqsDlqClient") private val notifyAwsSqsDlqClient: AmazonSQSAsync?,
                  @Qualifier(value = "caseCreatorAwsSqsClient") private val caseCreatorAwsSqsClient: AmazonSQSAsync?,
                  @Qualifier(value = "caseCreatorAwsSqsDlqClient") private val caseCreatorAwsSqsDlqClient: AmazonSQSAsync?,
                  @Qualifier(value = "migrationAwsSqsClient") private val migrationAwsSqsClient: AmazonSQSAsync?,
                  @Value("\${search-queue.sqs-queue}") private val searchQueueUrl: String,
                  @Value("\${search-dlq.sqs-queue}") private val searchDlqUrl: String,
                  @Value("\${audit-queue.sqs-queue}") private val auditQueueUrl: String,
                  @Value("\${audit-dlq.sqs-queue}") private val auditDlqUrl: String,
                  @Value("\${document-queue.sqs-queue}") private val documentQueueUrl: String,
                  @Value("\${document-dlq.sqs-queue}") private val documentDlqUrl: String,
                  @Value("\${notify-queue.sqs-queue}") private val notifyQueueUrl: String,
                  @Value("\${notify-dlq.sqs-queue}") private val notifyDlqUrl: String,
                  @Value("\${case-creator-queue.sqs-queue}") private val caseCreatorQueueUrl: String,
                  @Value("\${case-creator-dlq.sqs-queue}") private val caseCreatorDlqUrl: String,
                  @Value("\${migration-queue.sqs-queue}") private val migrationQueueUrl: String,
) {

    fun getQueuePair(client : QueuePairName) : QueuePair {
        return when (client) {
            QueuePairName.SEARCH -> QueuePair(searchAwsSqsClient, searchQueueUrl, searchAwsSqsDlqClient, searchDlqUrl)
            QueuePairName.AUDIT -> QueuePair(auditAwsSqsClient, auditQueueUrl, auditAwsSqsDlqClient, auditDlqUrl)
            QueuePairName.DOCUMENT -> QueuePair(documentAwsSqsClient, documentQueueUrl, documentAwsSqsDlqClient, documentDlqUrl)
            QueuePairName.NOTIFY -> QueuePair(notifyAwsSqsClient, notifyQueueUrl, notifyAwsSqsDlqClient, notifyDlqUrl)
            QueuePairName.CASECREATOR -> QueuePair(caseCreatorAwsSqsClient, caseCreatorQueueUrl, caseCreatorAwsSqsDlqClient, caseCreatorDlqUrl)
            QueuePairName.MIGRATION -> QueuePair(migrationAwsSqsClient, migrationQueueUrl, null, null)
        }
    }
}

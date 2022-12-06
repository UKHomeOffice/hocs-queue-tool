package uk.gov.digital.ho.hocs.queue.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import uk.gov.digital.ho.hocs.queue.domain.QueuePair
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName

@Configuration
@Profile(
    value = [
        "audit", "case-creator", "case-migrator",
        "document", "notify", "search"
    ]
)
class AwsConfiguration(
    @Value("\${sqs-region}") val region: String
) {

    @Bean
    @Profile("audit")
    fun auditAwsSqsClient(
        @Value("\${audit-queue.access-key-id}") auditAccessKey: String,
        @Value("\${audit-queue.secret-access-key}") auditSecretKey: String,
        @Value("\${audit-dlq.access-key-id}") auditDlqAccessKey: String,
        @Value("\${audit-dlq.secret-access-key}") auditDlqSecretKey: String,
        @Value("\${audit-queue.sqs-queue}") auditQueueUrl: String,
        @Value("\${audit-dlq.sqs-queue}") auditDlqUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.AUDIT, QueuePair(
                createClient(auditAccessKey, auditSecretKey), auditQueueUrl,
                createClient(auditDlqAccessKey, auditDlqSecretKey), auditDlqUrl
            )
        )
    }

    @Bean
    @Profile("case-creator")
    fun caseCreatorAwsSqsClient(
        @Value("\${case-creator-queue.access-key-id}") caseCreatorAccessKey: String,
        @Value("\${case-creator-queue.secret-access-key}") caseCreatorSecretKey: String,
        @Value("\${case-creator-dlq.access-key-id}") caseCreatorDlqAccessKey: String,
        @Value("\${case-creator-dlq.secret-access-key}") caseCreatorDlqSecretKey: String,
        @Value("\${case-creator-queue.sqs-queue}") caseCreatorQueueUrl: String,
        @Value("\${case-creator-dlq.sqs-queue}") caseCreatorDlqUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.CASECREATOR, QueuePair(
                createClient(caseCreatorAccessKey, caseCreatorSecretKey), caseCreatorQueueUrl,
                createClient(caseCreatorDlqAccessKey, caseCreatorDlqSecretKey), caseCreatorDlqUrl
            )
        )
    }

    @Bean
    @Profile("case-migrator")
    fun migrationAwsSqsClient(
        @Value("\${migration-queue.access-key-id}") migrationAccessKey: String,
        @Value("\${migration-queue.secret-access-key}") migrationSecretKey: String,
        @Value("\${migration-queue.sqs-queue}") migrationQueueUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.CASEMIGRATOR, QueuePair(
                createClient(migrationAccessKey, migrationSecretKey), migrationQueueUrl,
                null, null
            )
        )
    }

    @Bean
    @Profile("document")
    fun documentAwsSqsClient(
        @Value("\${document-queue.access-key-id}") documentAccessKey: String,
        @Value("\${document-queue.secret-access-key}") documentSecretKey: String,
        @Value("\${document-dlq.access-key-id}") documentDlqAccessKey: String,
        @Value("\${document-dlq.secret-access-key}") documentDlqSecretKey: String,
        @Value("\${document-queue.sqs-queue}") documentQueueUrl: String,
        @Value("\${document-dlq.sqs-queue}") documentDlqUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.DOCUMENT, QueuePair(
                createClient(documentAccessKey, documentSecretKey), documentQueueUrl,
                createClient(documentDlqAccessKey, documentDlqSecretKey), documentDlqUrl
            )
        )
    }

    @Bean
    @Profile("notify")
    fun notifyAwsSqsClient(
        @Value("\${notify-queue.access-key-id}") notifyAccessKey: String,
        @Value("\${notify-queue.secret-access-key}") notifySecretKey: String,
        @Value("\${notify-dlq.access-key-id}") notifyDlqAccessKey: String,
        @Value("\${notify-dlq.secret-access-key}") notifyDlqSecretKey: String,
        @Value("\${notify-queue.sqs-queue}") notifyQueueUrl: String,
        @Value("\${notify-dlq.sqs-queue}") notifyDlqUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.NOTIFY, QueuePair(
                createClient(notifyAccessKey, notifySecretKey), notifyQueueUrl,
                createClient(notifyDlqAccessKey, notifyDlqSecretKey), notifyDlqUrl
            )
        )
    }

    @Bean
    @Profile("search")
    fun searchAwsSqsClient(
        @Value("\${search-queue.access-key-id}") searchAccessKey: String,
        @Value("\${search-queue.secret-access-key}") searchSecretKey: String,
        @Value("\${search-dlq.access-key-id}") searchDlqAccessKey: String,
        @Value("\${search-dlq.secret-access-key}") searchDlqSecretKey: String,
        @Value("\${search-queue.sqs-queue}") searchQueueUrl: String,
        @Value("\${search-dlq.sqs-queue}") searchDlqUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.SEARCH, QueuePair(
                createClient(searchAccessKey, searchSecretKey), searchQueueUrl,
                createClient(searchDlqAccessKey, searchDlqSecretKey), searchDlqUrl
            )
        )
    }

    fun createClient(accessKey: String, secretKey: String): AmazonSQSAsync {
        val credentials: AWSCredentials = BasicAWSCredentials(accessKey, secretKey)
        return AmazonSQSAsyncClientBuilder
            .standard()
            .withRegion(region)
            .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
    }

}

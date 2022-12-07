package uk.gov.digital.ho.hocs.queue.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import uk.gov.digital.ho.hocs.queue.domain.QueuePair
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName

@Configuration
@Profile("localstack")
class AwsLocalStackConfiguration(
    @Value("\${sqs-region}") val region: String
) {

    @Bean
    fun auditAwsSqsClient(
        @Value("\${audit-queue.endpoint}") auditEndpoint: String,
        @Value("\${audit-dlq.endpoint}") auditDlqEndpoint: String,
        @Value("\${audit-queue.sqs-queue}") auditQueueUrl: String,
        @Value("\${audit-dlq.sqs-queue}") auditDlqUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.AUDIT, QueuePair(
                createClient(auditEndpoint), auditQueueUrl,
                createClient(auditDlqEndpoint), auditDlqUrl
            )
        )
    }

    @Bean
    fun caseCreatorAwsSqsClient(
        @Value("\${case-creator-queue.endpoint}") caseCreatorEndpoint: String,
        @Value("\${case-creator-dlq.endpoint}") caseCreatorDlqEndpoint: String,
        @Value("\${case-creator-queue.sqs-queue}") caseCreatorQueueUrl: String,
        @Value("\${case-creator-dlq.sqs-queue}") caseCreatorDlqUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.CASECREATOR, QueuePair(
                createClient(caseCreatorEndpoint), caseCreatorQueueUrl,
                createClient(caseCreatorDlqEndpoint), caseCreatorDlqUrl
            )
        )
    }

    @Bean
    fun documentAwsSqsClient(
        @Value("\${document-queue.endpoint}") documentEndpoint: String,
        @Value("\${document-dlq.endpoint}") documentDlqEndpoint: String,
        @Value("\${document-queue.sqs-queue}") documentQueueUrl: String,
        @Value("\${document-dlq.sqs-queue}") documentDlqUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.DOCUMENT, QueuePair(
                createClient(documentEndpoint), documentQueueUrl,
                createClient(documentDlqEndpoint), documentDlqUrl
            )
        )
    }

    @Bean
    fun migrationAwsSqsClient(
        @Value("\${migration-queue.endpoint}") migrationEndpoint: String,
        @Value("\${migration-queue.sqs-queue}") migrationQueueUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.CASEMIGRATOR, QueuePair(
                createClient(migrationEndpoint), migrationQueueUrl,
                null, null
            )
        )
    }

    @Bean
    fun notifyAwsSqsClient(
        @Value("\${notify-queue.endpoint}") notifyEndpoint: String,
        @Value("\${notify-dlq.endpoint}") notifyDlqEndpoint: String,
        @Value("\${notify-queue.sqs-queue}") notifyQueueUrl: String,
        @Value("\${notify-dlq.sqs-queue}") notifyDlqUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.NOTIFY, QueuePair(
                createClient(notifyEndpoint), notifyQueueUrl,
                createClient(notifyDlqEndpoint), notifyDlqUrl
            )
        )
    }

    @Bean
    fun opensearchAwsSqsClient(
        @Value("\${opensearch-queue.endpoint}") opensearchEndpoint: String,
        @Value("\${opensearch-dlq.endpoint}") opensearchDlqEndpoint: String,
        @Value("\${opensearch-queue.sqs-queue}") opensearchQueueUrl: String,
        @Value("\${opensearch-dlq.sqs-queue}") opensearchDlqUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.OPENSEARCH, QueuePair(
                createClient(opensearchEndpoint), opensearchQueueUrl,
                createClient(opensearchDlqEndpoint), opensearchDlqUrl
            )
        )
    }

    @Bean
    fun searchAwsSqsClient(
        @Value("\${search-queue.endpoint}") searchEndpoint: String,
        @Value("\${search-dlq.endpoint}") searchDlqEndpoint: String,
        @Value("\${search-queue.sqs-queue}") searchQueueUrl: String,
        @Value("\${search-dlq.sqs-queue}") searchDlqUrl: String
    ): Pair<QueuePairName, QueuePair> {
        return Pair(
            QueuePairName.SEARCH, QueuePair(
                createClient(searchEndpoint), searchQueueUrl,
                createClient(searchDlqEndpoint), searchDlqUrl
            )
        )
    }

    fun createClient(endpoint: String): AmazonSQSAsync {
        return AmazonSQSAsyncClientBuilder
            .standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .withCredentials(AWSStaticCredentialsProvider(AnonymousAWSCredentials())).build()
    }

}

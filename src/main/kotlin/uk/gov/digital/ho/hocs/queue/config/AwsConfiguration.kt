package uk.gov.digital.ho.hocs.queue.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(name = ["sqs-provider"], havingValue = "aws")
class AwsConfiguration(
  @Value("\${search-queue.access-key-id}") val searchAccessKeyId: String,
  @Value("\${search-queue.secret-access-key}") val searchSecretKey: String,
  @Value("\${search-dlq.access-key-id}") val searchDlqAccessKeyId: String,
  @Value("\${search-dlq.secret-access-key}") val searchDlqSecretKey: String,
  @Value("\${audit-queue.access-key-id}") val auditAccessKeyId: String,
  @Value("\${audit-queue.secret-access-key}") val auditSecretKey: String,
  @Value("\${audit-dlq.access-key-id}") val auditDlqAccessKeyId: String,
  @Value("\${audit-dlq.secret-access-key}") val auditDlqSecretKey: String,
  @Value("\${document-queue.access-key-id}") val documentAccessKeyId: String,
  @Value("\${document-queue.secret-access-key}") val documentSecretKey: String,
  @Value("\${document-dlq.access-key-id}") val documentDlqAccessKeyId: String,
  @Value("\${document-dlq.secret-access-key}") val documentDlqSecretKey: String,
  @Value("\${notify-queue.access-key-id}") val notifyAccessKeyId: String,
  @Value("\${notify-queue.secret-access-key}") val notifySecretKey: String,
  @Value("\${notify-dlq.access-key-id}") val notifyDlqAccessKeyId: String,
  @Value("\${notify-dlq.secret-access-key}") val notifyDlqSecretKey: String,
  @Value("\${case-creator-queue.access-key-id}") val caseCreatorAccessKeyId: String,
  @Value("\${case-creator-queue.secret-access-key}") val caseCreatorSecretKey: String,
  @Value("\${case-creator-dlq.access-key-id}") val caseCreatorDlqAccessKeyId: String,
  @Value("\${case-creator-dlq.secret-access-key}") val caseCreatorDlqSecretKey: String,
  @Value("\${migration-queue.access-key-id}") val migrationAccessKeyId: String,
  @Value("\${migration-queue.secret-access-key}") val migrationSecretKey: String,
  @Value("\${sqs-region}") val region: String
) {

  @Bean(name = ["searchAwsSqsClient"])
  @ConditionalOnProperty(prefix = "search-queue", name = ["sqs-queue"])
  fun searchAwsSqsClient(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(searchAccessKeyId, searchSecretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

  @Bean(name = ["searchAwsSqsDlqClient"])
  @ConditionalOnProperty(prefix = "search-dlq", name = ["sqs-queue"])
  fun searchAwsSqsDlqClient(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(searchDlqAccessKeyId, searchDlqSecretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

  @Bean(name = ["auditAwsSqsClient"])
  @ConditionalOnProperty(prefix = "audit-queue", name = ["sqs-queue"])
  fun auditAwsSqsClient(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(auditAccessKeyId, auditSecretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

  @Bean(name = ["auditAwsSqsDlqClient"])
  @ConditionalOnProperty(prefix = "audit-dlq", name = ["sqs-queue"])
  fun auditAwsSqsDlqClient(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(auditDlqAccessKeyId, auditDlqSecretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

  @Bean(name = ["documentAwsSqsClient"])
  @ConditionalOnProperty(prefix = "document-queue", name = ["sqs-queue"])
  fun documentAwsSqsClient(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(documentAccessKeyId, documentSecretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

  @Bean(name = ["documentAwsSqsDlqClient"])
  @ConditionalOnProperty(prefix = "document-dlq", name = ["sqs-queue"])
  fun documentAwsSqsDlqClient(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(documentDlqAccessKeyId, documentDlqSecretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

  @Bean(name = ["notifyAwsSqsClient"])
  @ConditionalOnProperty(prefix = "notify-queue", name = ["sqs-queue"])
  fun notifyAwsSqsClient(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(notifyAccessKeyId, notifySecretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

  @Bean(name = ["notifyAwsSqsDlqClient"])
  @ConditionalOnProperty(prefix = "notify-dlq", name = ["sqs-queue"])
  fun notifyAwsSqsDlqClient(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(notifyDlqAccessKeyId, notifyDlqSecretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

  @Bean(name = ["caseCreatorAwsSqsClient"])
  @ConditionalOnProperty(prefix = "case-creator-queue", name = ["sqs-queue"])
  fun caseCreatorAwsSqsClient(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(caseCreatorAccessKeyId, caseCreatorSecretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

  @Bean(name = ["caseCreatorAwsSqsDlqClient"])
  @ConditionalOnProperty(prefix = "case-creator-dlq", name = ["sqs-queue"])
  fun caseCreatorAwsSqsDlqClient(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(caseCreatorDlqAccessKeyId, caseCreatorDlqSecretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

  @Bean(name = ["migrationAwsSqsClient"])
  @ConditionalOnProperty(prefix = "migration-queue", name = ["sqs-queue"])
  fun migrationAwsSqsClient(): AmazonSQSAsync {
    val credentials: AWSCredentials = BasicAWSCredentials(migrationAccessKeyId, migrationSecretKey)
    return AmazonSQSAsyncClientBuilder
      .standard()
      .withRegion(region)
      .withCredentials(AWSStaticCredentialsProvider(credentials)).build()
  }

}

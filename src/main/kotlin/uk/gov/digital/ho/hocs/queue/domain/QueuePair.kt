package uk.gov.digital.ho.hocs.queue.domain

import com.amazonaws.services.sqs.AmazonSQSAsync

data class QueuePair(
  val mainClient : AmazonSQSAsync?,
  val mainEndpoint : String?,
  val dlqClient : AmazonSQSAsync?,
  val dlqEndpoint : String?
)

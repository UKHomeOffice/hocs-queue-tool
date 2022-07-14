package uk.gov.digital.ho.hocs.queue.service

import com.amazonaws.services.sqs.AmazonSQSAsync
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo

fun putMessageOnDlq(dlqClient: AmazonSQSAsync, dlqUrl: String, msgNumber : Int) {
  await untilCallTo { getNumberOfMessagesCurrentlyOnDeadLetterQueue(dlqClient, dlqUrl) } matches { it == 0 }

  repeat(msgNumber) {
    dlqClient.sendMessage(dlqUrl, "{\"content\": \"irrelevant\"}")
  }

  await untilCallTo { getNumberOfMessagesCurrentlyOnDeadLetterQueue(dlqClient, dlqUrl) } matches { it == msgNumber }
}

fun getNumberOfMessagesCurrentlyOnDeadLetterQueue(dlqClient: AmazonSQSAsync, dlqUrl: String): Int? {
  return getNumberOfMessagesCurrentlyOnEventQueue(dlqClient, dlqUrl)
}

fun getNumberOfMessagesCurrentlyOnEventQueue(queueClient: AmazonSQSAsync, queueUrl: String): Int? {
  val queueAttributes = queueClient.getQueueAttributes(queueUrl, listOf("ApproximateNumberOfMessages"))
  return queueAttributes.attributes["ApproximateNumberOfMessages"]?.toInt()
}

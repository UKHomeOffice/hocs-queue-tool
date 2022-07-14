package uk.gov.digital.ho.hocs.queue.service

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.PurgeQueueRequest
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import uk.gov.digital.ho.hocs.queue.config.helpers.QueueHelper
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName

@Service
class QueueAdminService(
    @Qualifier("queueHelper") private val queueHelper: QueueHelper,
    private val gson: Gson
) {
    /* Transfer the messages one at a time */
    fun transferMessages(name: QueuePairName) {
        with (queueHelper.getQueuePair(name)) {
            if (dlqClient == null || dlqEndpoint == null) {
                throw IllegalArgumentException("No DLQ setup for queue $name")
            }

            repeat(getMessageCount(dlqClient, dlqEndpoint)) {
                dlqClient.receiveOneMessage(dlqEndpoint)?.let { msg ->
                    mainClient.sendMessage(mainEndpoint, msg.body)
                    dlqClient.deleteMessage(DeleteMessageRequest(dlqEndpoint, msg.receiptHandle))
                }
            }.also { log.info("Transferred messages from $name dead letter queue to main queue") }
        }
    }

    /* Remove messages from the DLQ */
    fun purgeMessages(name: QueuePairName) {
        with (queueHelper.getQueuePair(name)) {
            if (dlqClient == null || dlqEndpoint == null) {
                throw IllegalArgumentException("No DLQ setup for queue $name")
            }

            dlqClient.purgeQueue(PurgeQueueRequest(dlqEndpoint)).also { log.info("Purged the dead letter queue") }
        }
    }

    /* Lists all messages on a dlq without acknowledging them */
    fun printMessages(name: QueuePairName, num: Int?) : List<String> {
        val messages = mutableListOf<String>()

        with (queueHelper.getQueuePair(name)) {
            if (dlqClient == null || dlqEndpoint == null) {
                throw IllegalArgumentException("No DLQ setup for queue $name")
            }

            val msgCount = getMessageCount(dlqClient, dlqEndpoint)
            repeat(if (msgCount >0) num ?: msgCount else 0) {
                dlqClient.receiveOneMessage(dlqEndpoint)?.let { msg ->
                    log.info(msg.body)
                    messages.add(gson.fromJson(msg.body, HashMap::class.java).toString())
                }

            }.also { log.info("Read messages from $name dead letter queue") }
        }
        return messages
    }

    private fun getMessageCount(amazonSQS: AmazonSQS, dlqUrl : String) =
        amazonSQS.getQueueAttributes(dlqUrl,listOf("ApproximateNumberOfMessages")).attributes["ApproximateNumberOfMessages"]?.toInt() ?: 0

    private fun AmazonSQSAsync.receiveOneMessage(dlqEndpoint : String) =
        this.receiveMessage(ReceiveMessageRequest(dlqEndpoint).withMaxNumberOfMessages(1)).messages.firstOrNull()

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}

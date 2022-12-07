package uk.gov.digital.ho.hocs.queue.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName
import uk.gov.digital.ho.hocs.queue.service.QueueAdminService

@RestController
class QueueAdminController(private val queueAdminService: QueueAdminService) {

    @GetMapping("/transfer")
    fun transferMessagesFromDeadLetterQueue(@RequestParam(name = "queue") pair: QueuePairName) {
        queueAdminService.transferMessages(pair)
    }

    @GetMapping("/purge")
    fun purgeMessagesFromQueue(
        @RequestParam(name = "queue") pair: QueuePairName,
        @RequestParam(name = "dlq") dlq: Boolean
    ) {
        queueAdminService.purgeMessages(pair, dlq)
    }

    @GetMapping("/printdlq")
    fun printMessagesFromDeadLetterQueue(
        @RequestParam(name = "queue") pair: QueuePairName,
        @RequestParam(name = "count") num: Int?
    ): ResponseEntity<List<String>> {
        return ResponseEntity.ok(queueAdminService.printMessages(pair, num))
    }

    @PostMapping("/send")
    fun sendMessageToMainQueue(
        @RequestParam(name = "queue") pair: QueuePairName,
        @RequestBody message: String
    ): ResponseEntity<String> {
        return ResponseEntity.ok(queueAdminService.sendMessage(pair, message));
    }

    @GetMapping("/attributes")
    fun printAttributesOfQueue(
        @RequestParam(name = "queue") pair: QueuePairName,
        @RequestParam(name = "dlq", defaultValue = false.toString()) dlq: Boolean
    ): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(queueAdminService.printAttributes(pair, dlq));
    }

}

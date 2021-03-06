package uk.gov.digital.ho.hocs.queue.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.digital.ho.hocs.queue.service.QueueAdminService
import uk.gov.digital.ho.hocs.queue.domain.enum.QueuePairName

@RestController
class QueueAdminController(private val queueAdminService: QueueAdminService) {

  @GetMapping("/transfer")
  fun transferMessagesFromDeadLetterQueue(@RequestParam(name = "queue") pair : QueuePairName) {
    queueAdminService.transferMessages(pair)
  }

  @GetMapping("/purgedlq")
  fun purgeMessagesFromDeadLetterQueue(@RequestParam(name = "queue") pair : QueuePairName) {
    queueAdminService.purgeMessages(pair)
  }

  @GetMapping("/printdlq")
  fun printMessagesFromDeadLetterQueue(@RequestParam(name = "queue") pair : QueuePairName, @RequestParam(name = "count") num: Int?) : ResponseEntity<List<String>> {
    return ResponseEntity.ok(queueAdminService.printMessages(pair, num))
  }

}

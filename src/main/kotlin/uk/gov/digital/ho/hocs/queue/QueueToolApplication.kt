package uk.gov.digital.ho.hocs.queue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QueueToolApplication

fun main(args: Array<String>) {
	runApplication<QueueToolApplication>(*args)
}

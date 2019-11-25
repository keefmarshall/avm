package uk.eleusis.challenges.avm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AvmApplication

fun main(args: Array<String>) {
    runApplication<AvmApplication>(*args)
}

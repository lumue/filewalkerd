package net.lumue.filewalkerd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FilewalkerdApplication

fun main(args: Array<String>) {
    runApplication<FilewalkerdApplication>(*args)
}

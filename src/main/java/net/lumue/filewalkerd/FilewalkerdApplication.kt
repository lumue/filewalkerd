package net.lumue.filewalkerd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class FilewalkerdApplication

fun main(args: Array<String>) {
    runApplication<FilewalkerdApplication>(*args)
}

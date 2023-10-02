package net.lumue.filewalkerd.scanner

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.Future

@OptIn(ExperimentalCoroutinesApi::class)
@Component
class ScanRunner(val taskScheduler: TaskScheduler) {

    private val logger = KotlinLogging.logger {}
    fun runScan(path: String, scanner: PathScanner) : Future<*> {
        logger.info("runScan path:$path scanner:$scanner")

        val future = taskScheduler.schedule({ scanner.execute(path) }, Instant.now())

        logger.info("exit runScan:$path scaner:$scanner result: $future")
        return future
    }

}

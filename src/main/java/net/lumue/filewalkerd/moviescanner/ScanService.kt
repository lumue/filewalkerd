package net.lumue.filewalkerd.moviescanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

@OptIn(ExperimentalCoroutinesApi::class)
@Service
class ScanService(val fileHandlingTaskRegistry: FileHandlingTasks = FileHandlingTasks()
) {

    private val logger = KotlinLogging.logger {}

    val runningTasks: MutableList<Unit> = mutableListOf()



    @Async()
    fun startScan(path: String, fileHandlerId: String): Future<String> {
        logger.info { "startScan path: $path fileHandlerId: $fileHandlerId" }

        val runningScan =
            fileHandlingTaskRegistry.byName(fileHandlerId).execute(path)
        runningTasks.add(runningScan)

        logger.info { "exit startScan path: $path fileHandlerId: $fileHandlerId" }
        return CompletableFuture.completedFuture("")
    }

}

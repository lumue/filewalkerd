package net.lumue.filewalkerd.scanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@OptIn(ExperimentalCoroutinesApi::class)
@Service
class ScanService(
    val scannerRegistry: ScannerRegistry,
    val scanRunner: ScanRunner
) {

    private val logger = KotlinLogging.logger {}
    @Async
    fun startScan(path: String, fileHandlerId: String){
        runBlocking {
            logger.info { "startScan path: $path fileHandlerId: $fileHandlerId" }
            scanRunner.runScan(path,scannerRegistry.byName(fileHandlerId))
            logger.info { "exit startScan path: $path fileHandlerId: $fileHandlerId" }
        }
    }

}

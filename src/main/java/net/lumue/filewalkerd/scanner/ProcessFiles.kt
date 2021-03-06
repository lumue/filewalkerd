package net.lumue.filewalkerd.scanner


import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class ProcessFiles(
        private val fileFilter: (file: File) -> Boolean = { true },
        private val handleFile: suspend (file: File) -> Any = {},
        private val context: CoroutineContext = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
) {

    private val logger: Logger = LoggerFactory.getLogger(ProcessFiles::class.java)


    operator fun invoke(
            path: String,
            consumerCount: Int = 20) {

        val rootPath = File(path)
        if (!rootPath.exists() || !rootPath.isDirectory)
            return

        runBlocking {
            val producer = launchFileListProducer(rootPath)

            val consumer = List(consumerCount) {
                launchFileConsumer(producer, handleFile)
            }
            consumer.forEach { it.join() }
        }
    }

    private fun CoroutineScope.launchFileConsumer(
            producer: ReceiveChannel<File>,
            handleFile: suspend (file: File) -> Any): Deferred<Unit> {

        return this.async(context) {
            for (file in producer) {
                try {
                    logger.debug("processing $file")
                    handleFile(file)
                    logger.debug("$file processed")
                } catch (t: Throwable) {
                    logger.error("error processing $file: ${t.message}", t)
                }
            }
        }

    }


    private fun CoroutineScope.launchFileListProducer(rootPath: File): ReceiveChannel<File> {
        return this.produce(context) {
            rootPath.walkBottomUp()
                    .filter { fileFilter(it) }
                    .forEach {
                        logger.debug("selected $it for processing ")
                        send(it)
                    }
            close()
        }
    }
}


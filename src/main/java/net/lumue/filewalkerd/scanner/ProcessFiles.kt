package net.lumue.filewalkerd.scanner


import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
internal class ProcessFiles(
    private val fileFilter: (file: File) -> Boolean = { true },
    private val handleFile: suspend (file: File) -> Any = {},
    private val context: CoroutineContext = Default
) {

    private val logger: Logger = LoggerFactory.getLogger(ProcessFiles::class.java)


    operator fun invoke(
        path: String,
        consumerCount: Int = 20
    ) {

        val rootPath = File(path)

        if (!rootPath.exists() || !rootPath.isDirectory)
            throw IllegalArgumentException("$path is not a valid directory")

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
        handleFile: suspend (file: File) -> Any
    ): Deferred<Unit> {

        return this.async(context) {
            for (file in producer) {
                try {
                    logger.debug("processing {}",file)
                    handleFile(file)
                    logger.debug("{} processed", file)
                } catch (t: Throwable) {
                    logger.error(/* p0 = */ "error processing $file: ${t.message}", /* p1 = */ t)
                }
            }
        }

    }


    private fun CoroutineScope.launchFileListProducer(rootPath: File): ReceiveChannel<File> {
        return this.produce(context) {
            rootPath.walkBottomUp()
                .filter { fileFilter(it) }
                .forEach {
                    logger.debug("selected {} for processing ", it)
                    send(it)
                }
            close()
        }
    }
}


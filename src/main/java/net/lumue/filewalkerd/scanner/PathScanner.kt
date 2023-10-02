package net.lumue.filewalkerd.scanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KotlinLogging
import java.io.File

@ExperimentalCoroutinesApi
open class PathScanner(
    private val fileFilter: (file: File) -> Boolean = { true },
    private val fileHandler: FileHandler
)
{
    private val logger = KotlinLogging.logger {}
    fun execute(path: String) {
        logger.info { "execute $path" }
        val processFiles = ProcessFiles(
            fileFilter,
            handleFile = { f ->
                fileHandler.handleFile(f)
            })
        processFiles.invoke(path)
        logger.info { "exit execute path: $path" }
    }

    interface FileHandler {
        fun handleFile(file: File)
    }
}
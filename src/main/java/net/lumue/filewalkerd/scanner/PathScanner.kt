package net.lumue.filewalkerd.scanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import mu.KotlinLogging
import java.io.File
import java.util.concurrent.ForkJoinPool

@ExperimentalCoroutinesApi
open class PathScanner(
    private val fileFilter: (file: File) -> Boolean = { true },
    private val fileHandler: FileHandler
) {
    private val logger = KotlinLogging.logger {}
    fun execute(path: String) {
        logger.info { "execute $path" }
        val processFiles = ProcessFiles(
            fileFilter,
            handleFile = ::handleGivenFile,
            context = ForkJoinPool.commonPool().asCoroutineDispatcher()
        )
        processFiles.invoke(path)
        logger.info { "exit execute path: $path" }
    }

    private fun handleGivenFile(file: File) {
        fileHandler.handleFile(file)
    }

    fun interface FileHandler {
        fun handleFile(file: File)
    }
}
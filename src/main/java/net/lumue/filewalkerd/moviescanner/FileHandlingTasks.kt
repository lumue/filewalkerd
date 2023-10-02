@file:OptIn(ExperimentalCoroutinesApi::class)

package net.lumue.filewalkerd.moviescanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KotlinLogging
import net.lumue.filewalkerd.scanner.FileHandler
import net.lumue.filewalkerd.scanner.ProcessFiles
import net.lumue.filewalkerd.util.FileHelper.isVideoFile
import java.io.File

class FileHandlingTasks {

    @OptIn(ExperimentalCoroutinesApi::class)
    val fileHandlingTasks: Map<String,FileHandlingTask> = mapOf("movieNfoTask" to CollectAndWriteMovieMetadataTask())

    fun byName(name:String) : FileHandlingTask{
        return fileHandlingTasks[name]!!
    }

    @ExperimentalCoroutinesApi
    open class FileHandlingTask(
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
    }

    @ExperimentalCoroutinesApi
    class CollectAndWriteMovieMetadataTask : FileHandlingTask(
        fileFilter = { f -> f.isVideoFile },
        NfoWriterFileHandler(true)
    )

    @ExperimentalCoroutinesApi
    class RenameMovieTask : FileHandlingTask(
        fileFilter = { f -> f.isVideoFile },
        RenameToNormalizedFileNameFileHandler()
    )
}

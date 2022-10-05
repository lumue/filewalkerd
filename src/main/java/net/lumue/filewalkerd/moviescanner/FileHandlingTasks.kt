package net.lumue.filewalkerd.moviescanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.lumue.filewalkerd.scanner.FileHandler
import net.lumue.filewalkerd.scanner.ProcessFiles
import net.lumue.filewalkerd.util.FileHelper.isVideoFile
import java.io.File

@ExperimentalCoroutinesApi
open class FileHandlingTask(
    private val fileFilter: (file: File) -> Boolean = { true },
    private val fileHandler: FileHandler
) {
    fun execute(path: String) {
        val processFiles = ProcessFiles(
            fileFilter,
            handleFile = { f ->
                fileHandler.handleFile(f)
            })
        processFiles.invoke(path)
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
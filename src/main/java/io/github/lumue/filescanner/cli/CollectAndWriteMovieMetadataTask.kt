package io.github.lumue.filescanner.cli

import io.github.lumue.filescanner.metadata.fileeventhandler.NfoWriterFileHandler
import io.github.lumue.filescanner.util.FileNamingUtils
import io.github.lumue.woelkchen.media.import.ProcessFiles
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

private val File.isVideoFile: Boolean
    get() {
        return FileNamingUtils.isVideoFileExtension(this)
    }

@ExperimentalCoroutinesApi
class CollectAndWriteMovieMetadataTask(
    private val fileFilter: (file: File) -> Boolean = {f->f.isVideoFile },
    private val fileHandler:NfoWriterFileHandler = NfoWriterFileHandler(true),
)
{
    fun execute(path:String) {
        val processFiles :ProcessFiles =ProcessFiles(
            fileFilter,
            handleFile = {f->fileHandler.handleFile(f)
        })
        processFiles.invoke(path)
    }
}
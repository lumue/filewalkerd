package net.lumue.filewalkerd.cli

import net.lumue.filewalkerd.metadata.fileeventhandler.NfoWriterFileHandler
import net.lumue.filewalkerd.util.FileNamingUtils
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
    private val fileHandler: NfoWriterFileHandler = NfoWriterFileHandler(true),
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

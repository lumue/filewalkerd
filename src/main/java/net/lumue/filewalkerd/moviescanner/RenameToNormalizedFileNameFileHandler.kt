package net.lumue.filewalkerd.moviescanner

import io.github.lumue.nfotools.Movie
import io.github.lumue.nfotools.NfoMovieSerializer
import net.lumue.filewalkerd.scanner.FileHandler
import net.lumue.filewalkerd.util.FileHelper.getNfoFilename
import net.lumue.filewalkerd.util.FileHelper.isVideoFileExtension
import net.lumue.filewalkerd.util.FileHelper.nfoMetadataFileExists
import net.lumue.filewalkerd.util.FileHelper.resolveInfoJsonPath
import net.lumue.filewalkerd.util.FileHelper.resolveNfoPath
import net.lumue.filewalkerd.util.renameToOrWarn
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.FilenameUtils.getFullPath
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream

class RenameToNormalizedFileNameFileHandler : FileHandler {

    private val movieSerializer = NfoMovieSerializer()

    override fun handleFile(file: File) {
        try {
            val originalMovieFilename = file.toString()
            if (isVideoFileExtension(file) && nfoMetadataFileExists(originalMovieFilename)) {
                val originalNfoFilename = getNfoFilename(originalMovieFilename)
                val nfoFile = resolveNfoPath(file).toFile()
                val infoJsonFile = resolveInfoJsonPath(file).toFile()
                val movieMetadata = movieSerializer.deserialize(FileInputStream(nfoFile))
                val newBaseFilename = sanitizeBasename(originalNfoFilename, movieMetadata)
                val newNfoFilename = "$newBaseFilename.nfo"
                val newMovieFilename = newBaseFilename + "." + FilenameUtils.getExtension(originalMovieFilename)
                file.renameToOrWarn(File(newMovieFilename), LOGGER)
                nfoFile.renameToOrWarn(File(newNfoFilename), LOGGER)
                val newInfoJsonFilenam = "$newBaseFilename.info.json"
                if (infoJsonFile.exists())
                    infoJsonFile.renameToOrWarn(File(newInfoJsonFilenam), LOGGER)
                LOGGER.info("$originalMovieFilename renamed to $newBaseFilename")
            }
        } catch (e: Throwable) {
            LOGGER.error("error processing file $file", e)
        }
    }

    private fun sanitizeBasename(originalNfoFilename: String, movieMetadata: Movie) =
        getFullPath(originalNfoFilename) + movieMetadata.title
            .replace("Teil ", "Part ")
            .replace("Episode ", "Part ")
            .replace("Vol. ", "Part ")
            .replace("Volume ", "Part ")
            .replace("Chapter ", "Part ")
            .replace("#", "")
            .replace("&", "+")
            .replace("´", "")
            .replace("?"," ")

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RenameToNormalizedFileNameFileHandler::class.java)
    }

}




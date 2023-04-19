package net.lumue.filewalkerd.moviescanner

import io.github.lumue.nfotools.Movie
import io.github.lumue.nfotools.Movie.MovieBuilder
import io.github.lumue.nfotools.NfoMovieSerializer
import net.lumue.filewalkerd.scanner.FileHandler
import net.lumue.filewalkerd.util.FileHelper.isVideoFileExtension
import net.lumue.filewalkerd.util.FileHelper.nfoMetadataFileExists
import net.lumue.filewalkerd.util.FileHelper.resolveInfoJsonPath
import net.lumue.filewalkerd.util.FileHelper.resolveMetaJsonPath
import net.lumue.filewalkerd.util.FileHelper.resolveNfoPath
import org.apache.commons.io.FilenameUtils.getBaseName
import org.apache.commons.io.FilenameUtils.getFullPath
import org.slf4j.LoggerFactory
import java.io.*
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.xml.bind.JAXBException

class NfoWriterFileHandler(private val overwriteExistingNfo: Boolean) : FileHandler {
    private val movieSerializer: NfoMovieSerializer = NfoMovieSerializer()
    private val smartActorResolverMetadataUpdater: SmartActorResolverMetadataUpdater =
        SmartActorResolverMetadataUpdater()

    override fun handleFile(file: File) {
        try {
            val filename = file.toString()
            if (isVideoFileExtension(file)) {
                val nfoMetadataFileExists = nfoMetadataFileExists(filename)
                if (!overwriteExistingNfo && nfoMetadataFileExists) {
                    LOGGER.warn("nfo file already exists for$filename")
                    return
                }
                val movieBuilder: MovieBuilder = if (nfoMetadataFileExists) {
                    val nfoLocation = resolveNfoPath(file).toFile()
                    try {
                        createBuilderFromNfo(nfoLocation)
                    } catch (e: MetadataSourceAccessError) {
                        LOGGER.warn(
                            "could not load movie from nfo file " + nfoLocation.absolutePath + ", starting fresh:",
                            e
                        )
                        Movie.builder()
                    }
                } else Movie.builder()
                val infoJsonLocation = resolveInfoJsonPath(file).toFile()
                val metaJsonLocation = resolveMetaJsonPath(file).toFile()
                if (infoJsonLocation.exists()) file.setLastModified(infoJsonLocation.lastModified())
                configureFromMediafile(file, movieBuilder)
                if (!infoJsonLocation.exists() && !metaJsonLocation.exists()) {
                    LOGGER.warn("no download metadata found for $file")
                    if (!nfoMetadataFileExists) writeNfoFile(movieBuilder.build(), filename)
                    return
                } else {
                    try {
                        configureFromInfoJson(infoJsonLocation, movieBuilder)
                        configureFromMetaJson(metaJsonLocation, movieBuilder)
                    } catch (e: MetadataSourceAccessError) {
                        LOGGER.warn("could not load additional metadata", e)
                    }
                }
                smartActorResolverMetadataUpdater.configureNfoMovieBuilder(movieBuilder)
                writeNfoFile(movieBuilder.build(), filename)
            }
        } catch (e: Exception) {
            LOGGER.error("error processing file $file", e)
        }
    }

    @Throws(JAXBException::class, IOException::class)
    private fun writeNfoFile(movie: Movie, mediaFileName: String) {
        val nfoFilename = getBaseName(mediaFileName) + ".nfo"
        LOGGER.info("serializing metadata for \"${movie.title}\" to \"${nfoFilename}\"")
        val outputStream: OutputStream = FileOutputStream(getFullPath(mediaFileName) + nfoFilename)
        movieSerializer.serialize(movie, outputStream)
        outputStream.close()
    }

    private fun createBuilderFromNfo(file: File): MovieBuilder {
        return try {
            movieSerializer.deserialize(FileInputStream(file)).copyBuilder()
        } catch (e: Exception) {
            throw MetadataSourceAccessError("could not load movie from nfo file " + file.absolutePath, e)
        }
    }

    private fun configureFromMediafile(file: File, movieBuilder: MovieBuilder) {
        if (!file.exists()) {
            LOGGER.warn("can no longer access $file")
            return
        }
        val mediaFileMovieMetadataSource = MediaFileMovieMetadataSource(file)
        mediaFileMovieMetadataSource.configureNfoMovieBuilder(movieBuilder)
    }

    private fun configureFromInfoJson(infoJsonPath: File, movieBuilder: MovieBuilder) {

        if (!infoJsonPath.exists()) {
            LOGGER.warn("no youtube-dl metadatafile found for $infoJsonPath")
            return
        }
        val infoJsonMovieMetadataSource = InfoJsonMovieMetadataSource(infoJsonPath)
        infoJsonMovieMetadataSource.configureNfoMovieBuilder(movieBuilder)
        val origin = infoJsonMovieMetadataSource.downloadPage
        val onlineMovieMetadataSource = OnlineMovieMetadataSource(origin)
        onlineMovieMetadataSource.configureNfoMovieBuilder(movieBuilder)
    }

    private fun configureFromMetaJson(metaJsonPath: File, movieBuilder: MovieBuilder) {

        if (!metaJsonPath.exists()) {
            return
        }
        val metaJsonMovieMetadataSource = MetaJsonMovieMetadataSource(metaJsonPath)
        metaJsonMovieMetadataSource.configureNfoMovieBuilder(movieBuilder)
        val origin = metaJsonMovieMetadataSource.downloadPage
        val onlineMovieMetadataSource = OnlineMovieMetadataSource(origin)
        onlineMovieMetadataSource.configureNfoMovieBuilder(movieBuilder)
    }

    companion object {
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        private val LOGGER = LoggerFactory.getLogger(NfoWriterFileHandler::class.java)
        val ZONE_OFFSET = ZoneOffset.ofHours(2)
    }
}
@file:OptIn(ExperimentalCoroutinesApi::class)

package net.lumue.filewalkerd.scanner.moviescanner

import io.github.lumue.nfotools.Movie
import io.github.lumue.nfotools.Movie.MovieBuilder
import io.github.lumue.nfotools.NfoMovieSerializer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.lumue.filewalkerd.scanner.PathScanner.FileHandler
import net.lumue.filewalkerd.util.FileHelper.isVideoFileExtension
import net.lumue.filewalkerd.util.FileHelper.nfoMetadataFileExists
import net.lumue.filewalkerd.util.FileHelper.resolveInfoJsonPath
import net.lumue.filewalkerd.util.FileHelper.resolveMetaJsonPath
import net.lumue.filewalkerd.util.FileHelper.resolveNfoPath
import org.apache.commons.io.FilenameUtils.getBaseName
import org.apache.commons.io.FilenameUtils.getFullPath
import org.slf4j.LoggerFactory
import java.io.*
import javax.xml.bind.JAXBException

class VideoFileHandler(private val overwriteExistingNfo: Boolean) : FileHandler {

    private val movieSerializer: NfoMovieSerializer = NfoMovieSerializer()

    private val smartActorResolverMetadataUpdater: SmartActorResolverMetadataUpdater = SmartActorResolverMetadataUpdater()

    private val smartTagResolverMetadataUpdater: SmartTagResolverMetadataUpdater = SmartTagResolverMetadataUpdater()


    override fun handleFile(file: File) {
        try {
            val filename = file.toString()
            if (isVideoFileExtension(file)) {
                val nfoMetadataFileExists = nfoMetadataFileExists(filename)
                if (!overwriteExistingNfo && nfoMetadataFileExists) {
                    LOGGER.info("nfo file already exists for$filename")
                    return
                }
//                if (nfoMetadataFileModifiedAfter(filename, doNotTouchIfModifiedAfter)) {
//                    LOGGER.info("nfo $filename file was already modified since yesterday ")
//                    return
//                }
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
                        LOGGER.warn("could not load additional metadata for $filename. error was: {}",e.localizedMessage)
                    }
                }
                smartActorResolverMetadataUpdater.mergeInto(movieBuilder)
                smartTagResolverMetadataUpdater.mergeInto(movieBuilder)
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
        mediaFileMovieMetadataSource.mergeInto(movieBuilder)
    }

    private fun configureFromInfoJson(infoJsonPath: File, movieBuilder: MovieBuilder) {

        if (!infoJsonPath.exists()) {
            LOGGER.warn("no youtube-dl metadatafile found for $infoJsonPath")
            return
        }
        val infoJsonMovieMetadataSource = InfoJsonMovieMetadataSource(infoJsonPath)
        infoJsonMovieMetadataSource.mergeInto(movieBuilder)
        val origin = infoJsonMovieMetadataSource.downloadPage
        val onlineMovieMetadataSource = OnlineMovieMetadataSource(origin)
        onlineMovieMetadataSource.mergeInto(movieBuilder)
    }

    private fun configureFromMetaJson(metaJsonPath: File, movieBuilder: MovieBuilder) {

        if (!metaJsonPath.exists()) {
            return
        }
        val metaJsonMovieMetadataSource = MetaJsonMovieMetadataSource(metaJsonPath)
        metaJsonMovieMetadataSource.mergeInto(movieBuilder)
        val origin = metaJsonMovieMetadataSource.downloadPage
        val onlineMovieMetadataSource = OnlineMovieMetadataSource(origin)
        onlineMovieMetadataSource.mergeInto(movieBuilder)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(VideoFileHandler::class.java)
    }
}
package net.lumue.filewalkerd.scanner.moviescanner

import io.github.lumue.infojson.DownloadMetadata
import io.github.lumue.infojson.DownloadMetadataStreamParser
import io.github.lumue.nfotools.Movie.MovieBuilder
import net.lumue.filewalkerd.util.FileHelper.readCreationTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer

class InfoJsonMovieMetadataSource(private val file: File) : MetadataSource {
    val downloadPage: URL
        get() {
            val downloadMetadata = readDownloadMetadata()
            try {
                return URI.create(downloadMetadata.webpageUrl).toURL()
            } catch (e: MalformedURLException) {
                throw MetadataSourceAccessError("malformed source url", e)
            }
        }

    override fun mergeInto(movieBuilder: MovieBuilder): MovieBuilder {
        val downloadMetadata = readDownloadMetadata()

        val uploadDateAsString = downloadMetadata.uploadDate
        if (uploadDateAsString != null && uploadDateAsString.isNotEmpty()) {
            val uploadDate: LocalDateTime
            val year = uploadDateAsString.substring(0, 4).toInt()
            val month = uploadDateAsString.substring(4, 6).toInt()
            val day = uploadDateAsString.substring(6, 8).toInt()
            uploadDate = LocalDateTime.of(year, month, day, 0, 0, 0)
            movieBuilder.withYear(uploadDate.year.toString())
            movieBuilder.withAired(uploadDate)
        }
        try {
            val downloadDate = readCreationTime(file)
            movieBuilder.withDateAdded(downloadDate)
        } catch (ioException: IOException) {
            LOGGER.error("error getting creation time of $file", ioException)
        }

        val title = if (downloadMetadata.title != null) downloadMetadata.title else ""
        movieBuilder.withTitle(title)
        movieBuilder.withTag(downloadMetadata.extractor)
        movieBuilder.withVotes(downloadMetadata.likeCount.toString())
        val description = if (downloadMetadata.description != null) downloadMetadata.description else ""
        movieBuilder.withTagline(description)
        extractTagsFromInfojson(downloadMetadata).forEach(Consumer { `val`: String? -> movieBuilder.withTag(`val`) })
        return movieBuilder
    }

    private fun readDownloadMetadata(): DownloadMetadata {
        val downloadMetadata: DownloadMetadata
        try {
            val inputStream: InputStream = FileInputStream(file)
            val parser = DownloadMetadataStreamParser()
            downloadMetadata = parser.apply(inputStream)
            inputStream.close()
        } catch (ioException: IOException) {
            throw MetadataSourceAccessError("error accessing $file", ioException)
        }
        return downloadMetadata
    }

    private fun extractTagsFromInfojson(downloadMetadata: DownloadMetadata): Set<String> {
        val tagset: MutableSet<String> = HashSet()
        val candidates: MutableSet<String> = HashSet()
        Optional.ofNullable(downloadMetadata.tags).ifPresent { c: List<String>? ->
            candidates.addAll(
                c!!
            )
        }
        Optional.ofNullable(downloadMetadata.categories).ifPresent { c: List<String>? ->
            candidates.addAll(
                c!!
            )
        }
        Optional.ofNullable(downloadMetadata.uploader).ifPresent { e: String -> candidates.add(e) }
        candidates.stream()
            .filter { obj: String? -> Objects.nonNull(obj) }
            .forEach { e: String -> tagset.add(e) }
        return tagset
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(InfoJsonMovieMetadataSource::class.java)
    }
}

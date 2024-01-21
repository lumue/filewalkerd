package net.lumue.filewalkerd.scanner.moviescanner

import io.github.lumue.nfotools.Movie.MovieBuilder
import net.lumue.filewalkerd.util.FileHelper.readCreationTime
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.IOException
import java.util.*
import java.util.function.Consumer

class MediaFileMovieMetadataSource(private val file: File) : MetadataSource {
    override fun mergeInto(movieBuilder: MovieBuilder): MovieBuilder {
        try {
            if (!file.exists()) return movieBuilder

            val uploadDate = readCreationTime(file)
            movieBuilder.withYear(uploadDate.year.toString())
            movieBuilder.withAired(uploadDate)
            if (file.absolutePath.contains("adult")) {
                movieBuilder.withGenre("Porn")
            }
            extractTagsFromPath().forEach(Consumer { `val`: String? -> movieBuilder.withTag(`val`) })
            movieBuilder.withTitle(FilenameUtils.getBaseName(file.name))
            return movieBuilder
        } catch (ioException: IOException) {
            throw MetadataSourceAccessError("error getting metadata from $file", ioException)
        }
    }

    private fun extractTagsFromPath(): Collection<String> {
        val ret: MutableCollection<String> = ArrayList()
        val parent = file.toPath().parent
        val namesCount = parent.nameCount
        for (i in namesCount - 1 downTo 1) {
            val name = parent.getName(i).toString()
            if ("media" == name) break
            ret.add(name.lowercase(Locale.getDefault()))
        }
        return ret
    }
}

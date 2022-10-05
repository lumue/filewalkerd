package net.lumue.filewalkerd.util

import org.apache.commons.io.FilenameUtils
import org.slf4j.Logger
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

object FileHelper {
    private val MOVIE_EXTENSIONS: Set<String> = HashSet(Arrays.asList("flv", "mp4", "avi", "mkv", "m4v"))
    private val METADATA_EXTENSIONS: Set<String> = HashSet(Arrays.asList("nfo", "info.json", "meta.json"))

    val File.isVideoFile: Boolean
        get() {
            return isVideoFileExtension(this)
        }


    @JvmStatic
    fun isVideoFileExtension(file: File): Boolean {
        val filename = file.name
        return MOVIE_EXTENSIONS.contains(FilenameUtils.getExtension(filename).toLowerCase())
    }

    fun getInfoJsonFilename(filename: String?): String {
        val baseName = FilenameUtils.getBaseName(filename)
        return FilenameUtils.getFullPath(filename) + baseName + ".info.json"
    }

    @JvmStatic
    fun getNfoFilename(filename: String?): String {
        val baseName = FilenameUtils.getBaseName(filename)
        return FilenameUtils.getFullPath(filename) + baseName + ".nfo"
    }

    fun getMetaJsonFilename(filename: String?): String {
        val baseName = FilenameUtils.getBaseName(filename)
        return FilenameUtils.getFullPath(filename) + baseName + ".meta.json"
    }

    @JvmStatic
    @Throws(IOException::class)
    fun readCreationTime(file: File): LocalDateTime {
        val attr = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
        val lastModified = attr.creationTime().toInstant().epochSecond
        val offset = ZoneOffset
            .ofTotalSeconds(TimeZone.getDefault().getOffset(lastModified * 1000) / 1000)
        return LocalDateTime.ofEpochSecond(lastModified, 0, offset)
    }

    @JvmStatic
    fun nfoMetadataFileExists(filename: String?): Boolean {
        return Files.exists(Paths.get(getNfoFilename(filename)))
    }

    @JvmStatic
    fun resolveNfoPath(file: File): Path {
        val fileName = file.absolutePath
        val nfoFilename = getNfoFilename(fileName)
        return Paths.get(nfoFilename)
    }

    @JvmStatic
    fun resolveInfoJsonPath(file: File): Path {
        val fileName = file.absolutePath
        val infoJsonFilename = getInfoJsonFilename(fileName)
        return Paths.get(infoJsonFilename)
    }

    @JvmStatic
    fun resolveMetaJsonPath(file: File): Path {
        val fileName = file.absolutePath
        val infoJsonFilename = getMetaJsonFilename(fileName)
        return Paths.get(infoJsonFilename)
    }


}

fun File.renameToOrWarn(newFile: File, logger: Logger) {
    if (this.renameTo(newFile))
        return
    logger.warn("renaming operation $this to $newFile failed")
}



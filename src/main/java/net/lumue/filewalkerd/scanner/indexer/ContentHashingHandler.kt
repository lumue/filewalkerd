package net.lumue.filewalkerd.scanner.indexer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.lumue.filewalkerd.scanner.PathScanner.FileHandler
import org.slf4j.LoggerFactory
import java.io.File

@ExperimentalCoroutinesApi
class ContentHashingHandler : FileHandler {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ContentHashingHandler::class.java)
    }

    override fun handleFile(file: File) {
        try {

            LOGGER.info("hash created for $file")
        } catch (e: Throwable) {
            LOGGER.error("error processing file $file", e)
        }
    }




}




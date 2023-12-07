@file:OptIn(ExperimentalCoroutinesApi::class)

package net.lumue.filewalkerd.scanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.lumue.filewalkerd.scanner.indexer.ContentHashingHandler
import net.lumue.filewalkerd.scanner.moviescanner.NfoWriterFileHandler
import net.lumue.filewalkerd.scanner.moviescanner.RenameToNormalizedFileNameFileHandler
import net.lumue.filewalkerd.util.FileHelper.isVideoFile
import org.springframework.stereotype.Component

class Scanners {

    @ExperimentalCoroutinesApi
    @Component
    class ContentHashingScanner : PathScanner(
        fileFilter = {true},
        fileHandler = ContentHashingHandler()
    )

    @ExperimentalCoroutinesApi
    @Component
    class CollectAndWriteMovieMetadataTask : PathScanner(
        fileFilter = { f -> f.isVideoFile },
        fileHandler = NfoWriterFileHandler(true)
    )

    @Component
    @ExperimentalCoroutinesApi
    class RenameMovieTask : PathScanner(
        fileFilter = { f -> f.isVideoFile },
        fileHandler = RenameToNormalizedFileNameFileHandler()
    )
}

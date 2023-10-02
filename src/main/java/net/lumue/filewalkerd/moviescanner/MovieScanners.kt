@file:OptIn(ExperimentalCoroutinesApi::class)

package net.lumue.filewalkerd.moviescanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.lumue.filewalkerd.scanner.PathScanner
import net.lumue.filewalkerd.util.FileHelper.isVideoFile
import org.springframework.stereotype.Component

class MovieScanners {



    @ExperimentalCoroutinesApi
    @Component
    class CollectAndWriteMovieMetadataTask : PathScanner(
        fileFilter = { f -> f.isVideoFile },
        NfoWriterFileHandler(true)
    )

    @Component
    @ExperimentalCoroutinesApi
    class RenameMovieTask : PathScanner(
        fileFilter = { f -> f.isVideoFile },
        RenameToNormalizedFileNameFileHandler()
    )
}

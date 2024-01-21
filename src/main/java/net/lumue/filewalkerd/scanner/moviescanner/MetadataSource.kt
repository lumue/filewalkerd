package net.lumue.filewalkerd.scanner.moviescanner

import io.github.lumue.nfotools.Movie.MovieBuilder

interface MetadataSource {
    fun mergeInto(movieBuilder: MovieBuilder): MovieBuilder
}


fun MovieBuilder.merge(metadataSource: MetadataSource) {
    metadataSource.mergeInto(this)
}

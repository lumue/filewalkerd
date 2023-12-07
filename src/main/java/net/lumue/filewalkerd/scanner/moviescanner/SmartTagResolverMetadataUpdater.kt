package net.lumue.filewalkerd.scanner.moviescanner

import io.github.lumue.nfotools.Movie
import java.util.*

class SmartTagResolverMetadataUpdater(
    private val knownTagSet: Set<String> = loadTags().toNormalizedForm()
) : NfoMovieMetadataUpdater {
    override fun configureNfoMovieBuilder(movieBuilder: Movie.MovieBuilder?): Movie.MovieBuilder {
        val movie = movieBuilder!!.build()
        val newTags = mutableSetOf<String>()
        val movieTitle = if(movie.title!=null)movie.title.toNormalizedForm() else ""
        val movieOutline =if (movie.outline!=null)movie.outline.toNormalizedForm() else ""
        val moviePlot =if (movie.plot!=null)movie.plot.toNormalizedForm()else ""
        val movieOriginalTitle =if(movie.originaltitle!=null)movie.originaltitle.toNormalizedForm()else ""
        newTags.addAll(knownTagSet
            .filter {  knownTag ->
                        !movie.tags().contains(knownTag) && !newTags.contains(knownTag) &&
                            (movieTitle.contains(knownTag) ||
                             movieOutline.contains(knownTag) ||
                             moviePlot.contains(knownTag) ||
                             movieOriginalTitle.contains(knownTag))
            }
        )
        newTags.forEach { tag -> movieBuilder.withTag(tag) }
        return movieBuilder
    }


}

private fun Set<String>.toNormalizedForm(): Set<String> {
    return this.filter { s-> s!=null }.map { s -> s.toNormalizedForm() }.toHashSet()
}

private fun String.toNormalizedForm():String{
    return this.lowercase(Locale.getDefault())
        .replace(" ", "")
        .replace("-", "")
        .replace("_", "")

}

fun loadTags(): Set<String> {
    return SmartTagResolverMetadataUpdater::class.java.getResourceAsStream("tag.csv")!!.bufferedReader()
        .readLines().filter { l->l!=null }.toHashSet()

}

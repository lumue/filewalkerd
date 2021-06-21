package net.lumue.filewalkerd.metadata.nfo

import io.github.lumue.nfotools.Movie

class SmartActorResolverMetadataUpdater(
    private val knownActorSet : Set<String> = loadActors()
)
    : NfoMovieMetadataUpdater {
    override fun configureNfoMovieBuilder(movieBuilder: Movie.MovieBuilder?): Movie.MovieBuilder {
        val movie = movieBuilder!!.build()
        val possibleActors = mutableSetOf<String>()
        val movieTags=movie.tags().toLowerCase()
        val movieTitle =movie.title.toLowerCase()
        possibleActors.addAll(knownActorSet.filter { actorName ->
            movieTitle.contains(actorName.toLowerCase()) || movieTags.contains(actorName.toLowerCase())
        })
        possibleActors.forEach { actor ->   movieBuilder.addActor(Movie.Actor(actor, ""))}
        return  movieBuilder
    }


}

private fun Set<String>.toLowerCase(): Set<String> {
   return this.map { s-> s. toLowerCase()}.toHashSet()
}

fun loadActors(): Set<String> {
    return SmartActorResolverMetadataUpdater::class.java.getResourceAsStream("actor.csv")!!.bufferedReader()
        .readLines().toHashSet()

}

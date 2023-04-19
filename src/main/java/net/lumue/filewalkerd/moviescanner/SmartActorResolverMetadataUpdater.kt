package net.lumue.filewalkerd.moviescanner

import io.github.lumue.nfotools.Movie

class SmartActorResolverMetadataUpdater(
    private val knownActorSet: Set<String> = loadActors()
) : NfoMovieMetadataUpdater {
    override fun configureNfoMovieBuilder(movieBuilder: Movie.MovieBuilder?): Movie.MovieBuilder {
        val movie = movieBuilder!!.build()
        val possibleActors = mutableSetOf<String>()
        val movieTags = movie.tags().toNormalizedForm()
        val movieTitle = movie.title.toNormalizedForm()
        possibleActors.addAll(knownActorSet
            .filter { actorName ->movieTitle.contains(actorName.toNormalizedForm()) || movieTags.contains(actorName.toNormalizedForm())}
        )
        possibleActors.forEach { actor -> movieBuilder.addActor(Movie.Actor(actor, "")) }
        return movieBuilder
    }


}

private fun Set<String>.toNormalizedForm(): Set<String> {
    return this.map { s -> s.toNormalizedForm() }.toHashSet()
}

private fun String.toNormalizedForm():String{
    return this.toLowerCase()
        .replace(" ","")
        .replace("-","")
        .replace("_","")

}

fun loadActors(): Set<String> {
    return SmartActorResolverMetadataUpdater::class.java.getResourceAsStream("actor.csv")!!.bufferedReader()
        .readLines().toHashSet()

}

package net.lumue.filewalkerd.moviescanner

import io.github.lumue.nfotools.Movie
import io.github.lumue.nfotools.Movie.MovieBuilder
import net.lumue.filewalkerd.mdresolverclient.api.ResolveControllerApi
import net.lumue.filewalkerd.mdresolverclient.invoker.ApiClient
import net.lumue.filewalkerd.mdresolverclient.invoker.ApiException
import net.lumue.filewalkerd.mdresolverclient.invoker.Configuration
import net.lumue.filewalkerd.mdresolverclient.model.Actor
import net.lumue.filewalkerd.mdresolverclient.model.MovieMetadata
import net.lumue.filewalkerd.mdresolverclient.model.Tag
import java.net.MalformedURLException
import java.net.URL
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.function.Consumer

class OnlineMovieMetadataSource(private val url: URL) : NfoMovieMetadataUpdater {
    val apiClient: ApiClient

    init {
        apiClient = Configuration.getDefaultApiClient()
            .setBasePath("/")
            .setReadTimeout(Duration.of(300, ChronoUnit.SECONDS))
    }

    override fun configureNfoMovieBuilder(movieBuilder: MovieBuilder): MovieBuilder {
        val metadata = metadataFromURL
        if (metadata.actors != null) {
            metadata.actors!!
                .filter { actor->!actor.name.equals("Empfehlen") }
                .forEach(Consumer { actor: Actor ->
                    movieBuilder.addActor(Movie.Actor(actor.name, ""))
                    movieBuilder.withTag(actor.name)
                })
        }
        if (metadata.tags != null) {
            metadata.tags!!
                .forEach(Consumer { t: Tag -> movieBuilder.withTag(t.name) })
        }
        movieBuilder.withSet(metadata.uploader)
        movieBuilder.withTag(metadata.uploader)
        movieBuilder.withPlot(metadata.description)
        movieBuilder.withOutline(metadata.description)
        movieBuilder.withTitle(metadata.title)
        movieBuilder.withSorttitle(metadata.title)
        if (metadata.duration != null) movieBuilder.withRuntime(
            Duration.parse(metadata.duration).toMinutes().toString() + ""
        )
        movieBuilder.withTagline(metadata.description)
        return movieBuilder
    }

    private val metadataFromURL: MovieMetadata
        get() {
            val apiInstance = ResolveControllerApi(apiClient)
            val httpURL: URL
            var externalForm =""
            return try {
                httpURL =  URL("http", url.host,80,url.file)
                externalForm= httpURL.toExternalForm()
                apiInstance.resolveMetadataforUrlInPath(externalForm)
            } catch (e: MalformedURLException) {
                throw RuntimeException("error transforming url", e)
            } catch (e: ApiException) {
                val msg =
                    "Exception when calling metadata-resolver service with \n\turl: $externalForm \n\tStatus code: ${e.code}\n\tReason: ${e.responseBody}\n\tResponse headers: ${e.responseHeaders}"
                throw MetadataSourceAccessError(msg, e)
            }
        }
}
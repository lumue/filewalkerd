package net.lumue.filewalkerd.moviescanner;

import io.github.lumue.nfotools.Movie;
import net.lumue.filewalkerd.mdresolverclient.api.ResolveControllerApi;
import net.lumue.filewalkerd.mdresolverclient.invoker.ApiClient;
import net.lumue.filewalkerd.mdresolverclient.invoker.ApiException;
import net.lumue.filewalkerd.mdresolverclient.invoker.Configuration;
import net.lumue.filewalkerd.mdresolverclient.model.MovieMetadata;

import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class OnlineMovieMetadataSource implements NfoMovieMetadataUpdater{

  private final URL url;


  public OnlineMovieMetadataSource(final URL url) {
    this.url = url;
  }

  @Override
  public Movie.MovieBuilder configureNfoMovieBuilder(final Movie.MovieBuilder movieBuilder) {
    MovieMetadata metadata=getMetadataFromURL();
    if (metadata.getActors() != null) {
      metadata.getActors().forEach(actor -> {
        movieBuilder.addActor(new Movie.Actor(actor.getName(), ""));
        movieBuilder.withTag(actor.getName());
      });
    }
    if (metadata.getTags() != null) {
      metadata.getTags().forEach(t ->movieBuilder.withTag(t.getName()));
    }
    movieBuilder.withSet(metadata.getUploader());
    movieBuilder.withTag(metadata.getUploader());
    movieBuilder.withPlot(metadata.getDescription());
    movieBuilder.withOutline(metadata.getDescription());
    movieBuilder.withTitle(metadata.getTitle());
    movieBuilder.withSorttitle(metadata.getTitle());
    if (metadata.getDuration() != null)
      movieBuilder.withRuntime(Duration.parse(metadata.getDuration()).toMinutes() + "");
    movieBuilder.withTagline(metadata.getDescription());
    return movieBuilder;
  }

  private MovieMetadata getMetadataFromURL() {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    //defaultClient.setBasePath("http://vm-services-media:8090");
    defaultClient.setHost("172.23.2.5");
    defaultClient.setReadTimeout(Duration.of(15, ChronoUnit.SECONDS));


    ResolveControllerApi apiInstance = new ResolveControllerApi(defaultClient);
    String urlAsString = this.url.toExternalForm();
    try {
      return apiInstance.resolveMetadataforUrlInPath(urlAsString);
    } catch (ApiException e) {
      String msg = "Exception when calling ResolveControllerApi#resolveMetadataforUrlInBody" +
              "Status code: " + e.getCode() +
              "Reason: " + e.getResponseBody() +
              "Response headers: " + e.getResponseHeaders();
      throw new MetadataSourceAccessError(msg,e);
    }
  }
}

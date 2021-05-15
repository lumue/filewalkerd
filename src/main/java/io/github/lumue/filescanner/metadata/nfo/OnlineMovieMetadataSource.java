package io.github.lumue.filescanner.metadata.nfo;

import io.github.lumue.mdresolver.client.api.ResolveControllerApi;
import io.github.lumue.mdresolver.client.invoker.ApiClient;
import io.github.lumue.mdresolver.client.invoker.ApiException;
import io.github.lumue.mdresolver.client.invoker.Configuration;
import io.github.lumue.mdresolver.client.model.MovieMetadata;
import io.github.lumue.nfotools.Movie;

import java.net.URL;
import java.util.Optional;

public class OnlineMovieMetadataSource implements NfoMovieMetadataUpdater{

  private final URL url;


  public OnlineMovieMetadataSource(final URL url) {
    this.url = url;
  }

  @Override
  public Movie.MovieBuilder configureNfoMovieBuilder(final Movie.MovieBuilder movieBuilder) {
    MovieMetadata metadata=getMetadataFromURL(url);
    metadata.getActors().forEach(actor -> {
      movieBuilder.addActor(new Movie.Actor(actor.getName(), ""));
      movieBuilder.withTag(actor.getName());
    });
    metadata.getTags().forEach(t ->movieBuilder.withTag(t.getName()));
    movieBuilder.withSet(metadata.getUploader());
    movieBuilder.withTag(metadata.getUploader());
    movieBuilder.withPlot(metadata.getDescription());
    movieBuilder.withOutline(metadata.getDescription());
    movieBuilder.withTitle(metadata.getTitle());
    movieBuilder.withSorttitle(metadata.getTitle());
    movieBuilder.withTagline(metadata.getDescription());
    //movieBuilder.withRuntime(metadata.getDuration());
    return movieBuilder;
  }

  private MovieMetadata getMetadataFromURL(URL url) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    //defaultClient.setBasePath("http://vm-services-media:8090");
    defaultClient.setHost("172.23.2.5");

    ResolveControllerApi apiInstance = new ResolveControllerApi(defaultClient);
    String urlAsString = this.url.toExternalForm();
    try {
      return apiInstance.resolveMetadataforUrlInPath(urlAsString);
    } catch (ApiException e) {
      StringBuilder msg=new StringBuilder("Exception when calling ResolveControllerApi#resolveMetadataforUrlInBody")
              .append("Status code: " + e.getCode())
              .append("Reason: " + e.getResponseBody())
              .append("Response headers: " + e.getResponseHeaders());
      throw new MetadataSourceAccessError(msg.toString(),e);
    }
  }
}

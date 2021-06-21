package net.lumue.filewalkerd.metadata.nfo;

import io.github.lumue.nfotools.Movie;

public interface NfoMovieMetadataUpdater {
  Movie.MovieBuilder configureNfoMovieBuilder(Movie.MovieBuilder movieBuilder);
}

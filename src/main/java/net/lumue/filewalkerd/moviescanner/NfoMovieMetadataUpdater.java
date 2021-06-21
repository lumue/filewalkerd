package net.lumue.filewalkerd.moviescanner;

import io.github.lumue.nfotools.Movie;

public interface NfoMovieMetadataUpdater {
  Movie.MovieBuilder configureNfoMovieBuilder(Movie.MovieBuilder movieBuilder);
}

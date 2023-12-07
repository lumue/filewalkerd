package net.lumue.filewalkerd.scanner.moviescanner;

import io.github.lumue.nfotools.Movie;

public interface NfoMovieMetadataUpdater {
    Movie.MovieBuilder configureNfoMovieBuilder(Movie.MovieBuilder movieBuilder);
}

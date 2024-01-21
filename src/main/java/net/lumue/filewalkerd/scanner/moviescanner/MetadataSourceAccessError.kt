package net.lumue.filewalkerd.scanner.moviescanner;

public class MetadataSourceAccessError extends RuntimeException {
    public MetadataSourceAccessError(final String s, final Exception ioException) {
        super(s, ioException);
    }
}

package io.github.lumue.filescanner.metadata.nfo;

import java.io.IOException;

public class MetadataSourceAccessError extends RuntimeException {
  public MetadataSourceAccessError(final String s, final Exception ioException) {
    super(s,ioException);
  }
}

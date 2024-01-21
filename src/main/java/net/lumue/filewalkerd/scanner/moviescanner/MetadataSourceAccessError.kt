package net.lumue.filewalkerd.scanner.moviescanner

class MetadataSourceAccessError(s: String?, ioException: Exception?) : RuntimeException(s, ioException)

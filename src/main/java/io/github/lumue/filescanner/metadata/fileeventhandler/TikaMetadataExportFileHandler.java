package io.github.lumue.filescanner.metadata.fileeventhandler;

import io.github.lumue.filescanner.discover.FileHandler;
import io.github.lumue.filescanner.metadata.content.TikaMetadataAccessor;
import io.github.lumue.filescanner.metadata.nfo.InfoJsonMovieMetadataSource;
import io.github.lumue.filescanner.metadata.nfo.MediaFileMovieMetadataSource;
import io.github.lumue.filescanner.metadata.nfo.MetaJsonMovieMetadataSource;
import io.github.lumue.filescanner.metadata.nfo.MetadataSourceAccessError;
import io.github.lumue.filescanner.util.FileNamingUtils;
import io.github.lumue.nfotools.Movie;
import io.github.lumue.nfotools.Movie.MovieBuilder;
import io.github.lumue.nfotools.NfoMovieSerializer;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TikaMetadataExportFileHandler implements FileHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(TikaMetadataExportFileHandler.class);

  private final boolean overwriteExistingNfo;

  private final NfoMovieSerializer movieSerializer;



  public TikaMetadataExportFileHandler(final boolean overwriteExistingNfo) throws JAXBException {
    this.overwriteExistingNfo = overwriteExistingNfo;
    this.movieSerializer = new NfoMovieSerializer();
  }

  @Override
  public void handleFile(final File file) {
    try {
      String filename = file.toString();
      String extension = FilenameUtils.getExtension(filename);



      if (FileNamingUtils.isVideoFileExtension(file)) {

        TikaMetadataAccessor tikaMetadataAccessor = new TikaMetadataAccessor(file.toPath());
        StringBuilder tikaMetadata=new StringBuilder("[");
        tikaMetadataAccessor.getPropertyKeys().forEach(key->{
          tikaMetadata.append("{\""+key+"\":\""+tikaMetadataAccessor.getProperty(key)+"\"},\n");
        });
        tikaMetadata.deleteCharAt(tikaMetadata.lastIndexOf(","));
        tikaMetadata.append("]");
        LOGGER.info("tikaMetadata for "+filename+":\n"+tikaMetadata);


      }
    } catch (Exception e) {
      LOGGER.error("error processing file " + file, e);
    }
  }

  private Path resolveTikaPath(final File file) {
    String fileName = file.getAbsolutePath();
    String infoJsonFilename = FileNamingUtils.getInfoJsonFilename(fileName);
    return Paths.get(infoJsonFilename);
  }

  private void writeTikaFile(final String data,final String mediaFileName)
      throws JAXBException, IOException {
    String nfoFilename = FilenameUtils.getFullPath(mediaFileName) +FilenameUtils.getBaseName(mediaFileName) + ".tika.json";
    Files.write(new File(nfoFilename).toPath(),data.getBytes(StandardCharsets.UTF_8));
    LOGGER.debug(nfoFilename + " created");
  }





}

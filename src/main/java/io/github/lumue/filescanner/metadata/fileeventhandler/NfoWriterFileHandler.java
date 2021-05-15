package io.github.lumue.filescanner.metadata.fileeventhandler;

import io.github.lumue.filescanner.discover.FileHandler;
import io.github.lumue.filescanner.metadata.content.TikaMetadataAccessor;
import io.github.lumue.filescanner.metadata.nfo.*;
import io.github.lumue.filescanner.util.FileNamingUtils;
import io.github.lumue.nfotools.Movie;
import io.github.lumue.nfotools.Movie.MovieBuilder;
import io.github.lumue.nfotools.NfoMovieSerializer;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NfoWriterFileHandler implements FileHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(NfoWriterFileHandler.class);

  private final boolean overwriteExistingNfo;

  private final NfoMovieSerializer movieSerializer;



  public NfoWriterFileHandler(final boolean overwriteExistingNfo) throws JAXBException {
    this.overwriteExistingNfo = overwriteExistingNfo;
    this.movieSerializer = new NfoMovieSerializer();
  }

  @Override
  public void handleFile(final File file) {
    try {
      String filename = file.toString();
      String extension = FilenameUtils.getExtension(filename);



      if (FileNamingUtils.isVideoFileExtension(file)) {


        boolean nfoMetadataFileExists = nfoMetadataFileExists(filename);
        if (!overwriteExistingNfo && nfoMetadataFileExists) {
          LOGGER.warn("nfo file already exists for" + filename);
          return;
        }


        MovieBuilder movieBuilder;
        if(nfoMetadataFileExists)
        {
          File nfoLocation = resolveNfoPath(file).toFile();
          try {
            movieBuilder = createBuilderFromNfo(nfoLocation);
          }
          catch(MetadataSourceAccessError e){
            LOGGER.error("could not load movie from nfo file "+nfoLocation.getAbsolutePath()+":",e);
            return;
          }
        }
        else
          movieBuilder=Movie.builder();

        configureFromMediafile(file,movieBuilder);

        File infoJsonLocation = resolveInfoJsonPath(file).toFile();
        File metaJsonLocation=resolveMetaJsonPath(file).toFile();

        if(!infoJsonLocation.exists() && !metaJsonLocation.exists()){
          LOGGER.warn("no download metadata found for "+file);
          if(!nfoMetadataFileExists)
            writeNfoFile(movieBuilder.build(),filename);
          return;
        }else {
          configureFromInfoJson(infoJsonLocation, movieBuilder);
          configureFromMetaJson(metaJsonLocation,movieBuilder);
        }
        writeNfoFile(movieBuilder.build(), filename);
      }
    } catch (Exception e) {
      LOGGER.error("error processing file " + file, e);
    }
  }

  private Path resolveNfoPath(final File file) {
    String fileName = file.getAbsolutePath();
    String nfoFilename = FileNamingUtils.getNfoFilename(fileName);
    return Paths.get(nfoFilename);
  }

  private Path resolveInfoJsonPath(final File file) {
    String fileName = file.getAbsolutePath();
    String infoJsonFilename = FileNamingUtils.getInfoJsonFilename(fileName);
    return Paths.get(infoJsonFilename);
  }

  private Path resolveMetaJsonPath(final File file) {
    String fileName = file.getAbsolutePath();
    String infoJsonFilename = FileNamingUtils.getMetaJsonFilename(fileName);
    return Paths.get(infoJsonFilename);
  }

  private void writeNfoFile( final Movie movie, final String mediaFileName)
      throws JAXBException, IOException {
    String nfoFilename = FilenameUtils.getFullPath(mediaFileName) +FilenameUtils.getBaseName(mediaFileName) + ".nfo";
    OutputStream outputStream = new FileOutputStream(nfoFilename);
    movieSerializer.serialize(movie, outputStream);
    outputStream.close();
    LOGGER.debug(nfoFilename + " created");
  }

  private MovieBuilder createBuilderFromNfo(final File file) {

    try {
      return movieSerializer.deserialize(new FileInputStream(file)).copyBuilder();
    } catch (Exception e) {
      throw new MetadataSourceAccessError("could not load movie from nfo file "+file.getAbsolutePath(),e);
    }

  }

  private MovieBuilder configureFromMediafile(final File file, final MovieBuilder movieBuilder)
      throws IOException {

    if(!file.exists()) {
      LOGGER.warn("can no longer access " + file);
      return movieBuilder;
    }

    final MediaFileMovieMetadataSource mediaFileMovieMetadataSource=new MediaFileMovieMetadataSource(file);
    return mediaFileMovieMetadataSource.configureNfoMovieBuilder(movieBuilder);
  }

  private MovieBuilder configureFromInfoJson(File infoJsonPath, MovieBuilder movieBuilder) {

    if(!infoJsonPath.exists()) {
      LOGGER.warn("no youtube-dl metadatafile found for " + infoJsonPath);
      return movieBuilder;
    }

    final InfoJsonMovieMetadataSource infoJsonMovieMetadataSource = new InfoJsonMovieMetadataSource(infoJsonPath);
    movieBuilder = infoJsonMovieMetadataSource.configureNfoMovieBuilder(movieBuilder);
    URL origin=infoJsonMovieMetadataSource.getDownloadPage();
    final OnlineMovieMetadataSource onlineMovieMetadataSource=new OnlineMovieMetadataSource(origin);
    return onlineMovieMetadataSource.configureNfoMovieBuilder(movieBuilder);
  }

  private MovieBuilder configureFromMetaJson(File metaJsonPath, MovieBuilder movieBuilder) {

    if(!metaJsonPath.exists()) {
       return movieBuilder;
    }

    final MetaJsonMovieMetadataSource metaJsonMovieMetadataSource = new MetaJsonMovieMetadataSource(metaJsonPath);
    movieBuilder= metaJsonMovieMetadataSource.configureNfoMovieBuilder( movieBuilder);
    URL origin=metaJsonMovieMetadataSource.getDownloadPage();
    final OnlineMovieMetadataSource onlineMovieMetadataSource=new OnlineMovieMetadataSource(origin);
    return onlineMovieMetadataSource.configureNfoMovieBuilder(movieBuilder);

  }

  private boolean nfoMetadataFileExists(final String filename) {
    return Files.exists(Paths.get(FileNamingUtils.getNfoFilename(filename)));
  }






}

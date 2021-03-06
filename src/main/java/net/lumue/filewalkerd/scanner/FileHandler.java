package net.lumue.filewalkerd.scanner;

import java.io.File;
import java.nio.file.Path;

public interface FileHandler {
  void handleFile(File file);
  default void handleFile(Path path){
    handleFile(path.toFile());
  }
}

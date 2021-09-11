package net.lumue.filewalkerd.util;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class FileNamingUtils {


private final static Set<String> MOVIE_EXTENSIONS = new HashSet<>(Arrays.asList("flv", "mp4", "avi","mkv","m4v"));
private final static Set<String> METADATA_EXTENSIONS = new HashSet<>(Arrays.asList("nfo", "info.json","meta.json"));

public static boolean isVideoFileExtension(File file) {
	final String filename = file.getName();
	return MOVIE_EXTENSIONS.contains(FilenameUtils.getExtension(filename).toLowerCase());
}

public static String getInfoJsonFilename(String filename) {
	String baseName = FilenameUtils.getBaseName(filename);
	return FilenameUtils.getFullPath(filename) + baseName + ".info.json";
}

public static String getNfoFilename(String filename) {
	String baseName = FilenameUtils.getBaseName(filename);
	return FilenameUtils.getFullPath(filename) + baseName + ".nfo";
}


public static String getMetaJsonFilename(String filename) {
	String baseName = FilenameUtils.getBaseName(filename);
	return FilenameUtils.getFullPath(filename) + baseName + ".meta.json";
}
}

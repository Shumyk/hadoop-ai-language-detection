package org.bigdatainc.util;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.bigdatainc.model.LanguageProfile;
import org.bigdatainc.model.LanguageProfiles;
import org.bigdatainc.model.value.Language;

public final class FileUtil {
  private FileUtil() {}

  public static final String LANG_DIRNAME = "lang/";
  public static final String HADOOP_RESULT_FILE = "part";
  public static final String HADOOP_OUTPUTS_DIRNAME = "hadoop-outputs";
  public static final String DETECTION_RESULTS_FILENAME_PATTERN = "detection-results-%s.txt";

  public static LanguageProfiles loadLanguageProfiles(final Configuration configuration) throws IOException {
    final var languageProfilesDirpath = HadoopPaths.resolveSibling(configuration, LANG_DIRNAME);
    final Set<LanguageProfile> profiles = new HashSet<>();

    try (FileSystem fileSystem = FileSystem.get(configuration)) {
      for (final var languageProfileFileStatus : fileSystem.listStatus(languageProfilesDirpath)) {
        final var languageProfileFilepath = languageProfileFileStatus.getPath();
        final Language language = Language.of(languageProfileFilepath.getName());

        try (FSDataInputStream is = fileSystem.open(languageProfileFilepath)) {
          profiles.add(LanguageProfile.parse(language, is.readAllBytes()));
        }
      }
    }
    return new LanguageProfiles(profiles);
  }

  public static String hadoopOutputsDirPath(final String outputPath) {
    return Paths.get(outputPath, HADOOP_OUTPUTS_DIRNAME).toString();
  }

  public static void copyLanguageTrainingResults(final String inputPath,
                                                 final String hadoopOutputFolderPath) throws IOException {
    final String language = resolveFilename(inputPath);
    doCopy(hadoopOutputFolderPath, LANG_DIRNAME + language);
  }

  public static void copyLanguageDetectionResults(final String hadoopOutputFolderPath) throws IOException {
    doCopy(hadoopOutputFolderPath, DETECTION_RESULTS_FILENAME_PATTERN.formatted(Instant.now()));
  }


  private static String resolveFilename(final String inputPath) {
    final String filenameWithExtension = inputPath.substring(inputPath.lastIndexOf("/") + 1);
    return filenameWithExtension.split("\\.")[0];
  }

  private static void doCopy(final String hadoopOutputFolderPath,
                             final String destination) throws IOException {
    final File hadoopOutputFolder = new File(hadoopOutputFolderPath);
    final File[] filesInOutputsDir = listFiles(hadoopOutputFolder);

    final Path destinationPath = hadoopOutputFolder.toPath().getParent().resolve(destination);
    Files.createDirectories(destinationPath.getParent());
    Files.copy(findComputationResultsPath(filesInOutputsDir), destinationPath, StandardCopyOption.REPLACE_EXISTING);

    removeDirectory(hadoopOutputFolder, filesInOutputsDir);
  }


  private static File[] listFiles(final File dir) {
    return requireNonNull(dir.listFiles(), "Files in directory should not be null");
  }

  private static Path findComputationResultsPath(final File[] filesInOutputsDir) {
    return Arrays.stream(filesInOutputsDir)
        .filter(file -> file.getName().startsWith(HADOOP_RESULT_FILE))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not find results file"))
        .toPath();
  }

  private static void removeDirectory(final File dir,
                                      final File[] dirFiles) throws IOException {
    for (final File toDelete : dirFiles) {
      Files.delete(toDelete.toPath());
    }
    Files.delete(dir.toPath());
  }
}

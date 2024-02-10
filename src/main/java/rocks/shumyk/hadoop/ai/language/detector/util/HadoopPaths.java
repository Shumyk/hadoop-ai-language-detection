package rocks.shumyk.hadoop.ai.language.detector.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public final class HadoopPaths {
  private HadoopPaths() {}

  public static final String OUTPUT_DIR_CONF = "mapred.output.dir";

  public static Path resolveSibling(final Configuration configuration,
                                    final String siblingFilename) {
    final Path hadoopOutputDirpath = new Path(configuration.get(OUTPUT_DIR_CONF));
    return new Path(hadoopOutputDirpath.getParent(), siblingFilename);
  }
}

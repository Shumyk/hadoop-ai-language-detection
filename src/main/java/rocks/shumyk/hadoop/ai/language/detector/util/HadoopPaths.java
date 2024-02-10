package rocks.shumyk.hadoop.ai.language.detector.util;

import static rocks.shumyk.hadoop.ai.language.detector.util.Const.CONFIG_OUTPUT_DIR_CONF;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public final class HadoopPaths {
  private HadoopPaths() {}

  public static Path resolveSibling(final Configuration configuration,
                                    final String siblingFilename) {
    final Path hadoopOutputDirpath = new Path(configuration.get(CONFIG_OUTPUT_DIR_CONF));
    return new Path(hadoopOutputDirpath.getParent(), siblingFilename);
  }
}

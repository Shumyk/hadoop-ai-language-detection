package rocks.shumyk.hadoop.ai.language.detector;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import rocks.shumyk.hadoop.ai.language.detector.detection.LanguageDetectionMapper;
import rocks.shumyk.hadoop.ai.language.detector.detection.LanguageDetectionReducer;
import rocks.shumyk.hadoop.ai.language.detector.training.LanguageTrainingMapper;
import rocks.shumyk.hadoop.ai.language.detector.training.LanguageTraningReducer;
import rocks.shumyk.hadoop.ai.language.detector.util.FileUtil;

public class LanguageDetectionHadoop {
  public static final String MODE_TRAIN = "train";
  public static final String MODE_DETECT = "detect";

  public static void main(String[] args) throws Exception {
    final String inputPath = args[0];
    final String outputPath = FileUtil.hadoopOutputsDirPath(args[1]);
    final String mode = args.length == 3 ? args[2] : MODE_DETECT;

    switch (mode) {
      case MODE_TRAIN -> trainNewLanguage(inputPath, outputPath);
      case MODE_DETECT -> detectLanguage(inputPath, outputPath);
      default -> throw new IllegalArgumentException("Unknown mode [%s]".formatted(mode));
    }
  }

  private static void trainNewLanguage(final String inputPath,
                                       final String outputPath) throws IOException, InterruptedException, ClassNotFoundException {
    HadoopJobRunner.builder("Language Training")
        .config("mapreduce.output.textoutputformat.separator", ":")
        .mapper(LanguageTrainingMapper.class)
        .reducer(LanguageTraningReducer.class)
        .outputKey(Text.class)
        .outputValue(IntWritable.class)
        .inputPath(inputPath)
        .outputPath(outputPath)
        .cleanup(() -> FileUtil.copyLanguageTrainingResults(inputPath, outputPath))
        .build()
        .run();
  }

  private static void detectLanguage(final String inputPath,
                                     final String outputPath) throws IOException, InterruptedException, ClassNotFoundException {
    HadoopJobRunner.builder("Language Detection")
        .mapper(LanguageDetectionMapper.class)
        .reducer(LanguageDetectionReducer.class)
        .outputKey(Text.class)
        .outputValue(Text.class)
        .inputPath(inputPath)
        .outputPath(outputPath)
        .cleanup(() -> FileUtil.copyLanguageDetectionResults(outputPath))
        .build()
        .run();
  }
}
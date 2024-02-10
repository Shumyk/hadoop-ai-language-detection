package rocks.shumyk.hadoop.ai.language.detector;

import static rocks.shumyk.hadoop.ai.language.detector.util.Const.CONFIG_OUTPUT_FORMAT_SEPARATOR;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import rocks.shumyk.hadoop.ai.language.detector.detection.LanguageDetectionMapper;
import rocks.shumyk.hadoop.ai.language.detector.detection.LanguageDetectionReducer;
import rocks.shumyk.hadoop.ai.language.detector.detection.writable.DetectionStats;
import rocks.shumyk.hadoop.ai.language.detector.model.enums.RunMode;
import rocks.shumyk.hadoop.ai.language.detector.training.LanguageTrainingMapper;
import rocks.shumyk.hadoop.ai.language.detector.training.LanguageTraningReducer;
import rocks.shumyk.hadoop.ai.language.detector.util.FileUtil;

public class LanguageDetectionHadoop {

  public static void main(final String[] args) throws Exception {
    final RunMode runMode = RunMode.resolve(args);
    final String inputPath = args[0];
    final String outputPath = FileUtil.hadoopOutputsDirPath(args[1]);

    switch (runMode) {
      case TRAIN -> trainNewLanguage(inputPath, outputPath);
      case DETECT -> detectLanguage(inputPath, outputPath);
    }
  }

  private static void trainNewLanguage(final String inputPath,
                                       final String outputPath) throws IOException, InterruptedException, ClassNotFoundException {
    HadoopJobRunner.builder("Language Training")
        .config(CONFIG_OUTPUT_FORMAT_SEPARATOR, ":")
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
        .mapOutputKey(Text.class)
        .mapOutputValue(Text.class)
        .outputKey(Text.class)
        .outputValue(DetectionStats.class)
        .inputPath(inputPath)
        .outputPath(outputPath)
        .cleanup(() -> FileUtil.copyLanguageDetectionResults(outputPath))
        .build()
        .run();
  }
}
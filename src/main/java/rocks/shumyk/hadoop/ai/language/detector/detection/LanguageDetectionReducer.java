package rocks.shumyk.hadoop.ai.language.detector.detection;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import rocks.shumyk.hadoop.ai.language.detector.detection.writable.DetectionStats;

public class LanguageDetectionReducer extends Reducer<Text, Text, Text, DetectionStats> {
  private final Map<Text, DetectionStats> langToStats = new ConcurrentHashMap<>();

  @Override
  public void reduce(final Text language,
                     final Iterable<Text> probabilityToInputLines,
                     final Context context) throws IOException, InterruptedException {
    final DetectionStats stats = langToStats.computeIfAbsent(language, x -> new DetectionStats());
    probabilityToInputLines.forEach(stats::add);

    context.write(language, stats);
  }
}

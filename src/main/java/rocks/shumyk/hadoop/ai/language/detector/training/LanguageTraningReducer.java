package rocks.shumyk.hadoop.ai.language.detector.training;

import java.io.IOException;
import java.util.stream.StreamSupport;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class LanguageTraningReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
  public static final int OCCURRENCES_THRESHOLD = 7;

  @Override
  protected void reduce(final Text bigram,
                        final Iterable<IntWritable> occurrences,
                        final Context context) throws IOException, InterruptedException {
    final int bigramOccurrences = StreamSupport
        .stream(occurrences.spliterator(), false)
        .mapToInt(IntWritable::get)
        .sum();
    if (bigramOccurrences > OCCURRENCES_THRESHOLD)
      context.write(bigram, new IntWritable(bigramOccurrences));
  }
}

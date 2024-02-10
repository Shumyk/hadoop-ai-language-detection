package rocks.shumyk.hadoop.ai.language.detector.training;

import java.io.IOException;
import java.util.stream.StreamSupport;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class LanguageTraningReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
  public static final int MIN_OCCURRENCES = 7;

  @Override
  protected void reduce(final Text key,
                        final Iterable<IntWritable> values,
                        final Context context) throws IOException, InterruptedException {
    final int sum = StreamSupport.stream(values.spliterator(), false)
        .mapToInt(IntWritable::get)
        .sum();
    if (sum > MIN_OCCURRENCES)
      context.write(key, new IntWritable(sum));
  }
}

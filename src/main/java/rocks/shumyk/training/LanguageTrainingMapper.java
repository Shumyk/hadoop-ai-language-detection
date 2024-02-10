package rocks.shumyk.training;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import rocks.shumyk.BigramExtractor;
import rocks.shumyk.model.value.Bigram;
import rocks.shumyk.util.Texts;

public class LanguageTrainingMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
  private static final IntWritable one = new IntWritable(1);

  @Override
  protected void map(final LongWritable key,
                     final Text value,
                     final Context context) throws IOException, InterruptedException {
    for (Bigram bigram : BigramExtractor.flat(value.toString()))
      context.write(Texts.quotes(bigram.name()), one);
  }
}

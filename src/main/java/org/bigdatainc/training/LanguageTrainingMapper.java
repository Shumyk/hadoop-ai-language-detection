package org.bigdatainc.training;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.bigdatainc.BigramExtractor;
import org.bigdatainc.model.value.Bigram;
import org.bigdatainc.util.Texts;

public class LanguageTrainingMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
  private static final IntWritable one = new IntWritable(1);

  @Override
  protected void map(final LongWritable key,
                     final Text value,
                     final Context context) throws IOException, InterruptedException {
    for (Bigram bigram : BigramExtractor.counted(value.toString()).keySet())
      context.write(Texts.quotes(bigram.name()), one);
  }
}

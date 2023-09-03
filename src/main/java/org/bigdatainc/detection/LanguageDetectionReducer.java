package org.bigdatainc.detection;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class LanguageDetectionReducer extends Reducer<Text, Text, Text, Text> {

  @Override
  public void reduce(final Text key,
                     final Iterable<Text> values,
                     final Context context) throws IOException, InterruptedException {
    context.write(key, new Text());
    for (Text value : values) {
      context.write(new Text("\t->"), value);
    }
  }
}

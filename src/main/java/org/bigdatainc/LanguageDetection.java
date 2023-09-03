package org.bigdatainc;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.bigdatainc.model.BigramsProfiles;
import org.bigdatainc.model.DetectedLanguage;
import org.bigdatainc.model.LanguageProfiles;
import org.bigdatainc.model.ProbableLanguages;
import org.bigdatainc.model.value.Bigram;
import org.bigdatainc.model.value.Count;
import org.bigdatainc.util.FileUtil;

public class LanguageDetection {
  private LanguageDetection() {}

  public static class BigramMapper extends Mapper<LongWritable, Text, Text, Text> {
    private BigramsProfiles bigramFrequency;

    @Override
    protected void setup(final Context context) throws IOException {
      final LanguageProfiles profiles = FileUtil.loadLanguageProfiles(context.getConfiguration());
      bigramFrequency = new BigramsProfiles(profiles);
    }

    @Override
    public void map(final LongWritable key,
                    final Text value,
                    final Context context) throws InterruptedException, IOException {
      final Map<Bigram, Count> bigrams = BigramExtractor.extractCounted(value.toString());
      if (bigrams.isEmpty())
        return;

      final DetectedLanguage detectedLanguage = detect(bigrams);

      final Text info = new Text("%s: %s".formatted(detectedLanguage.toString(), value));
      context.write(new Text(detectedLanguage.language().value()), info);
    }

    private DetectedLanguage detect(final Map<Bigram, Count> bigramsCounted) {
      final ProbableLanguages probabilities = ProbableLanguages.init(bigramFrequency.languages());
      final double weight = 0.5 / 10_000;
      for (final Entry<Bigram, Count> bigram$count : bigramsCounted.entrySet()) {
        probabilities.merge(weight, bigramFrequency.getProbabilities(bigram$count.getKey()), bigram$count.getValue());
        if (probabilities.normalize() > 0.99999) break;
      }
      probabilities.normalize();
      return probabilities.bestGuess();
    }
  }

  public static class BigramReducer extends Reducer<Text, Text, Text, Text> {

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

}

package org.bigdatainc;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.bigdatainc.model.LanguageProbabilities;
import org.bigdatainc.model.LanguageProfiles;
import org.bigdatainc.util.FileUtil;

public class LanguageDetection {
  private LanguageDetection() {}

  public static void main(String[] args) {
    final Random random = new Random(41);
    for (int i = 0; i < 20; i++) {
      final double gaussian = random.nextGaussian();
      final double alpha = 0.5 + (gaussian * 0.5);
      final double weight = alpha / 10_000;
      System.out.printf("gaus: %s, alpha: %s, weight: %s%n", gaussian, alpha, weight);
    }
  }

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
      final Map<String, Long> bigrams = BigramExtractor.extractCounted(value.toString());
      if (bigrams.isEmpty()) return;
      final DetectedLanguage detectedLanguage = detect(bigrams).orElse(DetectedLanguage.empty());

      final Text info = new Text("%s: %s".formatted(detectedLanguage.toString(), value));
      context.write(new Text(detectedLanguage.lang()), info);
    }

    private Optional<DetectedLanguage> detect(final Map<String, Long> bigrams) {
      final ProbableLanguages probabilities = ProbableLanguages.init(bigramFrequency.languages());
      final double weight = 0.5 / 10_000;
      for (final Entry<String, Long> bigram$count : bigrams.entrySet()) {
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

  public static class ProbableLanguages extends HashMap<String, Double> {
    public ProbableLanguages(final Map<String, Double> input) {super(input);}

    public static ProbableLanguages empty(final Collection<String> languages) {
      return languages.stream()
          .map(lang -> Map.entry(lang, 0.0))
          .collect(collectingAndThen(toMap(Entry::getKey, Entry::getValue), ProbableLanguages::new));
    }

    public static ProbableLanguages init(final Collection<String> languages) {
      return languages.stream()
          .map(lang -> Map.entry(lang, 1.0 / languages.size()))
          .collect(collectingAndThen(toMap(Entry::getKey, Entry::getValue), ProbableLanguages::new));
    }

    private static double calculateProbability(final double probability,
                                               final double weight,
                                               final double bigramProbability) {
      return probability * (weight + bigramProbability);
    }

    public void merge(final double weight,
                      final LanguageProbabilities langProbabilities,
                      final long count) {
      forEach((lang, currentProbability) -> {
        for (long x = 0; x < count; x++) {
          final double bigramProbability = langProbabilities.getOrDefault(lang, 0.0);
          put(lang, calculateProbability(currentProbability, weight, bigramProbability));
        }
      });
    }

    public double normalize() {
      final double[] maxProbability = {0.0};
      final double sumProbability = values().stream().mapToDouble(x -> x).sum();
      assert sumProbability != 0.0 : "Total sum of input is zero!";

      this.forEach((lang, oldProbability) -> {
        final double newProbability = oldProbability / sumProbability;
        put(lang, newProbability);
        if (newProbability > maxProbability[0])
          maxProbability[0] = newProbability;
      });
      return maxProbability[0];
    }

    public Optional<DetectedLanguage> bestGuess() {
      final List<DetectedLanguage> detectedLanguages = entrySet()
          .stream()
          .map(e -> new DetectedLanguage(e.getKey(), e.getValue()))
          .toList();
      return detectedLanguages
          .stream()
          .max(Comparator.comparingDouble(DetectedLanguage::probability));
    }
  }

  public record DetectedLanguage(String lang, double probability) {
    public static DetectedLanguage empty() {
      return new DetectedLanguage("not found", Double.NEGATIVE_INFINITY);
    }

    @Override
    public String toString() {
      return "%s=%.10f".formatted(lang, probability);
    }
  }
}

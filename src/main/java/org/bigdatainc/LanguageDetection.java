package org.bigdatainc;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.bigdatainc.model.BigramsProfiles;
import org.bigdatainc.model.LanguageProbabilities;
import org.bigdatainc.model.LanguageProfiles;
import org.bigdatainc.model.value.Bigram;
import org.bigdatainc.model.value.Count;
import org.bigdatainc.model.value.Language;
import org.bigdatainc.model.value.Probability;
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
      if (bigrams.isEmpty()) return;
      final DetectedLanguage detectedLanguage = detect(bigrams).orElse(DetectedLanguage.empty());

      final Text info = new Text("%s: %s".formatted(detectedLanguage.toString(), value));
      context.write(new Text(detectedLanguage.language().value()), info);
    }

    private Optional<DetectedLanguage> detect(final Map<Bigram, Count> bigramsCounted) {
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

  public static class ProbableLanguages extends HashMap<Language, Probability> {
    public ProbableLanguages(final Map<Language, Probability> input) {super(input);}

    public static ProbableLanguages empty(final Collection<Language> languages) {
      return languages.stream()
          .map(lang -> Map.entry(lang, Probability.zero()))
          .collect(collectingAndThen(toMap(Entry::getKey, Entry::getValue), ProbableLanguages::new));
    }

    public static ProbableLanguages init(final Collection<Language> languages) {
      return languages.stream()
          .map(lang -> Map.entry(lang, Probability.divided(languages.size())))
          .collect(collectingAndThen(toMap(Entry::getKey, Entry::getValue), ProbableLanguages::new));
    }

    public void merge(final double weight,
                      final LanguageProbabilities langProbabilities,
                      final Count count) {
      forEach((lang, currentProbability) -> {
        for (long x = 0; x < count.value(); x++) {
          final Probability bigramProbability = langProbabilities.getOrDefault(lang, Probability.zero());
          put(lang, currentProbability.multiply(weight, bigramProbability));
        }
      });
    }

    // TODO: look closer
    public double normalize() {
      final double[] maxProbability = {0.0};
      final double sumProbability = values().stream().mapToDouble(Probability::value).sum();
      assert sumProbability != 0.0 : "Total sum of input is zero!";

      this.forEach((lang, oldProbability) -> {
        final Probability newProbability = oldProbability.divide(sumProbability);
        put(lang, newProbability);
        if (newProbability.value() > maxProbability[0])
          maxProbability[0] = newProbability.value();
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
          .max(comparingDouble(x -> x.probability().value()));
    }
  }

  public record DetectedLanguage(Language language, Probability probability) {
    public static DetectedLanguage empty() {
      return new DetectedLanguage(Language.notFound(), Probability.zero());
    }

    @Override
    public String toString() {
      return "%s=%.10f".formatted(language.value(), probability.value());
    }
  }
}

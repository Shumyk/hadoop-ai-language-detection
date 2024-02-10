package rocks.shumyk.detection;

import java.io.IOException;
import java.util.Map;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import rocks.shumyk.BigramExtractor;
import rocks.shumyk.model.BigramsProfiles;
import rocks.shumyk.model.DetectedLanguage;
import rocks.shumyk.model.LanguageProfiles;
import rocks.shumyk.model.ProbableLanguages;
import rocks.shumyk.model.value.Bigram;
import rocks.shumyk.model.value.Count;
import rocks.shumyk.util.FileUtil;
import rocks.shumyk.util.Texts;

public class LanguageDetectionMapper extends Mapper<LongWritable, Text, Text, Text> {
  public static final double ALPHA = 0.5;
  public static final double BASE_FREQ = 10_000;
  public static final double WEIGHT = ALPHA / BASE_FREQ;
  public static final double CONFIDENCE_THRESHOLD = 0.99999;

  private BigramsProfiles bigramsProfiles;

  @Override
  protected void setup(final Context context) throws IOException {
    final LanguageProfiles profiles = FileUtil.loadLanguageProfiles(context.getConfiguration());
    bigramsProfiles = new BigramsProfiles(profiles);
  }

  @Override
  public void map(final LongWritable ignored,
                  final Text inputTextValue,
                  final Context context) throws InterruptedException, IOException {
    final Map<Bigram, Count> inputTextCountedBigrams = BigramExtractor.counted(inputTextValue.toString());
    if (inputTextCountedBigrams.isEmpty())
      return;

    final DetectedLanguage detectedLanguage = detectLanguage(inputTextCountedBigrams);

    final Text outputKey = Texts.just(detectedLanguage.language().value());
    final Text outputValue = Texts.keyValue(detectedLanguage, inputTextValue);
    context.write(outputKey, outputValue);
  }

  private DetectedLanguage detectLanguage(final Map<Bigram, Count> countedBigrams) {
    final ProbableLanguages probables = ProbableLanguages.init(bigramsProfiles.languages());

    for (final var bigram$count : countedBigrams.entrySet()) {
      final Bigram bigram = bigram$count.getKey();
      final Count count = bigram$count.getValue();
      probables.merge(WEIGHT, bigramsProfiles.getProbabilities(bigram), count);

      if (probables.normalize() > CONFIDENCE_THRESHOLD)
        break;
    }
    return probables.bestGuess();
  }
}

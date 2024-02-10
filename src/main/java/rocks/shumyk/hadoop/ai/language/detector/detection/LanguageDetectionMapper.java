package rocks.shumyk.hadoop.ai.language.detector.detection;

import java.io.IOException;
import java.util.Map;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import rocks.shumyk.hadoop.ai.language.detector.BigramExtractor;
import rocks.shumyk.hadoop.ai.language.detector.model.DetectedLanguage;
import rocks.shumyk.hadoop.ai.language.detector.model.LanguageProfiles;
import rocks.shumyk.hadoop.ai.language.detector.util.Texts;
import rocks.shumyk.hadoop.ai.language.detector.model.BigramsProfiles;
import rocks.shumyk.hadoop.ai.language.detector.model.ProbableLanguages;
import rocks.shumyk.hadoop.ai.language.detector.model.value.Bigram;
import rocks.shumyk.hadoop.ai.language.detector.model.value.Count;
import rocks.shumyk.hadoop.ai.language.detector.util.FileUtil;

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

    final Text languageKey = Texts.capitalize(detectedLanguage.language().value());
    final Text probabilityToInputText = Texts.keyValue(detectedLanguage.probabilityFormatted(), inputTextValue);
    context.write(languageKey, probabilityToInputText);
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

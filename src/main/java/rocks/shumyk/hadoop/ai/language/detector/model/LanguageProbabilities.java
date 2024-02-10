package rocks.shumyk.hadoop.ai.language.detector.model;

import java.util.HashMap;
import java.util.Map;
import rocks.shumyk.hadoop.ai.language.detector.model.value.Language;
import rocks.shumyk.hadoop.ai.language.detector.model.value.Probability;

public class LanguageProbabilities extends HashMap<Language, Probability> {
  private static final LanguageProbabilities EMPTY = new LanguageProbabilities(Map.of());

  public LanguageProbabilities() {super();}

  public LanguageProbabilities(final Map<Language, Probability> input) {super(input);}

  public static LanguageProbabilities empty() {return EMPTY;}

  public LanguageProbabilities add(final Language language,
                                   final Probability probability) {
    put(language, probability);
    return this;
  }

  public Probability getOrZero(final Language language) {
    return getOrDefault(language, Probability.zero());
  }
}

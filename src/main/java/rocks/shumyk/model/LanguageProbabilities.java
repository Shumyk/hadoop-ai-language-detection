package rocks.shumyk.model;

import java.util.HashMap;
import java.util.Map;
import rocks.shumyk.model.value.Language;
import rocks.shumyk.model.value.Probability;

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

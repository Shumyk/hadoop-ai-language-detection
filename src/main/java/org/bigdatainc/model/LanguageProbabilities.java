package org.bigdatainc.model;

import static java.util.Optional.ofNullable;

import java.util.HashMap;
import java.util.Map;

public class LanguageProbabilities extends HashMap<String, Double> {
  private static final LanguageProbabilities EMPTY = new LanguageProbabilities(Map.of());

  public LanguageProbabilities() {super();}

  public LanguageProbabilities(final Map<String, Double> input) {super(input);}

  public static LanguageProbabilities nullable(final LanguageProbabilities oldProbability) {
    return ofNullable(oldProbability)
        .orElseGet(LanguageProbabilities::new);
  }

  public static LanguageProbabilities empty() {return EMPTY;}

  public LanguageProbabilities add(final String language,
                                   final double probability) {
    put(language, probability);
    return this;
  }
}

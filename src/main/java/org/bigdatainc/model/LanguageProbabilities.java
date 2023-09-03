package org.bigdatainc.model;

import java.util.HashMap;
import java.util.Map;
import org.bigdatainc.model.value.Language;
import org.bigdatainc.model.value.Probability;

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
}

package org.bigdatainc.model;

import static java.util.Optional.ofNullable;

import java.util.HashMap;
import org.bigdatainc.model.value.Bigram;
import org.bigdatainc.model.value.Probability;

public class BigramsProbabilities extends HashMap<Bigram, LanguageProbabilities> {
  public BigramsProbabilities(final int initialCapacity) {
    super(initialCapacity);
  }

  public void add(final LanguageProfile profile,
                  final BigramData bigram) {
    final Probability probability = bigram.probability(profile.totalOccurrences());
    this.compute(
        bigram.name(),
        (i, old) -> ofNullable(old)
            .orElseGet(LanguageProbabilities::new)
            .add(profile.language(), probability)
    );
  }
}

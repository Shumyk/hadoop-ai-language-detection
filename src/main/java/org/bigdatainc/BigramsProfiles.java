package org.bigdatainc;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bigdatainc.model.BigramData;
import org.bigdatainc.model.LanguageProfile;
import org.bigdatainc.model.LanguageProbabilities;
import org.bigdatainc.model.LanguageProfiles;
import org.bigdatainc.model.value.Bigram;
import org.bigdatainc.model.value.Language;
import org.bigdatainc.model.value.Probability;

public class BigramsProfiles {
  private final Set<Language> languages;
  private final BigramsProbabilities bigramsProbabilities;

  public BigramsProfiles(final LanguageProfiles profiles) {
    this.languages = new HashSet<>(profiles.size());
    this.bigramsProbabilities = new BigramsProbabilities(profiles.bigramsCount());

    profiles.stream()
        .peek(profile -> languages.add(profile.language()))
        .forEach(profile ->
            profile.bigrams()
                .forEach(bigram -> bigramsProbabilities.add(profile, bigram))
        );
  }

  public Set<Language> languages() {
    return Collections.unmodifiableSet(languages);
  }

  public LanguageProbabilities getProbabilities(final Bigram bigram) {
    return bigramsProbabilities.getOrDefault(bigram, LanguageProbabilities.empty());
  }

  public static class BigramsProbabilities extends HashMap<Bigram, LanguageProbabilities> {
    public BigramsProbabilities(final int initialCapacity) {
      super(initialCapacity);
    }

    public void add(final LanguageProfile profile,
                    final BigramData bigram) {
      final Probability probability = bigram.probability(profile.totalOccurrences());
      this.compute(
          bigram.name(),
          (i, old) -> LanguageProbabilities.nullable(old).add(profile.language(), probability)
      );
    }
  }
}

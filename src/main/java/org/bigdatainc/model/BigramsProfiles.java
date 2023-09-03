package org.bigdatainc.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bigdatainc.model.value.Bigram;
import org.bigdatainc.model.value.Language;

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

}

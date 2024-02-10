package rocks.shumyk.hadoop.ai.language.detector.model;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.Set;
import rocks.shumyk.hadoop.ai.language.detector.model.value.Bigram;
import rocks.shumyk.hadoop.ai.language.detector.model.value.Language;

public class BigramsProfiles {
  private final Set<Language> languages;
  private final BigramsProbabilities bigramsProbabilities;

  public BigramsProfiles(final LanguageProfiles profiles) {
    this.languages = profiles.stream()
        .map(LanguageProfile::language)
        .collect(toUnmodifiableSet());
    this.bigramsProbabilities = new BigramsProbabilities(profiles.bigramsCount());

    for (final LanguageProfile profile : profiles)
      profile
          .bigramOccurrences()
          .forEach(occurrences -> bigramsProbabilities.add(profile, occurrences));
  }

  public Set<Language> languages() {
    return languages;
  }

  public LanguageProbabilities getProbabilities(final Bigram bigram) {
    return bigramsProbabilities.getOrDefault(bigram, LanguageProbabilities.empty());
  }

}

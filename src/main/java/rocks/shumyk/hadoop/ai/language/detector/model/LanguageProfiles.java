package rocks.shumyk.hadoop.ai.language.detector.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LanguageProfiles extends LinkedList<LanguageProfile> {
  public LanguageProfiles(final Collection<LanguageProfile> profiles) {
    super(profiles);
  }

  public int bigramsCount() {
    return (int) stream()
        .map(LanguageProfile::bigramOccurrences)
        .flatMap(List::stream)
        .map(BigramOccurrences::name)
        .distinct()
        .count();
  }
}

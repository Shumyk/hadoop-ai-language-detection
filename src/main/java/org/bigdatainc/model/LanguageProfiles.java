package org.bigdatainc.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LanguageProfiles extends LinkedList<LanguageProfile> {
  public LanguageProfiles(final Collection<LanguageProfile> profiles) {
    super(profiles);
  }

  public int bigramsCount() {
    return (int) stream()
        .map(LanguageProfile::bigrams)
        .flatMap(List::stream)
        .map(BigramData::name)
        .distinct()
        .count();
  }
}

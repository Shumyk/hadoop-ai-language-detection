package org.bigdatainc.model;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public record LanguageProfile(
    String language,
    int totalOccurrences,
    List<Bigram> bigrams
) {

  public static LanguageProfile parse(final String language,
                                      final byte[] contentBytes) {
    final int[] totalOccurrences = {0};
    return Arrays.stream(new String(contentBytes).split(System.lineSeparator()))
        .map(Bigram::parse)
        .peek(bigram -> totalOccurrences[0] += bigram.occurrences())
        .collect(collectingAndThen(toList(), x -> new LanguageProfile(language, totalOccurrences[0], x)));
  }
}

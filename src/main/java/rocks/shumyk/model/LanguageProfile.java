package rocks.shumyk.model;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import rocks.shumyk.model.value.Language;

public record LanguageProfile(
    Language language,
    int totalOccurrences,
    List<BigramData> bigrams
) {

  public static LanguageProfile parse(final Language language,
                                      final byte[] contentBytes) {
    final int[] totalOccurrences = {0};
    return Arrays.stream(new String(contentBytes).split(System.lineSeparator()))
        .map(BigramData::parse)
        .peek(bigram -> totalOccurrences[0] += bigram.occurrences())
        .collect(collectingAndThen(toList(), x -> new LanguageProfile(language, totalOccurrences[0], x)));
  }
}

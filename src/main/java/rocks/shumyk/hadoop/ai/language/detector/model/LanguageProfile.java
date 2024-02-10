package rocks.shumyk.hadoop.ai.language.detector.model;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import rocks.shumyk.hadoop.ai.language.detector.model.value.Language;

public record LanguageProfile(Language language,
                              int totalOccurrences,
                              List<BigramOccurrences> bigramOccurrences) {

  public static LanguageProfile parse(final Language language,
                                      final byte[] contentBytes) {
    final int[] totalOccurrences = {0};
    return Optional.of(contentBytes)
        .map(String::new)
        .map(x -> x.split(lineSeparator()))
        .map(Arrays::stream)
        .get()
        .map(BigramOccurrences::parse)
        .peek(bigram -> totalOccurrences[0] += bigram.occurrences())
        .collect(collectingAndThen(toList(), occurrences -> new LanguageProfile(language, totalOccurrences[0], occurrences)));
  }
}

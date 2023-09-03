package org.bigdatainc.model;

import org.bigdatainc.model.value.Bigram;
import org.bigdatainc.model.value.Probability;

public record BigramData(
    Bigram name,
    int occurrences
) {

  /**
   * @param bigramString - bigram to occurrences entry, e.g. '"to":69'
   * @return parsed {@link BigramData}
   */
  public static BigramData parse(final String bigramString) {
    final String[] bigramParts = bigramString.split(":");
    final String name = bigramParts[0].replace("\"", "");
    final int occurrences = Integer.parseInt(bigramParts[1]);

    return new BigramData(Bigram.of(name), occurrences);
  }

  public Probability probability(final double totalOccurrences) {
    return Probability.of(occurrences / totalOccurrences);
  }
}

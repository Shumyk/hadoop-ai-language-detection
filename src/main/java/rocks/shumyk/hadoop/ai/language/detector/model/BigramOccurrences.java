package rocks.shumyk.hadoop.ai.language.detector.model;

import rocks.shumyk.hadoop.ai.language.detector.model.value.Probability;
import rocks.shumyk.hadoop.ai.language.detector.model.value.Bigram;

public record BigramOccurrences(Bigram name,
                                int occurrences) {

  /**
   * @param bigramString - bigram to occurrences entry, e.g. '"to":69'
   * @return parsed {@link BigramOccurrences}
   */
  public static BigramOccurrences parse(final String bigramString) {
    final String[] bigramParts = bigramString.split(":");
    final String name = bigramParts[0].replace("\"", "");
    final int occurrences = Integer.parseInt(bigramParts[1]);

    return new BigramOccurrences(Bigram.of(name), occurrences);
  }

  public Probability probability(final double totalOccurrences) {
    return Probability.of(occurrences / totalOccurrences);
  }
}

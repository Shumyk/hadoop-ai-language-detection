package org.bigdatainc.model;

public record Bigram(
    String name,
    int occurrences
) {

  /**
   * @param bigramString - bigram to occurrences entry, e.g. '"to":69'
   * @return parsed {@link Bigram}
   */
  public static Bigram parse(final String bigramString) {
    final String[] bigramParts = bigramString.split(":");
    final String name = bigramParts[0].replace("\"", "");
    final int occurrences = Integer.parseInt(bigramParts[1]);

    return new Bigram(name, occurrences);
  }

  public double probability(final double totalOccurrences) {
    return occurrences / totalOccurrences;
  }
}

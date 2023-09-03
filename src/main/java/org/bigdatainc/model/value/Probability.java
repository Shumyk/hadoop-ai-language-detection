package org.bigdatainc.model.value;

public record Probability(double value) {

  public static Probability of(final double value) {
    return new Probability(value);
  }

  public static Probability zero() {
    return new Probability(0.0);
  }

  public static Probability divided(final int by) {
    return new Probability(1.0 / by);
  }


  public Probability calculate(final double weight,
                               final Probability bigramProbability) {
    return Probability.of(value * (weight + bigramProbability.value()));
  }

  public Probability divide(final double sumProbability) {
    return Probability.of(value / sumProbability);
  }
}

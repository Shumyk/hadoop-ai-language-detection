package rocks.shumyk.hadoop.ai.language.detector.model.value;

public record Probability(double value) implements Comparable<Probability> {

  public static Probability of(final double value) {
    return new Probability(value);
  }

  public static Probability zero() {
    return new Probability(0.0);
  }

  public static Probability divided(final int by) {
    return new Probability(1.0 / by);
  }


  public Probability multiply(final double weight,
                              final Probability bigramProbability) {
    return Probability.of(value * (weight + bigramProbability.value()));
  }

  public Probability divide(final double divider) {
    return Probability.of(value / divider);
  }


  @Override
  public int compareTo(final Probability o) {
    return Double.compare(value, o.value);
  }

  @Override
  public String toString() {
    return Double.toString(value);
  }
}

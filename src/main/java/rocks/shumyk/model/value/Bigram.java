package rocks.shumyk.model.value;

public record Bigram(String name) {
  public static Bigram of(final String name) {
    return new Bigram(name);
  }
}

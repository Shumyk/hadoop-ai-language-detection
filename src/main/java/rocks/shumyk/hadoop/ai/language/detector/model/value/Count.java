package rocks.shumyk.hadoop.ai.language.detector.model.value;

public record Count(int value) {
  public static Count of(final long value) {
    return new Count((int) value);
  }
}

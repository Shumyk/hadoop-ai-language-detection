package rocks.shumyk.hadoop.ai.language.detector.model.value;

public record Language(String value) {

  public static Language of(final String value) {
    return new Language(value);
  }

  public static Language unresolved() {
    return new Language("unresolved");
  }
}

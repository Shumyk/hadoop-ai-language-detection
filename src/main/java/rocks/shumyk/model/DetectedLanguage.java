package rocks.shumyk.model;

import rocks.shumyk.model.value.Language;
import rocks.shumyk.model.value.Probability;

public record DetectedLanguage(Language language, Probability probability) {
  private static final double MINIMAL_CONFIDENCE = 0.9;

  public static DetectedLanguage unresolved() {
    return new DetectedLanguage(Language.notResolved(), Probability.zero());
  }

  public boolean meetsConfidence() {
    return probability.value() > MINIMAL_CONFIDENCE;
  }

  @Override
  public String toString() {
    return "%s=%.10f".formatted(language.value(), probability.value());
  }
}

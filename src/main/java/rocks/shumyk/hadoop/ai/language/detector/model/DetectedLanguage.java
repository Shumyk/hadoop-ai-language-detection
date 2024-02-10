package rocks.shumyk.hadoop.ai.language.detector.model;

import rocks.shumyk.hadoop.ai.language.detector.model.value.Language;
import rocks.shumyk.hadoop.ai.language.detector.model.value.Probability;

public record DetectedLanguage(Language language,
                               Probability probability) {
  private static final double MINIMAL_CONFIDENCE = 0.9;

  public static DetectedLanguage unresolved() {
    return new DetectedLanguage(Language.unresolved(), Probability.zero());
  }

  public boolean meetsConfidence() {
    return probability.value() > MINIMAL_CONFIDENCE;
  }

  public String probabilityFormatted() {
    return "%.10f".formatted(probability.value());
  }
}

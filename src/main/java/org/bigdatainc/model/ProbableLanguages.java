package org.bigdatainc.model;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bigdatainc.model.value.Count;
import org.bigdatainc.model.value.Language;
import org.bigdatainc.model.value.Probability;

public class ProbableLanguages extends HashMap<Language, Probability> {
  public static final double ZERO = 0.0;

  public ProbableLanguages(final Map<Language, Probability> input) {super(input);}

  public static ProbableLanguages init(final Collection<Language> languages) {
    return languages.stream()
        .map(lang -> Map.entry(lang, Probability.divided(languages.size())))
        .collect(collectingAndThen(toMap(Entry::getKey, Entry::getValue), ProbableLanguages::new));
  }

  public void merge(final double weight,
                    final LanguageProbabilities profilesProbabilities,
                    final Count bigramCount) {
    forEach((language, currentProbability) -> {
      final Probability languageProfileProbability = profilesProbabilities.getOrZero(language);
      for (long x = 0; x < bigramCount.value(); x++)
        currentProbability = currentProbability.multiply(weight, languageProfileProbability);
      put(language, currentProbability);
    });
  }

  public double normalize() {
    final double[] maxProbability = {ZERO};
    final double sumProbability = this.values().stream().mapToDouble(Probability::value).sum();
    assert sumProbability != ZERO : "Total sum of probabilities is zero!";

    this.forEach((lang, oldProbability) -> {
      final Probability normalizedProbability = oldProbability.divide(sumProbability);
      put(lang, normalizedProbability);

      if (normalizedProbability.value() > maxProbability[0])
        maxProbability[0] = normalizedProbability.value();
    });
    return maxProbability[0];
  }

  public DetectedLanguage bestGuess() {
    return entrySet()
        .stream()
        .map(e -> new DetectedLanguage(e.getKey(), e.getValue()))
        .max(comparing(DetectedLanguage::probability))
        .filter(DetectedLanguage::meetsConfidence)
        .orElseGet(DetectedLanguage::unresolved);
  }
}

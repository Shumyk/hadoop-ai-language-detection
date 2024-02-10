package rocks.shumyk.hadoop.ai.language.detector;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import rocks.shumyk.hadoop.ai.language.detector.util.Texts;
import rocks.shumyk.hadoop.ai.language.detector.model.value.Bigram;
import rocks.shumyk.hadoop.ai.language.detector.model.value.Count;

public class BigramExtractor {
  private BigramExtractor() {}

  public static final int BIGRAM_LENGTH = 2;

  public static List<Bigram> flat(final CharSequence text) {
    final CharSequence readyText = Texts.applyPadding(text);
    final int endPos = readyText.length() - (BIGRAM_LENGTH - 1);

    return IntStream.range(0, endPos)
        .mapToObj(pos -> readyText.subSequence(pos, pos + BIGRAM_LENGTH))
        .map(CharSequence::toString)
        .map(Texts::removeSpecialCharacters)
        .map(Texts::lowerCaseIfAllUpper)
        .filter(Texts::isAlphaSpace)
        .map(Bigram::of)
        .toList();
  }

  public static Map<Bigram, Count> counted(final CharSequence text) {
    return flat(text)
        .stream()
        .collect(groupingBy(identity(), collectingAndThen(counting(), Count::of)));
  }

}

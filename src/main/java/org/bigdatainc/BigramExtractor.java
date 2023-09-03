package org.bigdatainc;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.Map;
import java.util.stream.IntStream;
import org.bigdatainc.model.value.Bigram;
import org.bigdatainc.model.value.Count;
import org.bigdatainc.util.Texts;

public class BigramExtractor {
  private BigramExtractor() {}

  public static final int BIGRAM_LENGTH = 2;

  // TODO: think of return type object
  public static Map<Bigram, Count> counted(final CharSequence text) {
    final CharSequence readyText = Texts.applyPadding(text);
    final int endPos = readyText.length() - (BIGRAM_LENGTH - 1);

    return IntStream.range(0, endPos)
        .mapToObj(pos -> readyText.subSequence(pos, pos + BIGRAM_LENGTH))
        .map(CharSequence::toString)
        .map(Texts::removeSpecialCharacters)
        .map(Texts::lowerCaseIfAllUpper)
        .filter(Texts::isAlphaSpace)
        .map(Bigram::of)
        .collect(groupingBy(identity(), collectingAndThen(counting(), Count::of)));
  }

}

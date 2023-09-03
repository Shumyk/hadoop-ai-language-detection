package org.bigdatainc;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bigdatainc.util.Texts;

public class BigramExtractor {
    private BigramExtractor() {}

    public static final int BIGRAM_LENGTH = 2;

    public static Map<String, Long> extractCounted(final CharSequence text) {
        final CharSequence readyText = Texts.applyPadding(text);
        final int endPos = readyText.length() - (BIGRAM_LENGTH - 1);

        return IntStream.range(0, endPos)
                .mapToObj(pos -> readyText.subSequence(pos, pos + BIGRAM_LENGTH))
                .map(CharSequence::toString)
                .map(Texts::removeSpecialCharacters)
                .map(Texts::lowerCaseIfAllUpper)
                .filter(Texts::isAlphaSpace)
                .collect(Collectors.groupingBy(identity(), counting()));
    }

}

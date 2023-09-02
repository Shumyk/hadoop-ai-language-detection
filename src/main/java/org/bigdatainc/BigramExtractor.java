package org.bigdatainc;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BigramExtractor {
    private BigramExtractor() {}

    private static final char PADDING = ' ';
    private static final int BIGRAM_LENGTH = 2;

    public static List<String> extract(final CharSequence text) {
        final CharSequence readyText = applyPadding(text);
        final int numGrams = readyText.length() - (BIGRAM_LENGTH - 1);

        return IntStream.range(0, numGrams)
                .mapToObj(pos -> readyText.subSequence(pos, pos + BIGRAM_LENGTH))
                .map(CharSequence::toString)
                .map(x -> x.replaceAll("[!?^$\\-+*<>(){}\\[\\].,‘\"]", " "))
                .map(BigramExtractor::lowerCaseIfAllUpper)
                .filter(BigramExtractor::isAlphaSpace)
                .toList();
    }

    public static Map<String, Long> extractCounted(final CharSequence text) {
        final CharSequence readyText = applyPadding(text);
        final int endPos = readyText.length() - (BIGRAM_LENGTH - 1);

        return IntStream.range(0, endPos)
                .mapToObj(pos -> readyText.subSequence(pos, pos + BIGRAM_LENGTH))
                .map(CharSequence::toString)
                .map(x -> x.replaceAll("[!?^$\\-+*<>(){}\\[\\].,‘\"]", " "))
                .map(BigramExtractor::lowerCaseIfAllUpper)
                .filter(BigramExtractor::isAlphaSpace)
                .collect(Collectors.groupingBy(identity(), counting()));
    }

    private static String lowerCaseIfAllUpper(final String x) {
        if (x.equals(x.toUpperCase()))
            return x.toLowerCase();
        return x;
    }

    // TODO: move
    public static boolean isAlphaSpace(final String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetter(str.charAt(i)) && str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    private static CharSequence applyPadding(final CharSequence text) {
        if (text.isEmpty())
            return text;

        final char firstChar = text.charAt(0);
        final char lastChar = text.charAt(text.length() - 1);
        if (firstChar == PADDING && lastChar == PADDING)
            return text;

        final StringBuilder sb = new StringBuilder();
        if (firstChar != PADDING) sb.append(PADDING);
        sb.append(text);
        if (lastChar != PADDING) sb.append(PADDING);
        return sb;
    }
}

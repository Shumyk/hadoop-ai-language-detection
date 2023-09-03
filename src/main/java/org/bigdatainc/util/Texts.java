package org.bigdatainc.util;

import org.apache.hadoop.io.Text;

public final class Texts {
    private Texts() {}

    public static final char PADDING = ' ';
    public static final String SPACE = " ";
    public static final String SPECIAL_CHARS_REGEX = "[!?^$\\-+*<>(){}\\[\\].,‘\"]";

    public static Text quotes(final String x) {
        return new Text("\"%s\"".formatted(x));
    }

    public static String removeSpecialCharacters(final String x) {
        return x.replaceAll(SPECIAL_CHARS_REGEX, SPACE);
    }

    public static String lowerCaseIfAllUpper(final String x) {
        if (x.equals(x.toUpperCase()))
            return x.toLowerCase();
        return x;
    }

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

    public static CharSequence applyPadding(final CharSequence text) {
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

package io.github.axolotlclient.AxolotlClientConfig.annotation;

import java.util.Locale;

public enum NamingScheme {
    NONE,
    CAMEL_CASE,
    PASCAL_CASE,
    SNAKE_CASE,
    KEBAB_CASE,
    SCREAMING_SNAKE_CASE,
    SCREAMING_KEBAB_CASE,
    UPPERCASE,
    LOWERCASE;

    public String apply(String value) {
        if (value.isBlank()) {
            return value;
        }
        return switch (this) {
            case NONE, CAMEL_CASE -> value;
            case PASCAL_CASE -> capitalize(value);
            case SNAKE_CASE -> {
                var builder = new StringBuilder(value.length());
                for (int i = 0; i < value.length(); i++) {
                    int codepoint = value.codePointAt(i);
                    if (Character.isUpperCase(codepoint)) {
                        builder.append("_");
                        codepoint = Character.toLowerCase(codepoint);
                    }
                    builder.appendCodePoint(codepoint);
                }
                yield builder.toString();
            }
            case KEBAB_CASE -> SNAKE_CASE.apply(value).replace("_", "-");
            case SCREAMING_SNAKE_CASE -> SNAKE_CASE.apply(value).toUpperCase(Locale.ROOT);
            case SCREAMING_KEBAB_CASE -> KEBAB_CASE.apply(value).toUpperCase(Locale.ROOT);
            case UPPERCASE -> value.toUpperCase(Locale.ROOT);
            case LOWERCASE -> value.toLowerCase(Locale.ROOT);
        };
    }

    private static String capitalize(String s) {
        if (s.isEmpty()) return s;

        int pos;

        for (pos = 0; pos < s.length(); pos++) {
            if (Character.isLetterOrDigit(s.codePointAt(pos))) {
                break;
            }
        }

        if (pos == s.length()) return s;

        int cp = s.codePointAt(pos);
        int cpUpper = Character.toUpperCase(cp);
        if (cpUpper == cp) return s;

        StringBuilder ret = new StringBuilder(s.length());
        ret.append(s, 0, pos);
        ret.appendCodePoint(cpUpper);
        ret.append(s, pos + Character.charCount(cp), s.length());

        return ret.toString();
    }
}

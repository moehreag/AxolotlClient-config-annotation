package io.github.axolotlclient.AxolotlClientConfig.annotation;

import java.util.Locale;
import java.util.function.Function;

/**
 * Naming schemes to use for renaming all values of a category
 */
public enum NamingScheme {
    /**
     * No specified scheme, used as a fallback.
     */
    NONE,
    /**
     * camelCase is the default scheme for Java field names.
     */
    CAMEL_CASE,
    /**
     * PascalCase is the default scheme for Java class names
     */
    PASCAL_CASE,
    /**
     * snake_case is the preferred scheme for config files, but not used automatically
     */
    SNAKE_CASE,
    /**
     * kebab-case
     */
    KEBAB_CASE,
    /**
     * SCREAMING_SNAKE_CASE
     */
    SCREAMING_SNAKE_CASE,
    /**
     * SCREAMING-KEBAB-CASE
     */
    SCREAMING_KEBAB_CASE,
    /**
     * UPPERCASE
     */
    UPPERCASE,
    /**
     * lowercase
     */
    LOWERCASE;

    /**
     * Apply this naming scheme to a string
     * @param value The value to apply this naming scheme to
     * @return the formatted value
     */
    public String apply(String value) {
        if (value.isBlank()) {
            return value;
        }
        return switch (this) {
            case NONE -> value;
            case CAMEL_CASE -> uncapitalize(value);
            case PASCAL_CASE -> capitalize(value);
            case SNAKE_CASE -> {
                var builder = new StringBuilder(value.length());
                boolean inWord = false;
                for (int i = 0; i < value.length(); i++) {
                    int codepoint = value.codePointAt(i);
                    if (Character.isUpperCase(codepoint) && inWord) {
                        codepoint = Character.toLowerCase(codepoint);
                    } else if (Character.isUpperCase(codepoint) && i < value.length() -1 && Character.isLetter(value.codePointAt(i+1))) {
                        if (i != 0 && !(value.charAt(i-1) == '-' || value.charAt(i-1) == '_')) {
                            builder.append("_");
                        }
                        inWord = true;
                        codepoint = Character.toLowerCase(codepoint);
                    } else {
                        inWord = false;
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

    private static String applyToFirstLetterOrDigit(String s, Function<Integer, Integer> function) {
        if (s.isEmpty()) return s;

        int pos;

        for (pos = 0; pos < s.length(); pos++) {
            if (Character.isLetterOrDigit(s.codePointAt(pos))) {
                break;
            }
        }

        if (pos == s.length()) return s;

        int cp = s.codePointAt(pos);
        int cpUpper = function.apply(cp);
        if (cpUpper == cp) return s;

        StringBuilder ret = new StringBuilder(s.length());
        ret.append(s, 0, pos);
        ret.appendCodePoint(cpUpper);
        ret.append(s, pos + Character.charCount(cp), s.length());

        return ret.toString();
    }

    private static String capitalize(String s) {
        return applyToFirstLetterOrDigit(s, Character::toUpperCase);
    }

    private static String uncapitalize(String s) {
        return applyToFirstLetterOrDigit(s, Character::toLowerCase);
    }
}

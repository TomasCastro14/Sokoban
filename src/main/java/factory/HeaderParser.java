package factory;

import exception.InvalidLevelException;
import exception.LevelFormatRule;
import model.game.LevelHeader;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Pure Fabrication: turns the header lines ("clave: valor") into a typed LevelHeader.
 * Each field is registered by its key with its own conversion, so a new field is one more entry.
 * Header lines are the first lines of the file, so list index i is file line i + 1.
 */
class HeaderParser {
    private static final char SEPARATOR = ':';

    /** One header field: its key and how its text becomes a valid value (invalid text throws). */
    private record Field<T>(String key, Function<String, T> converter) {
    }

    private static final Field<String> NAME = new Field<>("nombre", LevelHeader::requireName);
    private static final Field<Duration> TIME_LIMIT = new Field<>("tiempo",
            text -> LevelHeader.requireTimeLimit(Duration.ofSeconds(Integer.parseInt(text))));
    private static final Field<Integer> TARGET_SCORE = new Field<>("objetivo",
            text -> LevelHeader.requireTargetScore(Integer.parseInt(text)));
    /** Order in which missing fields are reported. */
    private static final List<Field<?>> FIELDS = List.of(NAME, TIME_LIMIT, TARGET_SCORE);

    LevelHeader parse(List<String> lines) throws InvalidLevelException {
        Map<Field<?>, Object> values = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                readLine(lines.get(i), i + 1, values);
            }
        }
        for (Field<?> field : FIELDS) {
            if (!values.containsKey(field)) {
                throw new InvalidLevelException(LevelFormatRule.HEADER_MISSING_FIELD, field.key());
            }
        }
        return new LevelHeader(valueOf(values, NAME), valueOf(values, TIME_LIMIT), valueOf(values, TARGET_SCORE));
    }

    private void readLine(String line, int fileLine, Map<Field<?>, Object> values) throws InvalidLevelException {
        int separator = line.indexOf(SEPARATOR);
        if (separator < 0) {
            throw new InvalidLevelException(LevelFormatRule.HEADER_MALFORMED_LINE, fileLine);
        }
        String key = line.substring(0, separator).trim();
        Field<?> field = fieldFor(key, fileLine);
        if (values.containsKey(field)) {
            throw new InvalidLevelException(LevelFormatRule.HEADER_DUPLICATE_FIELD, fileLine, key);
        }
        values.put(field, convert(field, line.substring(separator + 1).trim(), fileLine));
    }

    private static Field<?> fieldFor(String key, int fileLine) throws InvalidLevelException {
        for (Field<?> field : FIELDS) {
            if (field.key().equals(key)) {
                return field;
            }
        }
        throw new InvalidLevelException(LevelFormatRule.HEADER_UNKNOWN_FIELD, fileLine, key);
    }

    /** The value's own rules (number format included) become a header error at the field's line. */
    private static Object convert(Field<?> field, String text, int fileLine) throws InvalidLevelException {
        try {
            return field.converter().apply(text);
        } catch (IllegalArgumentException e) {
            throw new InvalidLevelException(LevelFormatRule.INVALID_HEADER, fileLine, field.key() + " '" + text + "'");
        }
    }

    @SuppressWarnings("unchecked") // values are stored by the field that converted them
    private static <T> T valueOf(Map<Field<?>, Object> values, Field<T> field) {
        return (T) values.get(field);
    }
}

package exception;

import java.util.Objects;

/**
 * The single exception for a level file with a format error. It only carries what went wrong:
 * the broken rule, the 1-based line of the file (counting header and "---") and an optional detail.
 * A missing file is not a format error: that is LevelNotFoundException.
 */
public class InvalidLevelException extends Exception {
    /** Line of a global rule that is not tied to one line of the file. */
    public static final int NO_LINE = -1;
    private static final int FIRST_LINE = 1;
    private static final String PREFIX = "Nivel inválido: ";

    private final LevelFormatRule rule;
    private final int line;
    private final String detail;

    /** A global rule, not tied to one line. */
    public InvalidLevelException(LevelFormatRule rule) {
        this(NO_LINE, rule, null);
    }

    /** A global rule with extra detail (for example, which box type does not match). */
    public InvalidLevelException(LevelFormatRule rule, String detail) {
        this(NO_LINE, rule, detail);
    }

    /** @param line 1-based line of the file */
    public InvalidLevelException(LevelFormatRule rule, int line) {
        this(rule, line, null);
    }

    /**
     * @param line   1-based line of the file
     * @param detail extra information, for example the character that was found
     */
    public InvalidLevelException(LevelFormatRule rule, int line, String detail) {
        this(fileLine(line), rule, detail);
    }

    private InvalidLevelException(int line, LevelFormatRule rule, String detail) {
        super(messageOf(Objects.requireNonNull(rule, "rule"), line, detail));
        this.rule = rule;
        this.line = line;
        this.detail = detail;
    }

    /**
     * @deprecated free-text rule kept only until every validation moves to {@link LevelFormatRule}.
     * {@link #getRule()} returns null for these exceptions.
     */
    @Deprecated
    public InvalidLevelException(String message, int line) {
        super(line > 0 ? message + " (line " + line + ")" : message);
        this.rule = null;
        this.line = line > 0 ? line : NO_LINE;
        this.detail = null;
    }

    public LevelFormatRule getRule() {
        return rule;
    }

    /** 1-based line of the file, or {@link #NO_LINE} for a global rule. */
    public int getLine() {
        return line;
    }

    public boolean hasLine() {
        return line != NO_LINE;
    }

    /** Extra information about the error, or null if there is none. */
    public String getDetail() {
        return detail;
    }

    /** A line passed explicitly must be a real line of the file; global rules use the constructors without line. */
    private static int fileLine(int line) {
        if (line < FIRST_LINE) {
            throw new IllegalArgumentException("Lines start at " + FIRST_LINE + ", got " + line);
        }
        return line;
    }

    /** The only place that builds the text: "Nivel inválido: <rule>[: <detail>][ (línea N)]". */
    private static String messageOf(LevelFormatRule rule, int line, String detail) {
        StringBuilder message = new StringBuilder(PREFIX).append(rule.description());
        if (detail != null) {
            message.append(": ").append(detail);
        }
        if (line != NO_LINE) {
            message.append(" (línea ").append(line).append(')');
        }
        return message.toString();
    }
}

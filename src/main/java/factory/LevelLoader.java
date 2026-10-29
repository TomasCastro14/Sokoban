package factory;

import exception.InvalidLevelException;
import exception.LevelNotFoundException;
import model.board.Direction;
import model.board.Position;
import model.box.BoxType;
import model.box.KeyBox;
import model.box.NormalBox;
import model.box.Player;
import model.game.Level;
import model.tile.Floor;
import model.tile.Ice;
import model.tile.StoragePoint;
import model.tile.Inaccessible;
import port.LevelSource;
import service.ClasspathLevelSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Builder director: reads the level text and tells the builder what to create. */
public class LevelLoader {
    private static final String SEPARATOR = "---";
    /** Rule reported when a map character is not in the format (the creators map is the only source). */
    static final String UNKNOWN_CHARACTER = "Unknown character";

    private final Map<Character, CellCreator> creators = new HashMap<>();
    private final HeaderParser headerParser = new HeaderParser();
    private final LevelSource source;

    /** Reads the game levels from the classpath (levels/nivelNN.txt). */
    public LevelLoader() {
        this(new ClasspathLevelSource());
    }

    public LevelLoader(LevelSource source) {
        this.source = source;
        register("#", new WallCreator());
        register("%", (p, symbol) -> new CellContent(new Inaccessible(), null));
        register(" ", (p, symbol) -> new CellContent(new Floor(), null));
        register("@", (p, symbol) -> new CellContent(new Floor(), new Player(Direction.DOWN)));
        register("$", (p, symbol) -> new CellContent(new Floor(), new NormalBox()));
        register("K", (p, symbol) -> new CellContent(new Floor(), new KeyBox()));
        register(WeakBoxCreator.symbols(), new WeakBoxCreator());
        register(".", (p, symbol) -> new CellContent(new StoragePoint(BoxType.NORMAL), null));
        register(":", (p, symbol) -> new CellContent(new StoragePoint(BoxType.WEAK), null));
        register(";", (p, symbol) -> new CellContent(new StoragePoint(BoxType.KEY), null));
        register("~", (p, symbol) -> new CellContent(new Ice(), null));
        register("abcde", new KeyTileCreator());
        register("ABCDE", new DoorCreator());
        register("PQRS", new PortalCreator());
    }

    private void register(String symbols, CellCreator creator) {
        for (char symbol : symbols.toCharArray()) {
            creators.put(symbol, creator);
        }
    }

    /** Builds level n from the lines its source gives (1-based). */
    public Level load(int levelNumber) throws LevelNotFoundException, InvalidLevelException {
        return parse(source.lines(levelNumber));
    }

    public boolean exists(int levelNumber) {
        return source.exists(levelNumber);
    }

    /** Parses the full text of a level file: header, "---" separator and map. */
    public Level parse(String text) throws InvalidLevelException {
        return parse(text.lines().toList());
    }

    private Level parse(List<String> lines) throws InvalidLevelException {
        int separator = lines.indexOf(SEPARATOR);
        if (separator < 0) {
            throw new InvalidLevelException("Missing '" + SEPARATOR + "' between header and map", -1);
        }
        LevelBuilder builder = new LevelBuilder().withMapStartLine(separator + 2); // 1-based line after "---"
        builder.withHeader(headerParser.parse(lines.subList(0, separator)));
        parseMap(lines, separator + 1, builder);
        return builder.build();
    }

    /** Map rows start right after the separator; error lines are 1-based lines of the file. */
    private void parseMap(List<String> lines, int firstRow, LevelBuilder builder) throws InvalidLevelException {
        for (int i = firstRow; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int col = 0; col < line.length(); col++) {
                char symbol = line.charAt(col);
                CellCreator creator = creators.get(symbol);
                if (creator == null) {
                    throw new InvalidLevelException(UNKNOWN_CHARACTER + " '" + symbol + "'", i + 1);
                }
                Position position = new Position(i - firstRow, col);
                builder.addCell(position, creator.create(position, symbol));
            }
        }
    }
}

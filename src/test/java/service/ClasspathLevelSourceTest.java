package service;

import exception.LevelNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Each folder under /level-source holds a tiny nivel01.txt named after what it tests. */
class ClasspathLevelSourceTest {
    private static final List<String> BASIC_LINES = List.of(
            "nombre: Nivel basico", "tiempo: 90", "objetivo: 500", "---", "#####", "#@$.#", "#####");

    private static ClasspathLevelSource sourceIn(String folder) {
        return new ClasspathLevelSource("/level-source/" + folder + "/nivel%02d.txt");
    }

    @Test
    @DisplayName("1. Lee un nivel del classpath y devuelve sus lineas completas y en orden")
    void readsAllLinesInOrder() throws LevelNotFoundException {
        assertEquals(BASIC_LINES, sourceIn("basic").lines(1));
    }

    @Test
    @DisplayName("2. La cantidad de lineas, la primera y la ultima coinciden con el archivo")
    void firstLastAndCountMatch() throws LevelNotFoundException {
        List<String> lines = sourceIn("basic").lines(1);

        assertEquals(7, lines.size());
        assertEquals("nombre: Nivel basico", lines.get(0));
        assertEquals("#####", lines.get(lines.size() - 1));
    }

    @Test
    @DisplayName("3. Con finales de linea de Windows (\\r\\n) ninguna linea termina en \\r")
    void windowsLineEndingsAreRemoved() throws LevelNotFoundException {
        List<String> lines = sourceIn("crlf").lines(1);

        assertEquals(BASIC_LINES, lines);
        assertTrue(lines.stream().noneMatch(line -> line.endsWith("\r")));
    }

    @Test
    @DisplayName("4. Los caracteres no ASCII del encabezado se leen bien (UTF-8)")
    void readsUtf8() throws LevelNotFoundException {
        assertEquals("nombre: Año de la ñandú con tildes: áéíóú", sourceIn("utf8").lines(1).get(0));
    }

    @Test
    @DisplayName("5. Si el archivo trae BOM, la primera linea no arranca con el BOM")
    void bomIsRemoved() throws LevelNotFoundException {
        assertEquals(BASIC_LINES, sourceIn("bom").lines(1));
    }

    @Test
    @DisplayName("6. Pedir un nivel que no existe lanza LevelNotFoundException diciendo que se busco")
    void missingLevelIsReported() {
        LevelNotFoundException e = assertThrows(LevelNotFoundException.class, () -> sourceIn("basic").lines(7));

        assertTrue(e.getMessage().contains("7"), e.getMessage());
        assertTrue(e.getMessage().contains("/level-source/basic/nivel07.txt"), e.getMessage());
    }

    @Test
    @DisplayName("7. Un numero de nivel menor a 1 falla enseguida con un mensaje claro")
    void levelNumberMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> sourceIn("basic").lines(0));
        assertThrows(IllegalArgumentException.class, () -> sourceIn("basic").exists(-1));
    }

    @Test
    @DisplayName("8. Un patron de recurso null falla enseguida")
    void nullPatternIsRejected() {
        assertThrows(NullPointerException.class, () -> new ClasspathLevelSource(null));
    }

    @Test
    @DisplayName("9. exists dice si el nivel esta en el classpath, sin leerlo")
    void existsChecksTheClasspath() {
        assertTrue(sourceIn("basic").exists(1));
        assertFalse(sourceIn("basic").exists(2));
    }

    @Test
    @DisplayName("10. La fuente por defecto busca levels/nivelNN.txt")
    void defaultConventionIsLevelsFolder() {
        LevelNotFoundException e = assertThrows(LevelNotFoundException.class, () -> new ClasspathLevelSource().lines(42));

        assertTrue(e.getMessage().contains("/levels/nivel42.txt"), e.getMessage());
    }
}

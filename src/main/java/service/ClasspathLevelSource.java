package service;

import exception.LevelNotFoundException;
import port.LevelSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Pure Fabrication: reads levels as classpath resources, so it works the same from the IDE,
 * the tests and a .jar. It only reads text; it knows nothing about the level format.
 */
public class ClasspathLevelSource implements LevelSource {
    /** Single place for the level naming convention: levels/nivel01.txt, levels/nivel02.txt, ... */
    public static final String DEFAULT_PATTERN = "/levels/nivel%02d.txt";
    private static final String BOM = "﻿";

    private final String resourcePattern;

    public ClasspathLevelSource() {
        this(DEFAULT_PATTERN);
    }

    /** @param resourcePattern absolute resource path with a %d (or %02d) for the level number */
    public ClasspathLevelSource(String resourcePattern) {
        this.resourcePattern = Objects.requireNonNull(resourcePattern, "resourcePattern");
    }

    @Override
    public List<String> lines(int levelNumber) throws LevelNotFoundException {
        String resource = resourceOf(levelNumber);
        try (InputStream in = ClasspathLevelSource.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new LevelNotFoundException("Level " + levelNumber + " not found: " + resource);
            }
            return withoutBom(new String(in.readAllBytes(), StandardCharsets.UTF_8)).lines().toList();
        } catch (IOException e) {
            throw new LevelNotFoundException("Could not read level " + levelNumber + ": " + resource, e);
        }
    }

    @Override
    public boolean exists(int levelNumber) {
        return ClasspathLevelSource.class.getResource(resourceOf(levelNumber)) != null;
    }

    private String resourceOf(int levelNumber) {
        if (levelNumber < 1) {
            throw new IllegalArgumentException("Level numbers start at 1, got " + levelNumber);
        }
        return String.format(resourcePattern, levelNumber);
    }

    private static String withoutBom(String text) {
        return text.startsWith(BOM) ? text.substring(BOM.length()) : text;
    }
}

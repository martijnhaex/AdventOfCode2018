package be.jidoka.adventofcode.puzzle;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public abstract class PuzzleWithInputFile<T> implements Puzzle<T> {

    protected Stream<String> streamPuzzleInput(String fileName) {
        return readContentOfInputFile(fileName).stream();
    }

    private List<String> readContentOfInputFile(String fileName) {
        try {
            return Files.readAllLines(Paths.get(this.getClass().getClassLoader().getResource(fileName).toURI()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot read input file for this puzzle!");
        }
    }
}

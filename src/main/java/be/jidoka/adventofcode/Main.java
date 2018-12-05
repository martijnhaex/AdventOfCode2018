package be.jidoka.adventofcode;

import be.jidoka.adventofcode.puzzle.Puzzle;
import be.jidoka.adventofcode.puzzle.day.PuzzleDayOne;
import be.jidoka.adventofcode.puzzle.day.PuzzleDayTwo;

import java.util.Map;
import java.util.Optional;

public class Main {
    private static final Map<Integer, Puzzle> PUZZLES;

    public static void main(String[] args) {
        Puzzle puzzle = loadPuzzle(Integer.valueOf(args[0]));
        System.out.println("Your puzzle answer for Part One is " + puzzle.solvePartOne());
        System.out.println("Your puzzle answer for Part Two is " + puzzle.solvePartTwo());
    }

    private static Puzzle loadPuzzle(Integer dayOfAdvent) {
        return Optional.ofNullable(PUZZLES.get(dayOfAdvent))
                .orElseThrow(() -> new IllegalArgumentException("Cannot load puzzle for day <" + dayOfAdvent + "> of advent!"));
    }

    static {
        PUZZLES = Map.ofEntries(
                Map.entry(1, new PuzzleDayOne()),
                Map.entry(2, new PuzzleDayTwo())
        );
    }
}

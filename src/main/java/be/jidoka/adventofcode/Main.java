package be.jidoka.adventofcode;

import be.jidoka.adventofcode.puzzle.Puzzle;
import be.jidoka.adventofcode.puzzle.day.PuzzleDayEleven;
import be.jidoka.adventofcode.puzzle.day.PuzzleDayFive;
import be.jidoka.adventofcode.puzzle.day.PuzzleDayFour;
import be.jidoka.adventofcode.puzzle.day.PuzzleDayOne;
import be.jidoka.adventofcode.puzzle.day.PuzzleDaySix;
import be.jidoka.adventofcode.puzzle.day.PuzzleDayTen;
import be.jidoka.adventofcode.puzzle.day.PuzzleDayThree;
import be.jidoka.adventofcode.puzzle.day.PuzzleDayTwo;

import java.util.Map;
import java.util.Optional;

public class Main {
    private static final Map<Integer, Puzzle> PUZZLES;

    public static void main(String[] args) {
        Integer day = Integer.valueOf(args[0]);

        System.out.println("Advent of Code --- Day " + day);

        Puzzle puzzle = loadPuzzle(day);
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
                Map.entry(2, new PuzzleDayTwo()),
                Map.entry(3, new PuzzleDayThree()),
                Map.entry(4, new PuzzleDayFour()),
                Map.entry(5, new PuzzleDayFive()),
                Map.entry(6, new PuzzleDaySix()),
                Map.entry(10, new PuzzleDayTen()),
                Map.entry(11, new PuzzleDayEleven())
        );
    }
}

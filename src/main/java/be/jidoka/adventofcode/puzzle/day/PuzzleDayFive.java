package be.jidoka.adventofcode.puzzle.day;

import be.jidoka.adventofcode.puzzle.PuzzleWithInputFile;

import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class PuzzleDayFive extends PuzzleWithInputFile<Integer> {

    @Override
    public Integer solvePartOne() {
        return readPolymer()
                .removeReactions()
                .length();
    }

    @Override
    public Integer solvePartTwo() {
        return readPolymer()
                .getShortestFormPossible()
                .length();
    }

    private Polymer readPolymer() {
        return streamPuzzleInput("puzzleDayFive.txt")
                .map(Polymer::new)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private static class Polymer {

        private final String value;

        public Polymer(String value) {
            this.value = value;
        }

        public String removeReactions() {
            return removeAllReactions(value);
        }

        public String getShortestFormPossible() {
            return analyzeUnits()
                    .stream()
                    .map(unit -> removeAllReactions(value.replaceAll("(?i)" + unit, "")))
                    .min(comparingInt(String::length))
                    .get();
        }

        private String removeAllReactions(String polymer) {
            String polymerWithoutReactions = removeFirstReaction(polymer);
            while (true) {
                String currentPolymerWithoutReactions = removeFirstReaction(polymerWithoutReactions);
                if (polymerWithoutReactions.equals(currentPolymerWithoutReactions)) {
                    break;
                }
                polymerWithoutReactions = currentPolymerWithoutReactions;
            }
            return polymerWithoutReactions;
        }

        private String removeFirstReaction(String polymer) {
            StringBuffer polymerBuffer = new StringBuffer(polymer);
            for (int i = 0; i <= polymerBuffer.length() - 2; i++) {
                if (polymerBuffer.charAt(i) != polymerBuffer.charAt(i + 1) && Character.toLowerCase(polymerBuffer.charAt(i)) == Character.toLowerCase(polymerBuffer.charAt(i + 1))) {
                    polymerBuffer.deleteCharAt(i + 1);
                    polymerBuffer.deleteCharAt(i);
                    break;
                }
            }
            return polymerBuffer.toString();
        }

        private List<String> analyzeUnits() {
            return Arrays.stream(value.toLowerCase().split(""))
                    .distinct()
                    .collect(toList());
        }
    }
}

package be.jidoka.adventofcode.puzzle.day;

import be.jidoka.adventofcode.puzzle.PuzzleWithInputFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class PuzzleDayTwo extends PuzzleWithInputFile<String> {

    @Override
    public String solvePartOne() {
        return readInput()
                .map(CheckSumPart::new)
                .flatMap(CheckSumPart::getCheckSumValues)
                .collect(groupingBy(identity(), counting()))
                .values()
                .stream()
                .reduce(1L, (a, b) -> a * b)
                .toString();
    }

    @Override
    public String solvePartTwo() {
        List<CheckSumPart> checkSumParts = readInput().map(CheckSumPart::new).collect(toList());

        Map<Integer, Set<String>> similarityScores = new HashMap<>();
        for (int i = 0; i < checkSumParts.size(); i++) {
            CheckSumPart checkSumPart = checkSumParts.get(i);
            for (int j = 0; j < checkSumParts.size(); j++) {
                if (i != j) {
                    CheckSumPart otherChecksumPart = checkSumParts.get(j);

                    Integer similarityScore = checkSumPart.getSimilarityScore(otherChecksumPart);
                    if (!similarityScores.containsKey(similarityScore)) {
                        similarityScores.put(similarityScore, new HashSet<>());
                    }
                    similarityScores.get(similarityScore).add(checkSumPart.getValue());
                    similarityScores.get(similarityScore).add(otherChecksumPart.getValue());
                }
            }
        }

        return similarityScores
                .entrySet()
                .stream()
                .max(comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .map(SimilarityScore::new)
                .map(SimilarityScore::getSimilarity)
                .get();
    }

    private Stream<String> readInput() {
        return streamPuzzleInput("puzzleDayTwo.txt");
    }

    private static class CheckSumPart {

        private final String checkSumPart;
        private final boolean hasTwoLetterMatch;
        private final boolean hasThreeLetterMatch;

        protected CheckSumPart(String checkSumPart) {
            Set<Long> checkSumPartAnalysis = analyze(checkSumPart);

            this.checkSumPart = checkSumPart;
            this.hasTwoLetterMatch = checkSumPartAnalysis.contains(2L);
            this.hasThreeLetterMatch = checkSumPartAnalysis.contains(3L);
        }

        protected String getValue() {
            return checkSumPart;
        }

        protected Stream<Integer> getCheckSumValues() {
            if (hasTwoLetterMatch && hasThreeLetterMatch) {
                return Stream.of(2, 3);
            } else if (hasTwoLetterMatch) {
                return Stream.of(2);
            } else if (hasThreeLetterMatch) {
                return Stream.of(3);
            }
            return Stream.of();
        }

        protected Integer getSimilarityScore(CheckSumPart checkSumPart) {
            return new SimilarityScore(this.getValue(), checkSumPart.getValue()).getScore();
        }

        private Set<Long> analyze(String checkSumPart) {
            return new HashSet<>(Arrays.stream(checkSumPart.split(""))
                    .collect(groupingBy(identity(), counting()))
                    .values());
        }
    }

    private static class SimilarityScore {

        private final String value;
        private final String otherValue;

        protected SimilarityScore(String value, String otherValue) {
            this.value = value;
            this.otherValue = otherValue;
        }

        protected SimilarityScore(Set<String> values) {
            if (values.size() != 2) {
                throw new IllegalStateException("Excepted only two values for a SimilarityScore!");
            }

            Iterator<String> valuesIterator = values.iterator();
            this.value = valuesIterator.next();
            this.otherValue = valuesIterator.next();
        }

        protected Integer getScore() {
            return getSimilarity().length();
        }

        protected String getSimilarity() {
            String[] parts = value.split("");
            String[] otherParts = otherValue.split("");

            StringBuilder reason = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals(otherParts[i])) {
                    reason.append(parts[i]);
                }
            }
            return reason.toString();
        }
    }
}

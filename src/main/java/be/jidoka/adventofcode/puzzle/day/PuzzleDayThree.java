package be.jidoka.adventofcode.puzzle.day;

import be.jidoka.adventofcode.puzzle.PuzzleWithInputFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class PuzzleDayThree extends PuzzleWithInputFile<Long> {

    @Override
    public Long solvePartOne() {
        return getOverlappingInches().count();
    }

    @Override
    public Long solvePartTwo() {
        List<Inch> overlappingInches = getOverlappingInches().map(Map.Entry::getKey).collect(toList());

        return readInput()
                .filter(c -> !c.contains(overlappingInches))
                .map(Claim::getId)
                .findFirst()
                .get();
    }

    private Stream<Claim> readInput() {
        return streamPuzzleInput("puzzleDayThree.txt").map(Claim::new);
    }

    private Stream<Map.Entry<Inch, Long>> getOverlappingInches() {
        return readInput()
                .flatMap(Claim::getClaimedInches)
                .collect(groupingBy(identity(), counting()))
                .entrySet()
                .stream()
                .filter(x -> x.getValue() > 1L);
    }

    private static class Claim {

        private final Long id;
        private final Integer leftMargin;
        private final Integer topMargin;
        private final Integer width;
        private final Integer depth;

        protected Claim(String claim) {
            Pattern claimPattern = Pattern.compile("#(\\d*)[ ]@[ ](\\d*),(\\d*):[ ](\\d*)x(\\d*)");
            Matcher claimMatcher = claimPattern.matcher(claim);
            if (!claimMatcher.matches()) {
                throw new IllegalStateException("Expected a valid Claim!");
            }

            this.id = Long.valueOf(claimMatcher.group(1));
            this.leftMargin = Integer.valueOf(claimMatcher.group(2));
            this.topMargin = Integer.valueOf(claimMatcher.group(3));
            this.width = Integer.valueOf(claimMatcher.group(4));
            this.depth = Integer.valueOf(claimMatcher.group(5));
        }

        public Stream<Inch> getClaimedInches() {
            List<Inch> claimedInches = new ArrayList<>();
            for (int x = leftMargin + 1; x < leftMargin + width + 1; x++) {
                for (int y = topMargin + 1; y < topMargin + depth + 1; y++) {
                    claimedInches.add(new Inch(x, y));
                }
            }
            return claimedInches.stream();
        }

        public Long getId() {
            return id;
        }

        public boolean contains(List<Inch> otherInches) {
            Set<Inch> inches = getClaimedInches().collect(toSet());
            return otherInches.stream()
                    .anyMatch(inches::contains);
        }
    }

    private static class Inch {

        private final Integer x;
        private final Integer y;

        protected Inch(Integer x, Integer y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Inch inch = (Inch) o;
            return Objects.equals(x, inch.x) &&
                    Objects.equals(y, inch.y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return x + "," + y;
        }
    }
}

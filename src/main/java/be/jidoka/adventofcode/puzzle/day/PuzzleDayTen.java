package be.jidoka.adventofcode.puzzle.day;

import be.jidoka.adventofcode.puzzle.PuzzleWithInputFile;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class PuzzleDayTen extends PuzzleWithInputFile<String> {

    @Override
    public String solvePartOne() {
        return createHeaven().getAlignedStarsRepresentation();
    }

    @Override
    public String solvePartTwo() {
        return createHeaven().getSecondsToAlignStars().toString();
    }

    private Heaven createHeaven() {
        return new Heaven(readStars().collect(toList()));
    }

    private Stream<Stars> readStars() {
        return streamPuzzleInput("puzzleDayTen.txt")
                .map(Stars::new);
    }

    private static class Stars {

        private Coordinate coordinate;
        private final Velocity velocity;

        public Stars(String content) {
            Pattern contentPattern = Pattern.compile("^position=<([ \\-\\d]*),([ \\-\\d]*)> velocity=<([ \\-\\d]*),([ \\-\\d]*)>$");
            Matcher contentMatcher = contentPattern.matcher(content);
            if (!contentMatcher.matches()) {
                throw new IllegalStateException("Expected a valid content!");
            }

            this.coordinate = new Coordinate(Integer.valueOf(contentMatcher.group(1).trim()), Integer.valueOf(contentMatcher.group(2).trim()));
            this.velocity = new Velocity(Integer.valueOf(contentMatcher.group(3).trim()), Integer.valueOf(contentMatcher.group(4).trim()));
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }

        public void move() {
            this.coordinate = new Coordinate(
                    coordinate.getX() + velocity.getHorizontalMovementSpeed(),
                    coordinate.getY() + velocity.getVerticalMovementSpeed()
            );
        }
    }

    private static class Coordinate {

        private final Integer x;
        private final Integer y;

        public Coordinate(Integer x, Integer y) {
            this.x = x;
            this.y = y;
        }

        public Integer getX() {
            return x;
        }

        public Integer getY() {
            return y;
        }
    }

    private static class Velocity {

        private final Integer horizontalMovementSpeed;
        private final Integer verticalMovementSpeed;

        public Velocity(Integer horizontalMovementSpeed, Integer verticalMovementSpeed) {
            this.horizontalMovementSpeed = horizontalMovementSpeed;
            this.verticalMovementSpeed = verticalMovementSpeed;
        }

        public Integer getHorizontalMovementSpeed() {
            return horizontalMovementSpeed;
        }

        public Integer getVerticalMovementSpeed() {
            return verticalMovementSpeed;
        }
    }

    private static class Heaven {

        private final List<Stars> stars;

        public Heaven(List<Stars> stars) {
            this.stars = stars;
        }

        public String getAlignedStarsRepresentation() {
            Integer alignedHeight = calculateHeight();
            while (alignedHeight > 10) {
                moveStars();
                alignedHeight = calculateHeight();
            }
            return presentStars();
        }

        public Integer getSecondsToAlignStars() {
            Integer seconds = 0;
            Integer alignedHeight = calculateHeight();
            while (alignedHeight > 10) {
                moveStars();
                alignedHeight = calculateHeight();
                seconds++;
            }
            return seconds;
        }

        private Integer calculateHeight() {
            return determineMaximumYCoordinate() - determineMinimumYCoordinate();
        }

        private String presentStars() {
            Integer minimumXCoordinate = determineMinimumXCoordinate();
            Integer maximumXCoordinate = determineMaximumXCoordinate();
            Integer minimumYCoordinate = determineMinimumYCoordinate();
            Integer maximumYCoordinate = determineMaximumYCoordinate();

            StringBuilder humanReadableRepresentation = new StringBuilder();
            humanReadableRepresentation.append("\n");
            for (int y = minimumYCoordinate; y <= maximumYCoordinate; y++) {
                for (int x = minimumXCoordinate; x <= maximumXCoordinate; x++) {
                    if (hasPointFor(x, y)) {
                        humanReadableRepresentation.append("#");
                    } else {
                        humanReadableRepresentation.append(".");
                    }
                }
                humanReadableRepresentation.append("\n");
            }
            return humanReadableRepresentation.toString();
        }

        private boolean hasPointFor(Integer xCoordinate, Integer yCoordinate) {
            return stars.stream()
                    .anyMatch(stars -> stars.getCoordinate().getX().equals(xCoordinate) && stars.getCoordinate().getY().equals(yCoordinate));
        }

        private Integer determineMinimumXCoordinate() {
            return stars.stream()
                    .map(Stars::getCoordinate)
                    .mapToInt(Coordinate::getX)
                    .min()
                    .orElse(0);
        }

        private Integer determineMinimumYCoordinate() {
            return stars.stream()
                    .map(Stars::getCoordinate)
                    .mapToInt(Coordinate::getY)
                    .min()
                    .orElse(0);
        }

        private Integer determineMaximumXCoordinate() {
            return stars.stream()
                    .map(Stars::getCoordinate)
                    .mapToInt(Coordinate::getX)
                    .max()
                    .orElse(0);
        }

        private Integer determineMaximumYCoordinate() {
            return stars.stream()
                    .map(Stars::getCoordinate)
                    .mapToInt(Coordinate::getY)
                    .max()
                    .orElse(0);
        }

        private void moveStars() {
            stars.forEach(Stars::move);
        }
    }
}

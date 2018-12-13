package be.jidoka.adventofcode.puzzle.day;

import be.jidoka.adventofcode.puzzle.PuzzleWithInputFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;

public class PuzzleDayEleven extends PuzzleWithInputFile<String> {

    @Override
    public String solvePartOne() {
        return createGrid()
                .calculatePowerLevels()
                .startCellOfA3X3SquareWithMostPower();
    }

    @Override
    public String solvePartTwo() {
        return createGrid()
                .calculatePowerLevels()
                .startCellOfARandomSizedSquareWithMostPower();
    }

    private Grid createGrid() {
        return streamPuzzleInput("puzzleDayEleven.txt")
                .mapToInt(Integer::valueOf)
                .findFirst()
                .stream()
                .mapToObj(Grid::new)
                .findFirst()
                .get();
    }

    private static class Grid {
        private static final Integer GRID_SIZE = 300;

        private final Integer gridSerialNumber;
        private final Integer[][] representation;

        private Grid(Integer gridSerialNumber) {
            this.gridSerialNumber = gridSerialNumber;
            this.representation = new Integer[GRID_SIZE][GRID_SIZE];
        }

        public Grid calculatePowerLevels() {
            for (int x = 0; x < GRID_SIZE; x++) {
                for (int y = 0; y < GRID_SIZE; y++) {
                    representation[x][y] = calculatePowerLevel(x, y);
                }
            }
            return this;
        }

        public String startCellOfA3X3SquareWithMostPower() {
            return calculateMaximumPowerSourceForSquareSize(3)
                    .map(PowerSource::printCoordinates)
                    .orElse(null);
        }

        public String startCellOfARandomSizedSquareWithMostPower() {
            return IntStream.rangeClosed(1, GRID_SIZE)
                    .parallel()
                    .boxed()
                    .map(this::calculateMaximumPowerSourceForSquareSize)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .max(comparing(PowerSource::getPower))
                    .map(PowerSource::printCoordinatesWithSize)
                    .get();
        }

        private Optional<PowerSource> calculateMaximumPowerSourceForSquareSize(Integer squareSize) {
            List<PowerSource> powerSources = new ArrayList<>();
            for (int x = 0; x < GRID_SIZE - squareSize; x++) {
                for (int y = 0; y < GRID_SIZE - squareSize; y++) {
                    powerSources.add(new PowerSource(x, y, calculatePower(x, y, squareSize), squareSize));
                }
            }
            return powerSources.stream().max(comparing(PowerSource::getPower));
        }

        private Integer calculatePowerLevel(Integer xCoordinate, Integer yCoordinate) {
            Integer rackID = calculateRackID(xCoordinate);
            Integer startPowerLevel = calculateStartPowerLevel(rackID, yCoordinate);
            Integer hundredsDigit = extractHundredsDigitFrom((startPowerLevel + gridSerialNumber) * rackID);
            return hundredsDigit - 5;
        }

        private Integer calculateRackID(Integer xCoordinate) {
            return xCoordinate + 10;
        }

        private Integer calculateStartPowerLevel(Integer rackID, Integer yCoordinate) {
            return rackID * yCoordinate;
        }

        private Integer extractHundredsDigitFrom(Integer number) {
            if (number > 100) {
                char[] digits = String.valueOf(number).toCharArray();
                return digits[digits.length - 3] - '0';
            }
            return 0;
        }

        private Integer calculatePower(Integer xCoordinate, Integer yCoordinate, Integer squareSize) {
            Integer currentXCoordinate = null;
            Integer currentYCoordinate = null;
            Integer power = 0;
            for (int x = 0; x < squareSize; x++) {
                currentXCoordinate = xCoordinate + x;
                for (int y = 0; y < squareSize; y++) {
                    currentYCoordinate = yCoordinate + y;

                    if (isWithinBounds(currentXCoordinate, currentYCoordinate)) {
                        power += representation[currentXCoordinate][currentYCoordinate];
                    }
                }
            }
            return power;
        }

        private boolean isWithinBounds(Integer xCoordinate, Integer yCoordinate) {
            return xCoordinate < GRID_SIZE && yCoordinate < GRID_SIZE;
        }
    }

    private static class PowerSource {

        private final Integer xCoordinate;
        private final Integer yCoordinate;
        private final Integer power;
        private final Integer squareSize;

        private PowerSource(Integer xCoordinate, Integer yCoordinate, Integer power, Integer squareSize) {
            this.xCoordinate = xCoordinate;
            this.yCoordinate = yCoordinate;
            this.power = power;
            this.squareSize = squareSize;
        }

        public Integer getPower() {
            return power;
        }

        public String printCoordinates() {
            return String.format("%d,%d", xCoordinate, yCoordinate);
        }

        public String printCoordinatesWithSize() {
            return String.format("%d,%d,%d", xCoordinate, yCoordinate, squareSize);
        }
    }
}

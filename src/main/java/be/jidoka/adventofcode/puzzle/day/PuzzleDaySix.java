package be.jidoka.adventofcode.puzzle.day;

import be.jidoka.adventofcode.puzzle.PuzzleWithInputFile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.lang.Integer.MAX_VALUE;
import static java.util.stream.Collectors.toList;

public class PuzzleDaySix extends PuzzleWithInputFile<Integer> {

    @Override
    public Integer solvePartOne() {
        return getGrid().getLargestRegion();
    }

    @Override
    public Integer solvePartTwo() {
        return getGrid().getAmountOfRegionsWithinDistanceOf(10000);
    }

    private Grid getGrid() {
        return new Grid(readPoints().collect(toList()));
    }

    private Stream<Point> readPoints() {
        return streamPuzzleInput("puzzleDaySix.txt")
                .map(Point::new);
    }

    private static class Point {

        private final Integer id;
        private final Integer x;
        private final Integer y;

        public Point(String coordinate) {
            String[] coordinateSplit = coordinate.split(", ");

            this.id = PointIDGenerator.next();
            this.x = Integer.valueOf(coordinateSplit[0]);
            this.y = Integer.valueOf(coordinateSplit[1]);
        }

        public Point(Integer x, Integer y) {
            this.id = PointIDGenerator.next();
            this.x = x;
            this.y = y;
        }

        public Integer getId() {
            return id;
        }

        public Integer getX() {
            return x;
        }

        public Integer getY() {
            return y;
        }

        public Integer getManhattanDistance(Point other) {
            return Math.abs(getX() - other.getX()) + Math.abs(getY() - other.getY());
        }
    }

    private static class PointIDGenerator {
        private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

        public static Integer next() {
            return SEQUENCE.getAndIncrement();
        }
    }

    private static class Grid {

        private final List<Point> points;

        public Grid(List<Point> points) {
            this.points = points;
        }

        public Integer getLargestRegion() {
            return createRegions()
                    .values()
                    .stream()
                    .mapToInt(x -> x)
                    .max()
                    .getAsInt();
        }

        public Integer getAmountOfRegionsWithinDistanceOf(Integer allowedDistance) {
            Integer gridLengthX = getMaximumX() + 1;
            Integer gridLengthY = getMaximumY() + 1;

            Integer regionsWithinDistanceOf = 0;
            for (int x = 0; x < gridLengthX; x++) {
                for (int y = 0; y < gridLengthY; y++) {
                    if (getTotalDistanceTo(new Point(x, y)) < allowedDistance) {
                        regionsWithinDistanceOf++;
                    }
                }
            }
            return regionsWithinDistanceOf;
        }

        private Map<Integer, Integer> createRegions() {
            Integer gridLengthX = getMaximumX() + 1;
            Integer gridLengthY = getMaximumY() + 1;
            Integer[][] grid = new Integer[gridLengthX][gridLengthY];

            Map<Integer, Integer> regions = new HashMap<>();
            for (int x = 0; x < gridLengthX; x++) {
                for (int y = 0; y < gridLengthY; y++) {
                    Optional<Point> closestPoint = getPointClosestTo(new Point(x, y));

                    if (closestPoint.isPresent()) {
                        Integer closestPointId = closestPoint.get().getId();

                        grid[x][y] = closestPointId;

                        Integer regionSize = regions.getOrDefault(closestPointId, 0);
                        regions.put(closestPointId, regionSize + 1);
                    }
                }
            }
            return filterOutInfiniteRegions(regions, detectInfiniteRegions(grid, gridLengthX, gridLengthY));
        }

        private Set<Integer> detectInfiniteRegions(Integer[][] grid, Integer gridLengthX, Integer gridLengthY) {
            Set<Integer> infiniteRegions = new HashSet<>();

            for (int x = 0; x < gridLengthX; x++) {
                infiniteRegions.add(grid[x][0]);
                infiniteRegions.add(grid[x][gridLengthY - 1]);
            }

            for (int y = 0; y < gridLengthY; y++) {
                infiniteRegions.add(grid[0][y]);
                infiniteRegions.add(grid[gridLengthX - 1][y]);
            }

            return infiniteRegions;
        }

        private Map<Integer, Integer> filterOutInfiniteRegions(Map<Integer, Integer> regions, Set<Integer> infiniteRegions) {
            for (Integer infiniteRegion : infiniteRegions) {
                regions.remove(infiniteRegion);
            }
            return regions;
        }

        private Optional<Point> getPointClosestTo(Point other) {
            Point closestPoint = null;

            Integer shortestDistance = MAX_VALUE;
            for (Point point : points) {
                Integer distance = point.getManhattanDistance(other);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closestPoint = point;
                } else if (distance.equals(shortestDistance)) {
                    closestPoint = null;
                }
            }

            return Optional.ofNullable(closestPoint);
        }

        private Integer getTotalDistanceTo(Point other) {
            Integer totalDistance = 0;
            for (Point point : points) {
                totalDistance += point.getManhattanDistance(other);
            }
            return totalDistance;
        }

        private Integer getMaximumX() {
            return points.stream().mapToInt(Point::getX).max().getAsInt();
        }

        private Integer getMaximumY() {
            return points.stream().mapToInt(Point::getY).max().getAsInt();
        }
    }
}

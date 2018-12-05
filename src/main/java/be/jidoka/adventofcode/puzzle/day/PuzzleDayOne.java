package be.jidoka.adventofcode.puzzle.day;

import be.jidoka.adventofcode.puzzle.PuzzleWithInputFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class PuzzleDayOne extends PuzzleWithInputFile<Integer> {

    @Override
    public Integer solvePartOne() {
        return readInput().sum();
    }

    @Override
    public Integer solvePartTwo() {
        List<Integer> frequencies = readInput().boxed().collect(toList());

        Map<Integer, Integer> seenFrequencies = new HashMap<>();
        seenFrequencies.put(0, 1);

        Integer calibratedFrequency = calibrate(frequencies, seenFrequencies, 0);
        while (seenFrequencies.get(calibratedFrequency) == 1) {
            calibratedFrequency = calibrate(frequencies, seenFrequencies, calibratedFrequency);
        }

        return calibratedFrequency;
    }

    private IntStream readInput() {
        return streamPuzzleInput("puzzleDayOne.txt").mapToInt(Integer::valueOf);
    }

    private Integer calibrate(List<Integer> frequencies, Map<Integer, Integer> seenFrequencies, Integer startFrequency) {
        Integer calibratedFrequency = startFrequency;
        for (Integer frequency : frequencies) {
            calibratedFrequency += frequency;
            if (!seenFrequencies.containsKey(calibratedFrequency)) {
                seenFrequencies.put(calibratedFrequency, 1);
            } else {
                seenFrequencies.put(calibratedFrequency, seenFrequencies.get(calibratedFrequency) + 1);
                break;
            }
        }
        return calibratedFrequency;
    }
}

package be.jidoka.adventofcode.puzzle.day;

import be.jidoka.adventofcode.puzzle.PuzzleWithInputFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

public class PuzzleDayFour extends PuzzleWithInputFile<Integer> {

    @Override
    public Integer solvePartOne() {
        return getGuards()
                .stream()
                .sorted(comparing(Guard::getMinutesAsleep).reversed())
                .peek(guard -> System.out.println("[" + guard.getMinutesAsleep() + "] " + guard.getId() + " * " + guard.getMostAsleepInMinute() + " = " + (guard.getId() * guard.getMostAsleepInMinute())))
                .mapToInt(guard -> guard.getId() * guard.getMostAsleepInMinute())
                .findFirst()
                .getAsInt();
    }

    @Override
    public Integer solvePartTwo() {
        return getGuards()
                .stream()
                .sorted(comparing(Guard::getMaximumTimesAsleepInAMinute).reversed())
                .peek(guard -> System.out.println("[" + guard.getMaximumTimesAsleepInAMinute() + "] " + guard.getId() + " * " + guard.getMostAsleepInMinute() + " = " + (guard.getId() * guard.getMostAsleepInMinute())))
                .mapToInt(guard -> guard.getId() * guard.getMostAsleepInMinute())
                .findFirst()
                .getAsInt();
    }

    private List<Guard> getGuards() {
        List<Event> events = getEvents();

        Map<Integer, Guard> guards = new HashMap<>();
        Integer lastGuardId = null;
        for (Event event : events) {
            if (event.isStartOfShift()) {
                lastGuardId = event.getGuardId();
                if (!guards.containsKey(lastGuardId)) {
                    guards.put(lastGuardId, new Guard(lastGuardId));
                }
            }
            guards.get(lastGuardId).addEvent(event);
        }
        return new ArrayList<>(guards.values());
    }

    private List<Event> getEvents() {
        return readInput()
                .sorted(comparing(o -> o.dateTime))
                .collect(Collectors.toList());
    }

    private Stream<Event> readInput() {
        return streamPuzzleInput("puzzleDayFour.txt").map(Event::new);
    }

    private static class Event {

        private final String raw;
        private final LocalDateTime dateTime;
        private final EventType type;
        private final Integer guardId;

        public Event(String eventLog) {
            Pattern eventLogPattern = Pattern.compile("\\[(\\d{4}-\\d{2}-\\d{2}[ ]\\d{2}:\\d{2})\\][ ]([a-zA-Z0-9 #]*)");
            Matcher eventLogMatcher = eventLogPattern.matcher(eventLog);
            if (!eventLogMatcher.matches()) {
                throw new IllegalStateException("Expected a valid Event Log!");
            }

            this.raw = eventLog;
            this.dateTime = dateTimeFor(eventLogMatcher.group(1));
            this.type = eventTypeFor(eventLogMatcher.group(2));
            this.guardId = guardIdFor(eventLogMatcher.group(2));
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public Integer getGuardId() {
            return guardId;
        }

        public boolean isStartOfShift() {
            return type == EventType.START_SHIFT;
        }

        public boolean isAsleep() {
            return type == EventType.ASLEEP;
        }

        public boolean isAwake() {
            return type == EventType.WAKES_UP;
        }

        private LocalDateTime dateTimeFor(String dateTime) {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        private EventType eventTypeFor(String description) {
            switch (description) {
                case "falls asleep":
                    return EventType.ASLEEP;
                case "wakes up":
                    return EventType.WAKES_UP;
                default:
                    return EventType.START_SHIFT;
            }
        }

        private Integer guardIdFor(String description) {
            Pattern guardIdPattern = Pattern.compile("Guard #(\\d*) begins shift");
            Matcher guardIdMatcher = guardIdPattern.matcher(description);

            if (guardIdMatcher.matches()) {
                return Integer.valueOf(guardIdMatcher.group(1));
            }
            return null;
        }

        @Override
        public String toString() {
            return raw;
        }

        private enum EventType {
            ASLEEP, WAKES_UP, START_SHIFT
        }
    }

    private static class Guard {

        private final Integer id;
        private final List<Event> events;

        public Guard(Integer id) {
            this.id = id;
            this.events = new ArrayList<>();
        }

        public void addEvent(Event event) {
            events.add(event);
        }

        public Integer getId() {
            return id;
        }

        public Long getMinutesAsleep() {
            Long minutesAsleep = 0L;

            LocalDateTime sleptAt = null;
            for (Event event : events) {
                if (event.isAsleep()) {
                    sleptAt = event.getDateTime();
                } else if (event.isAwake()) {
                    minutesAsleep += ChronoUnit.MINUTES.between(sleptAt, event.getDateTime());
                    sleptAt = null;
                }
            }

            return minutesAsleep;
        }

        public Integer getMaximumTimesAsleepInAMinute() {
            return timesAsleepInMinutes()
                    .values()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);
        }

        public Integer getMostAsleepInMinute() {
            return timesAsleepInMinutes()
                    .entrySet()
                    .stream()
                    .max(comparing(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .orElse(0);
        }

        private Map<Integer, Integer> timesAsleepInMinutes() {
            Map<Integer, Integer> asleepInMinute = new HashMap<>();

            LocalDateTime sleptAt = null;
            for (Event event : events) {
                if (event.isAsleep()) {
                    sleptAt = event.getDateTime();
                } else if (event.isAwake()) {
                    sleepsBetween(sleptAt, event.getDateTime(), asleepInMinute);
                    sleptAt = null;
                }
            }
            return asleepInMinute;
        }

        private void sleepsBetween(LocalDateTime sleptAt, LocalDateTime wakesAt, Map<Integer, Integer> asleepInMinute) {
            LocalDateTime currentDateTime = sleptAt;
            while (currentDateTime.isBefore(wakesAt)) {
                if (!asleepInMinute.containsKey(currentDateTime.getMinute())) {
                    asleepInMinute.put(currentDateTime.getMinute(), 1);
                } else {
                    asleepInMinute.put(currentDateTime.getMinute(), asleepInMinute.get(currentDateTime.getMinute()) + 1);
                }
                currentDateTime = currentDateTime.plusMinutes(1);
            }
        }
    }
}

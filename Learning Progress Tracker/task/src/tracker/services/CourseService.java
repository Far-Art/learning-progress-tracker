package tracker.services;

import tracker.beans.Course;
import tracker.beans.Progress;
import tracker.beans.Student;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

// this singleton is eagerly loaded just for simplicity
public class CourseService implements ExitableService {
    private final static ConcurrentMap<Course, Progress> POPULARITY = new ConcurrentHashMap<>();
    private final static List<Course> COURSES = Arrays.stream(Course.values()).toList();
    private final static CourseService instance = new CourseService();
    private final InputService inputService = InputService.getInstance();
    private final StudentService studentService = StudentService.getInstance();
    private final String NA = "n/a";

    private CourseService() {
        COURSES.forEach(c -> POPULARITY.put(c, new Progress()));
    }

    public static CourseService getInstance() {
        return instance;
    }

    public void printCourseStatistics() {
        System.out.println("Type the name of a course to see details or 'back' to quit.");
        updateCourseStatistics();
        printByPopularity();
        printByActivity();
        printByHardness();

        while (true) {
            try {
                String input = inputService.getInput();
                if (input.equalsIgnoreCase(back)) {
                    break;
                }
                Course course = findCourse(input);
                printTopStudentsByCourse(course);

            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    private void printByPopularity() {
        Comparator<Map.Entry<Course, Integer>> comparator = Comparator.comparingInt(Map.Entry::getValue);
        Map<Course, Integer> enrollmentMap = new HashMap<>(Course.values().length);

        POPULARITY.forEach((k, v) -> {
            if (v.getAssignments() > 0) {
                enrollmentMap.put(k, enrollmentMap.getOrDefault(k, 0) + 1);
            }
        });

        String mostPopular;
        try {
            int byValue = enrollmentMap.entrySet().stream()
                    .sorted(Comparator.comparingInt(e -> e.getKey().ordinal()))
                    .max(comparator)
                    .orElseThrow(NoSuchElementException::new).getValue();

            mostPopular = enrollmentMap.entrySet().stream().filter(e -> e.getValue() == byValue).map(e -> e.getKey().getLabel()).collect(Collectors.joining(", "));
        } catch (NoSuchElementException e) {
            mostPopular = NA;
        }

        String leastPopular;
        try {
            String finalMostPopular = mostPopular;
            int byValue = enrollmentMap.entrySet().stream()
                    .sorted(Comparator.comparingInt(e -> e.getKey().ordinal()))
                    .filter(c -> !finalMostPopular.contains(c.getKey().getLabel()))
                    .min(comparator)
                    .orElseThrow(NoSuchElementException::new).getValue();

            leastPopular = enrollmentMap.entrySet().stream().filter(e -> e.getValue() == byValue).map(e -> e.getKey().getLabel()).collect(Collectors.joining(", "));
        } catch (NoSuchElementException e) {
            leastPopular = NA;
        }
        System.out.printf("Most popular: %s%n", mostPopular);
        System.out.printf("Least popular: %s%n", leastPopular);
    }

    private void printByActivity() {
        Comparator<Map.Entry<Course, Progress>> comparator = Comparator.comparingInt(e -> e.getValue().getAssignments());
        String mostActive;
        String leastActive;
        boolean hasValues = POPULARITY.values().stream().anyMatch(c -> c.getPoints() > 0);

        if (hasValues) {
            try {
                int byValue = POPULARITY.entrySet().stream()
                        .sorted(Comparator.comparingInt(e -> e.getKey().ordinal()))
                        .max(comparator)
                        .orElseThrow(NoSuchElementException::new).getValue().getAssignments();

                mostActive = POPULARITY.entrySet().stream().filter(e -> e.getValue().getAssignments() == byValue).map(e -> e.getKey().getLabel()).collect(Collectors.joining(", "));
            } catch (NoSuchElementException e) {
                mostActive = NA;
            }

            try {
                String finalMostActive = mostActive;
                int byValue = POPULARITY.entrySet().stream()
                        .sorted(Comparator.comparingInt(e -> e.getKey().ordinal()))
                        .filter(c -> !finalMostActive.contains(c.getKey().getLabel()))
                        .min(Comparator.comparingInt(e -> e.getValue().getAssignments()))
                        .orElseThrow(NoSuchElementException::new).getValue().getAssignments();

                leastActive = POPULARITY.entrySet().stream().filter(e -> e.getValue().getAssignments() == byValue).map(e -> e.getKey().getLabel()).collect(Collectors.joining(", "));
            } catch (NoSuchElementException e) {
                leastActive = NA;
            }
        } else {
            mostActive = NA;
            leastActive = NA;
        }

        System.out.printf("Highest activity: %s%n", mostActive);
        System.out.printf("Lowest activity: %s%n", leastActive);
    }

    private void printByHardness() {
        Comparator<Map.Entry<Course, Progress>> comparator = Comparator.comparingDouble(e -> calcAveragePerAssignment(e.getValue()));

        String hardest;
        try {
            double byValue = calcAveragePerAssignment(POPULARITY.entrySet().stream()
                    .sorted(Comparator.comparingInt(e -> e.getKey().ordinal()))
                    .filter(e -> e.getValue().getAssignments() > 0)
                    .min(comparator)
                    .orElseThrow(NoSuchElementException::new).getValue());

            hardest = POPULARITY.entrySet().stream().filter(e -> calcAveragePerAssignment(e.getValue()) == byValue).map(e -> e.getKey().getLabel()).collect(Collectors.joining(", "));
        } catch (NoSuchElementException e) {
            hardest = NA;
        }

        String easiest;
        try {
            String finalHardest = hardest;
            double byValue = calcAveragePerAssignment(POPULARITY.entrySet().stream()
                    .sorted(Comparator.comparingInt(e -> e.getKey().ordinal()))
                    .filter(e -> e.getValue().getAssignments() > 0)
                    .filter(e -> !finalHardest.contains(e.getKey().getLabel()))
                    .max(comparator)
                    .orElseThrow(NoSuchElementException::new).getValue());

            easiest = POPULARITY.entrySet().stream().filter(e -> calcAveragePerAssignment(e.getValue()) == byValue).map(e -> e.getKey().getLabel()).collect(Collectors.joining(", "));
        } catch (NoSuchElementException e) {
            easiest = NA;
        }
        System.out.printf("Easiest course: %s%n", easiest);
        System.out.printf("Hardest course: %s%n", hardest);
    }

    private void updateCourseStatistics() {
        resetStatistics();
        COURSES.forEach(c -> {
            studentService.getStudents().forEach(student -> {
                if (student.getCourses().containsKey(c)) {
                    POPULARITY.get(c).addPoints(student.getCourses().get(c).getPoints());
                    POPULARITY.get(c).addAssignments(student.getCourses().get(c).getAssignments());
                }
            });
        });
    }

    private void resetStatistics() {
        POPULARITY.values().forEach(v -> {
            v.setPoints(0);
            v.setAssignments(0);
        });
    }

    private double calcAveragePerAssignment(Progress progress) {
        return progress.getAssignments() == 0 ? 0 : 1.0 * progress.getPoints() / progress.getAssignments();
    }

    private void printTopStudentsByCourse(Course course) {
        List<Student> students = studentService.getStudents();
        Comparator<Student> comparator = Comparator.comparingInt((Student s) -> s.getCourses().getOrDefault(course, new Progress()).getPoints()).reversed().thenComparing(Student::getId);
        students.sort(comparator);
        System.out.println(course.getLabel());
        System.out.println("id    points completed");

        students.forEach(s -> {
            Progress progress = s.getCourses().getOrDefault(course, new Progress());
            if (progress.getPoints() > 0) {
                System.out.printf("%s %-6d %s%s%n", s.getId(), progress.getPoints(), calcCompletionPercent(s, course), "%");
            }
        });
    }

    private BigDecimal calcCompletionPercent(Student student, Course course) {
        if (student.getCourses().containsKey(course)) {
            Progress progress = student.getCourses().get(course);
            BigDecimal acquired = BigDecimal.valueOf(progress.getPoints());
            BigDecimal needed = BigDecimal.valueOf(course.getPassPoints());
            BigDecimal result = acquired.divide(needed, 16, RoundingMode.HALF_UP);
            result = result.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP);
            return result;
        }
        return BigDecimal.ZERO;
    }

    private Course findCourse(String course) {
        for (Course c : Course.values()) {
            if (c.getLabel().equalsIgnoreCase(course)) {
                return c;
            }
        }
        throw new InputMismatchException("Error! Unknown course!");
    }
}

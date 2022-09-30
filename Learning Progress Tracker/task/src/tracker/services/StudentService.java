package tracker.services;

import tracker.beans.Course;
import tracker.beans.Student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// this singleton is eagerly loaded just for simplicity
public class StudentService implements ExitableService {

    private final static StudentService instance = new StudentService();
    private final static ConcurrentMap<String, Student> STUDENTS = new ConcurrentHashMap<>();
    private final InputService inputService = InputService.getInstance();
    private final ValidatorService validator = ValidatorService.getInstance();

    private StudentService() {
    }

    public static StudentService getInstance() {
        return instance;
    }

    public void addStudents() {
        System.out.print("Enter student credentials or 'back' to return.");
        int studentsAdded = 0;
        while (true) {
            try {
                String input = inputService.getInput();
                if (input.equalsIgnoreCase(back)) {
                    break;
                }
                Student student = parseStudent(input);
                if (isEmailExist(student.getEmail())) {
                    throw new IllegalArgumentException("This email is already taken.");
                }
                STUDENTS.put(student.getId(), student);
                studentsAdded++;
            } catch (InputMismatchException | IllegalArgumentException e) {
                System.out.print(e.getMessage());
            }
        }
        System.out.printf("Total %d student%s have been added.", studentsAdded, studentsAdded == 1 ? "" : "s");
    }

    public void addPoints() {
        System.out.print("Enter an id and points or 'back' to return.");
        while (true) {
            try {
                String input = inputService.getInput();
                if (input.equalsIgnoreCase(back)) {
                    break;
                }
                String[] inputArr = input.split(" ");
                if (inputArr.length != 5) {
                    throw new InputMismatchException("Error: Incorrect points format!");
                }

                Arrays.stream(Arrays.copyOfRange(inputArr, 1, inputArr.length)).forEach(i -> {
                    if (!validator.onlyDigits(i) || Integer.parseInt(i) < 0) {
                        throw new InputMismatchException("Error: Incorrect points format!");
                    }
                });

                if (inputArr[0].equalsIgnoreCase(">")) {
                    if (!STUDENTS.containsKey(inputArr[0])) {
                        Student student = new Student();
                        student.setFirstName("name");
                        student.setLastName("lastName");
                        student.setEmail("email@email.comcom");
                        student.setId(">");
                        STUDENTS.put(student.getId(), student);
                    }

                }
                if (!STUDENTS.containsKey(inputArr[0])) {
                    throw new InputMismatchException(String.format("Error: No student is found for id=%s!", inputArr[0]));
                }

                updateStudentPoints(inputArr);
                System.out.print("Points updated.");
            } catch (InputMismatchException e) {
                System.out.print(e.getMessage());
            }
        }
    }

    public void findStudent() {
        System.out.print("Enter an id or 'back' to return.");
        while (true) {
            try {
                String input = inputService.getInput();
                if (input.equalsIgnoreCase(back)) {
                    break;
                }
                if (!STUDENTS.containsKey(input)) {
                    throw new InputMismatchException(String.format("Error: No student is found for id=%s!", input));
                }
                Student student = STUDENTS.get(input);

                System.out.printf("%s points:", input);
                student.getCourses().forEach((key, value) -> System.out.printf(" %s=%d;", key.getLabel(), value.getPoints()));
            } catch (InputMismatchException e) {
                System.out.print(e.getMessage());
            }

        }

    }

    public void printStudents() {
        if (STUDENTS.size() == 0) {
            System.out.print("No students found.");
        } else {
            System.out.println("Students:");
            STUDENTS.values().forEach(s -> System.out.println(s.getId()));
        }
    }

    public List<Student> getStudents() {
        return new ArrayList<>(STUDENTS.values());
    }

    public boolean isEmailExist(String email) {
        return STUDENTS.values().stream().anyMatch(e -> e.getEmail().equals(email));
    }


    private Student parseStudent(String input) {
        String[] credentials = input.split("\\s+");

        if (credentials.length < 3) {
            throw new InputMismatchException("Error: Incorrect credentials!");
        }

        String firstName = credentials[0];
        if (!validator.validateName(firstName)) {
            throw new InputMismatchException("Error: Incorrect first name!");
        }

        String[] lastNameArr = Arrays.copyOfRange(credentials, 1, credentials.length - 1);
        Arrays.stream(lastNameArr).forEach(e -> {
            if (!validator.validateName(e)) {
                throw new InputMismatchException("Error: Incorrect last name!");
            }
        });
        String lastName = String.join(" ", lastNameArr);

        String email = credentials[credentials.length - 1];
        if (!validator.validateEmail(email)) {
            throw new InputMismatchException("Error: Incorrect email!");
        }

        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);

        System.out.print("The student has been added.");
        return student;
    }

    private void updateStudentPoints(String[] values) {
        Student student = STUDENTS.get(values[0]);
        for (int i = 0; i < Course.values().length; i++) {
            Course course = Course.values()[i];
            int addedVal = Integer.parseInt(values[i + 1]);
            student.addCourseAssignment(course, addedVal);
        }
    }
}

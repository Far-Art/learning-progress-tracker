package tracker.services;

import tracker.beans.Command;

import java.util.InputMismatchException;

// this singleton is eagerly loaded just for simplicity
public class MainCommandService implements ExitableService {
    private final static MainCommandService instance = new MainCommandService();
    private final InputService inputService = InputService.getInstance();
    private final StudentService studentService = StudentService.getInstance();
    private final CourseService courseService = CourseService.getInstance();
    private final NotificationService notificationService = NotificationService.getInstance();
    private final String exit = "exit";
    private final String empty = "";
    private final String addStudents = "add students";
    private final String addPoints = "add points";
    private final String list = "list";

    private final String find = "find";

    private final String statistics = "statistics";

    private final String notify = "notify";

    private MainCommandService() {
    }

    public static MainCommandService getInstance() {
        return instance;
    }

    public boolean executeCommand() {
        while (true) {
            try {
                switch (parseCommand(inputService.getInput().toLowerCase())) {
                    case EXIT -> {
                        System.out.print("Bye!");
                        return false;
                    }
                    case ADD_STUDENTS -> {
                        studentService.addStudents();
                    }
                    case BACK -> {
                        System.out.print("Enter 'exit' to exit the program.");
                    }
                    case LIST -> {
                        studentService.printStudents();
                    }
                    case ADD_POINTS -> {
                        studentService.addPoints();
                    }
                    case FIND -> {
                        studentService.findStudent();
                    }
                    case STATISTICS -> {
                        courseService.printCourseStatistics();
                    }
                    case NOTIFY -> {
                        notificationService.notifyStudents();
                    }
                }
                break;
            } catch (InputMismatchException e) {
                System.out.print(e.getMessage());
            } catch (Exception e1) {
                System.out.print("Error: Something went wrong, try again!");
            }
        }
        return true;
    }


    private Command parseCommand(String input) {
        return switch (input) {
            case empty -> throw new InputMismatchException("Error: No input!");
            case exit -> Command.EXIT;
            case back -> Command.BACK;
            case addStudents -> Command.ADD_STUDENTS;
            case addPoints -> Command.ADD_POINTS;
            case list -> Command.LIST;
            case find -> Command.FIND;
            case statistics -> Command.STATISTICS;
            case notify -> Command.NOTIFY;
            default -> throw new InputMismatchException("Error: unknown command!");
        };
    }
}

package tracker.services;

import tracker.beans.Course;
import tracker.beans.Student;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// this singleton is eagerly loaded just for simplicity
public class NotificationService implements ExitableService {
    private final static NotificationService instance = new NotificationService();

    private final ConcurrentMap<Student, Set<Course>> notifiedStudents = new ConcurrentHashMap<>();
    private final StudentService studentService = StudentService.getInstance();

    public static NotificationService getInstance() {
        return instance;
    }

    public void notifyStudents() {
        Set<Student> notified = new HashSet<>();
        studentService.getStudents().forEach(student -> {
            student.getCourses().forEach((course, progress) -> {
                if (course.getPassPoints() <= progress.getPoints()) {
                    if (!notifiedCourse(student, course)) {
                        addCourseToNotified(student, course);
                        sendNotification(student, course);
                        notified.add(student);
                    }
                }
            });
        });
        System.out.printf("Total %d students have been notified.", notified.size());
    }

    private boolean notifiedCourse(Student student, Course course) {
        return notifiedStudents.containsKey(student) && notifiedStudents.get(student).contains(course);
    }

    private void addCourseToNotified(Student student, Course course) {
        Set<Course> studentCourses = notifiedStudents.getOrDefault(student, new HashSet<>());
        studentCourses.add(course);
        notifiedStudents.put(student, studentCourses);
    }

    private void sendNotification(Student student, Course course) {
        System.out.printf("To: %s%n", student.getEmail());
        System.out.println("Re: Your Learning Progress");
        System.out.printf("Hello, %s %s! You have accomplished our %s course!%n", student.getFirstName(), student.getLastName(), course.getLabel());
    }

}

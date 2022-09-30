package tracker.beans;

import java.util.LinkedHashMap;
import java.util.Map;

public class Student {

    private static int NEXT_ID = 10000;

    private final Map<Course, Progress> courses = new LinkedHashMap<>();

    private String id = String.valueOf(NEXT_ID++);

    private String firstName;

    private String lastName;
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addCourseAssignment(Course course, int points) {
        Progress progress = courses.getOrDefault(course, new Progress());
        if (points > 0) {
            progress.addPoints(points);
            progress.addAssignments(1);
        }
        courses.put(course, progress);
    }

    public Map<Course, Progress> getCourses() {
        return courses;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Student{");
        sb.append("id=").append(id);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

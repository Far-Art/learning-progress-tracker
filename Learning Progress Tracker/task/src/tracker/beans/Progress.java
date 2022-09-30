package tracker.beans;

public class Progress {

    private int assignments;
    private int points;

    public int getAssignments() {
        return assignments;
    }

    public void setAssignments(int assignments) {
        this.assignments = assignments;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void addAssignments(int assignments) {
        this.assignments += assignments;
    }

}

package tracker.beans;

public enum Course {
    JAVA("Java", 600),
    ALGORITHMS("DSA", 400),
    DATABASES("Databases", 480),
    SPRING("Spring", 550);

    private final String label;

    private final int passPoints;

    Course(String label, int passPoints) {
        this.label = label;
        this.passPoints = passPoints;
    }

    public int getPassPoints() {
        return passPoints;
    }

    public String getLabel() {
        return label;
    }
}

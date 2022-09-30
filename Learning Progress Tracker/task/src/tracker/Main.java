package tracker;

import tracker.services.MainCommandService;

public class Main {
    private final static String TITLE = "Learning Progress Tracker";

    private final MainCommandService commandService = MainCommandService.getInstance();

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        System.out.print(TITLE);
        boolean isRunning = true;
        while (isRunning) {
            isRunning = commandService.executeCommand();
        }
    }
}

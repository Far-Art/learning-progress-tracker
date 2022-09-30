package tracker.services;

import java.io.FilterInputStream;
import java.util.Scanner;

// this singleton is eagerly loaded just for simplicity
public class InputService {

    private final static InputService instance = new InputService();

    private InputService() {
    }

    public static InputService getInstance() {
        return instance;
    }

    public String getInput() {
        // System.in wrapped in FilterInputStream to prevent closing when scanner is closed
        // System.in cannot be reopened after being closed
        try (Scanner scanner = new Scanner(new FilterInputStream(System.in) {
            public void close() {
            }
        })) {
            return scanner.nextLine().strip();
        }
    }
}

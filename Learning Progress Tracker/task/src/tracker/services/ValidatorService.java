package tracker.services;

// this singleton is eagerly loaded just for simplicity
public class ValidatorService {

    private final static ValidatorService instance = new ValidatorService();

    // from http://emailregex.com/
    private final String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    private final String nameRegex = "^([a-zA-Z]+-[a-zA-Z]+)|([a-zA-Z]+)'?([a-zA-Z]+)(-(([a-zA-Z]+)'?([a-zA-Z]+)))?$";

    private final String onlyDigits = "^[0-9]+$";


    private ValidatorService() {
    }

    public static ValidatorService getInstance() {
        return instance;
    }

    public boolean validateEmail(String email) {
        return email.matches(emailRegex);
    }

    public boolean validateName(String name) {
        return name.matches(nameRegex);
    }

    public boolean onlyDigits(String str) {
        return str.matches(onlyDigits);
    }
}

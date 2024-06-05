package first_project.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean correctAccount(String provided) {
        Pattern accountPattern = Pattern.compile("^E\\d{13}$");
        Matcher accountMatcher = accountPattern.matcher(provided);
        return accountMatcher.matches();
    }
    public static boolean correctSum(String provided) {
        Pattern sumPattern = Pattern.compile("\\d+\\.?\\d*");
        Matcher sumMatcher = sumPattern.matcher(provided);
        return sumMatcher.matches();
    }
    public static boolean checkName(String provided) {
        String nameRegex = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
        Pattern namePattern = Pattern.compile(nameRegex);
        Matcher nameMatcher = namePattern.matcher(provided);

        return nameMatcher.matches();
    }
    public static boolean checkAddress(String provided) {
        String addressRegex = "^([a-zA-Z]+\\s){0,2}[a-zA-Z]+\\s\\d+-\\d+$";
        Pattern addressPattern = Pattern.compile(addressRegex);
        Matcher addressMatcher = addressPattern.matcher(provided);

        return addressMatcher.matches();
    }
    public static boolean checkPassword(String provided) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern passwordPattern = Pattern.compile(passwordRegex);
        Matcher passwordMatcher = passwordPattern.matcher(provided);

        return passwordMatcher.matches();
    }
    public static boolean checkUserID(String provided) {
        String idRegex = "^\\d{6}$";
        Pattern idPattern = Pattern.compile(idRegex);
        Matcher idMatcher = idPattern.matcher(provided);

        return idMatcher.matches();
    }
    public static boolean checkNum(String provided) {
        String numRegex = "^[123]$";
        Pattern numPattern = Pattern.compile(numRegex);
        Matcher numMatcher = numPattern.matcher(provided);

        return numMatcher.matches();
    }
}

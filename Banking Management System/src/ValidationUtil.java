import java.math.BigDecimal;

public class ValidationUtil {

    // Name: only letters and spaces
    public static String validateName(String name) {
        if (name == null || !name.matches("[a-zA-Z ]+")) {
            throw new IllegalArgumentException(
                    "Name must contain only letters and spaces.");
        }
        return name;
    }

    // Email: must end with @gmail.com and allow only letters,
    // numbers, and dots before @gmail.com
    public static String validateEmail(String email) {
        if (email == null ||
                !email.matches("^[A-Za-z0-9.]+@gmail\\.com$")) {
            throw new IllegalArgumentException(
                    "Email must end with @gmail.com and contain only letters, numbers, and dots before @.");
        }
        return email.toLowerCase();
    }

    public static String validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty.");
        }

        if (!username.matches("[a-zA-Z0-9_]{3,20}")) {
            throw new IllegalArgumentException(
                    "Username must be 3-20 characters and contain only letters, numbers, and underscores.");
        }

        return username.toLowerCase();
    }

    public static String validatePassword(String password) {
        if (password == null || password.length() < 8
                || !password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*\\d.*")
                || !password.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters, an uppercase letter, a lowercase letter, a digit, and a special character.");
        }

        return password;
    }

    // Phone: exactly 10 digits
    public static String validatePhone(String phone) {
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new IllegalArgumentException(
                    "Phone number must contain exactly 10 digits.");
        }
        return phone;
    }

    // Initial deposit: at least 500
    public static BigDecimal validateInitialDeposit(
            BigDecimal amount) {
        if (amount == null ||
                amount.compareTo(new BigDecimal("500")) < 0) {
            throw new IllegalArgumentException(
                    "Initial deposit must be at least 500.");
        }

        if (amount.scale() > 2) {
            throw new IllegalArgumentException(
                    "Initial deposit can have at most 2 decimal places.");
        }

        return amount;
    }

    public static void validateAmount(BigDecimal amount,
                                boolean allowZero)
            throws BankingException {

        if (amount == null || amount.scale() > 2
                || amount.signum() < 0
                || (!allowZero && amount.signum() == 0)) {
            throw new BankingException(
                    allowZero
                            ? "Initial deposit must be non-negative "
                            + "and have at most two decimal places."
                            : "Amount must be positive with at most "
                            + "two decimal places.");
        }
    }
}

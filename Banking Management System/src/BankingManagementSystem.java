import java.math.BigDecimal;
import java.util.Scanner;

public class BankingManagementSystem {

    private static final Scanner sc = new Scanner(System.in);
    private static final BankService bankService = new BankService();

    public static void main(String[] args) {

        String loggedInAccount = null;

        while (true) {
            try {
                if (loggedInAccount == null) {

                    System.out.println("\n===== BANKING SYSTEM =====");
                    System.out.println("1. Register");
                    System.out.println("2. Login");
                    System.out.println("3. Exit");

                    int choice = readInt("Enter your choice: ");

                    switch (choice) {
                        case 1 -> register();
                        case 2 -> loggedInAccount = login();
                        case 3 -> {
                            sc.close();
                            return;
                        }
                        default -> System.out.println("Invalid choice.");
                    }

                } else {

                    System.out.println("\n===== BANKING MENU =====");
                    System.out.println("1. Deposit");
                    System.out.println("2. Withdraw");
                    System.out.println("3. Fund Transfer");
                    System.out.println("4. Transaction History");
                    System.out.println("5. Check Balance");
                    System.out.println("6. Logout");

                    int choice = readInt("Enter your choice: ");

                    switch (choice) {
                        case 1 -> deposit(loggedInAccount);
                        case 2 -> withdraw(loggedInAccount);
                        case 3 -> transfer(loggedInAccount);
                        case 4 -> history(loggedInAccount);
                        case 5 -> balance(loggedInAccount);
                        case 6 -> {
                            loggedInAccount = null;
                            System.out.println("Logged out successfully.");
                        }
                        default -> System.out.println("Invalid choice.");
                    }
                }

            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }

    }

        private static void register () throws Exception {
            System.out.print("Enter customer name: ");
            String name = ValidationUtil.validateName(
                    sc.nextLine().trim());

            System.out.print("Enter email: ");
            String email = ValidationUtil.validateEmail(
                    sc.nextLine().trim());

            System.out.print("Enter username: ");
            String username = ValidationUtil.validateUsername(
                    sc.nextLine().trim());

            System.out.print("Enter password: ");
            String password = ValidationUtil.validatePassword(
                    sc.nextLine());

            System.out.print("Enter phone number: ");
            String phone = ValidationUtil.validatePhone(
                    sc.nextLine().trim());

            BigDecimal initialDeposit =
                    ValidationUtil.validateInitialDeposit(
                            readAmount("Enter initial deposit: "));


            bankService.createAccount(name, email, username, password, phone,
                    initialDeposit);
        }

        private static String login() throws Exception {
            System.out.print("Enter username: ");
            String username = sc.nextLine().trim();
            System.out.print("Enter password: ");
            String password = sc.nextLine().trim();

            return bankService.login(username,password);

        }

        private static void deposit (String loggedInAccount) throws Exception {

            BigDecimal amount = readAmount("Enter deposit amount: ");
            bankService.deposit(loggedInAccount, amount);
        }

        private static void withdraw (String loggedInAccount) throws Exception {

            BigDecimal amount =
                    readAmount("Enter withdrawal amount: ");

            bankService.withdraw(loggedInAccount, amount);
        }

        private static void transfer (String loggedInAccount) throws Exception {

            System.out.print("Enter destination account number: ");
            String to = sc.nextLine().trim();

            BigDecimal amount =
                    readAmount("Enter transfer amount: ");

            bankService.transfer(loggedInAccount, to, amount);
        }

        private static void history (String loggedInAccount) throws Exception {

            bankService.transactionHistory(loggedInAccount);
        }

        private static void balance (String loggedInAccount) throws Exception {
            bankService.showBalance(loggedInAccount);
        }

        private static BigDecimal readAmount(String message){
            System.out.print(message);
            return new BigDecimal(sc.nextLine().trim());
        }

        private static int readInt (String message){
            System.out.print(message);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                throw new BankingException("Enter a valid choice");
            }
        }
}

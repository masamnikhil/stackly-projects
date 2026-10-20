import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Map;

public class BankService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private String generateAccountNumber(Connection con)
            throws SQLException {

        String sql = """
        SELECT 1 FROM accounts
        WHERE account_number = ?
        """;

        for (int attempt = 0; attempt < 100; attempt++) {

            StringBuilder sb = new StringBuilder();

            // First digit: 1 to 9 (never 0).
            sb.append(RANDOM.nextInt(9) + 1);

            // Generate the remaining 9 digits: 0 to 9.
            for (int i = 0; i < 9; i++) {
                sb.append(RANDOM.nextInt(10));
            }

            String accountNumber = sb.toString();

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, accountNumber);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return accountNumber;
                    }
                }
            }
        }

        throw new SQLException(
                "Unable to generate a unique account number.");
    }

    public void createAccount(
            String name, String email, String username, String password,  String phone,
            BigDecimal initialDeposit) throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int customerId;

                String customerSql = """
                    INSERT INTO customers(name, username, email, password, phone)
                    VALUES (?, ?, ?, ?, ?)
                    """;

                try (PreparedStatement ps =
                             con.prepareStatement(
                                     customerSql,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    String hashedPassword =
                            BCrypt.hashpw(password, BCrypt.gensalt());

                    ps.setString(1, name);
                    ps.setString(2, username);
                    ps.setString(3, email);
                    ps.setString(4, hashedPassword);
                    ps.setString(5, phone);
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Customer ID was not generated");
                        }
                        customerId = rs.getInt(1);
                    }
                }

            String accountNumber = generateAccountNumber(con);

                String accountSql = """
                    INSERT INTO accounts
                        (customer_id, account_number, balance)
                    VALUES (?, ?, ?)
                    """;

                int accountId;

                try (PreparedStatement ps =
                             con.prepareStatement(
                                     accountSql,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, customerId);
                    ps.setString(2, accountNumber);
                    ps.setBigDecimal(3, initialDeposit);
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Account ID was not generated");
                        }
                        accountId = rs.getInt(1);
                    }
                }

                if (initialDeposit.signum() > 0) {
                    addHistory(
                            con, accountId, null,
                            "DEPOSIT", initialDeposit,
                            "SUCCESS", "Initial deposit");
                }

                con.commit();
                System.out.println("Account created successfully. Login now");

            } catch (Exception e) {
                rollback(con, e);
                throw e;
            }
            finally {
                con.setAutoCommit(true);
            }
        }
    }

    public String login(String username, String password)
            throws Exception {

        String sql = """
        SELECT c.password, c.name, c.username, a.account_number
        FROM customers c
        JOIN accounts a ON c.customer_id = a.customer_id
        WHERE c.username = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new BankingException(
                            "Invalid username.");
                }

                String passwordHash = rs.getString("password");

                if (!BCrypt.checkpw(password, passwordHash)) {
                    throw new BankingException(
                            "Invalid password.");
                }
                    System.out.println("\n========== CUSTOMER DETAILS ==========");
                    System.out.println("Account Number : " + rs.getString("account_number"));
                    System.out.println("Name           : " + rs.getString("name"));
                    System.out.println("Username       : " + rs.getString("username"));
                    System.out.println("======================================");

                    return rs.getString("account_number");
            }
        }
    }

    // Deposit money.
    public void deposit(String accountNumber,
                        BigDecimal amount) throws Exception {

        ValidationUtil.validateAmount(amount, false);

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            Map<String, ?> details = null;
            try {
                 details = getAccountId(
                        con, accountNumber, true);

                String sql = """
                    UPDATE accounts
                    SET balance = balance + ?
                    WHERE account_id = ?
                    """;

                try (PreparedStatement ps =
                             con.prepareStatement(sql)) {
                    ps.setBigDecimal(1, amount);
                    ps.setInt(2, (int) details.get("account_id"));
                    if (ps.executeUpdate() != 1) {
                        throw new BankingException(
                                "Debit failed.");
                    }
                }

                addHistory(con, (int) details.get("account_id"), null,
                        "DEPOSIT", amount, "SUCCESS",
                        "Cash deposit");

                con.commit();
                System.out.println("Deposit successful.");

            } catch (Exception e) {
                rollback(con, e);
                if (details != null) {
                    try {
                        addFailedHistory(
                                (int) details.get("account_id"),
                                null,
                                "DEPOSIT",
                                amount,
                                e.getMessage()
                        );
                    } catch (Exception historyException) {
                        e.addSuppressed(historyException);
                    }
                }
                throw e;
            }
            finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Withdraw money.
    public void withdraw(String accountNumber,
                         BigDecimal amount) throws Exception {

        ValidationUtil.validateAmount(amount, false);

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            Map<String, ?> details = null;

            try {
                details = getAccountId(
                        con, accountNumber, true);

                BigDecimal balance = getBalance(con, (int) details.get("account_id"));

                if (balance.compareTo(amount) < 0) {
                    throw new BankingException(
                            "Insufficient balance.");
                }

                String sql = """
                    UPDATE accounts
                    SET balance = balance - ?
                    WHERE account_id = ?
                    AND balance >= ?
                    """;

                try (PreparedStatement ps =
                             con.prepareStatement(sql)) {
                    ps.setBigDecimal(1, amount);
                    ps.setInt(2, (int) details.get("account_id"));
                    ps.setBigDecimal(3, amount);

                    if (ps.executeUpdate() != 1) {
                        throw new BankingException(
                                "Withdrawal failed.");
                    }
                }

                addHistory(con, (int) details.get("account_id"), null,
                        "WITHDRAWAL", amount, "SUCCESS",
                        "Cash withdrawal");

                con.commit();
                System.out.println("Withdrawal successful.");

            } catch (Exception e) {
                rollback(con, e);
                if (details != null) {
                    try {
                        addFailedHistory(
                                (int) details.get("account_id"),
                                null,
                                "WITHDRAWAL",
                                amount,
                                e.getMessage()
                        );
                    } catch (Exception historyException) {
                        e.addSuppressed(historyException);
                    }
                }
                throw e;
            }
            finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Transfer money atomically between two accounts.
    public void transfer(String fromAccount,
                         String toAccount,
                         BigDecimal amount) throws Exception {

        ValidationUtil.validateAmount(amount, false);

        if (fromAccount.equals(toAccount)) {
            throw new BankingException(
                    "Source and destination accounts must differ.");
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            Map<?, ?> fromDetails = null;
            Map<?, ?> toDetails = null;

            try {
                // Obtain account IDs before locking.
                fromDetails = getAccountId(
                        con, fromAccount, false);
                toDetails = getAccountId(
                        con, toAccount, false);

                // Always lock in ascending ID order to reduce
                // deadlocks when two transfers happen together.
                int firstId = Math.min((int) fromDetails.get("account_id"), (int) toDetails.get("account_id"));
                int secondId = Math.max((int) fromDetails.get("account_id"), (int) toDetails.get("account_id"));

                getBalance(con, firstId);
                getBalance(con, secondId);

                BigDecimal balance = getBalance(con, (int) fromDetails.get("account_id"));

                if (balance.compareTo(amount) < 0) {
                    throw new BankingException(
                            "Insufficient balance for transfer.");
                }

                String debitSql = """
                    UPDATE accounts
                    SET balance = balance - ?
                    WHERE account_id = ?
                    AND balance >= ?
                    """;

                try (PreparedStatement ps =
                             con.prepareStatement(debitSql)) {
                    ps.setBigDecimal(1, amount);
                    ps.setInt(2, (int) fromDetails.get("account_id"));
                    ps.setBigDecimal(3, amount);

                    if (ps.executeUpdate() != 1) {
                        throw new BankingException(
                                "Debit failed.");
                    }
                }

                // If this update fails, the debit is rolled back.
                String creditSql = """
                    UPDATE accounts
                    SET balance = balance + ?
                    WHERE account_id = ?
                    """;

                try (PreparedStatement ps =
                             con.prepareStatement(creditSql)) {
                    ps.setBigDecimal(1, amount);
                    ps.setInt(2, (int) toDetails.get("account_id"));

                    if (ps.executeUpdate() != 1) {
                        throw new BankingException(
                                "Credit failed.");
                    }
                }

                // Record both sides of the transfer.
                addHistory(con, (int) fromDetails.get("account_id"), (int) toDetails.get("account_id"),
                        "TRANSFER_OUT", amount, "SUCCESS",
                        "Transfer to " + toDetails.get("name"));

                addHistory(con, (int) toDetails.get("account_id"), (int) fromDetails.get("account_id"),
                        "TRANSFER_IN", amount, "SUCCESS",
                        "Transfer from " + fromDetails.get("name"));

                // Save both balance updates and history together.
                con.commit();

                System.out.println("Transfer successful.");

            } catch (Exception e) {
                rollback(con, e);
                if(fromDetails != null &&  toDetails != null) {
                    addFailedHistory((int) fromDetails.get("account_id"), (int) toDetails.get("account_id"),
                            "TRANSFER_OUT", amount,
                            "Transfer to " + toDetails.get("name") + " failed");

                    addFailedHistory((int) toDetails.get("account_id"), (int) fromDetails.get("account_id"),
                            "TRANSFER_IN", amount,
                            "Transfer from " + fromDetails.get("name") + " failed");
                }
                throw e;
            }
            finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Display transaction history for an account.
    public void transactionHistory(String accountNumber)
            throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            Map<?,?> details = getAccountId(
                    con, accountNumber, false);

            String sql = """
                SELECT transaction_id, transaction_type,
                       amount, status, related_account_id,
                       description, transaction_time
                FROM transactions
                WHERE account_id = ?
                ORDER BY transaction_time DESC
                """;

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {
                ps.setInt(1, (int) details.get("account_id"));

                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println(
                            "TYPE | AMOUNT | STATUS | DESCRIPTION | TIME");

                    while (rs.next()) {
                        System.out.printf(
                                "%s | %s | %s | %s | %s%n",
                                rs.getString("transaction_type"),
                                rs.getBigDecimal("amount"),
                                rs.getString("status"),
                                rs.getString("description"),
                                rs.getTimestamp("transaction_time"));
                    }
                }
            }
        }
    }

    // Show current balance.
    public void showBalance(String accountNumber)
            throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            Map<?,?> details = getAccountId(con, accountNumber, false);
            System.out.println("Balance: " + getBalance(con, (int) details.get("account_id")));
        }
    }

    // Return account ID and optionally lock its row.
    private Map<String,?> getAccountId(Connection con,
                                  String accountNumber,
                                  boolean lock) throws Exception {

        String sql =  """
        SELECT a.account_id, c.name
        FROM accounts a
        JOIN customers c ON a.customer_id = c.customer_id
        WHERE a.account_number = ?
        """ + (lock ? " FOR UPDATE" : "");

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new BankingException(
                            "Account not found: " + accountNumber);
                }
                return Map.of("account_id", rs.getInt("account_id"),  "name", rs.getString("name"));
            }
        }
    }

    // Read balance and lock the row for updates.
    private BigDecimal getBalance(Connection con, int accountId)
            throws SQLException {

        String sql = """
            SELECT balance FROM accounts
            WHERE account_id = ? FOR UPDATE
            """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {
            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new BankingException("Account not found.");
                }
                return rs.getBigDecimal("balance");
            }
        }
    }

    // Insert transaction history in the same DB transaction.
    private void addHistory(Connection con,
                            int accountId,
                            Integer relatedAccountId,
                            String type,
                            BigDecimal amount,
                            String status,
                            String description) throws SQLException {

        String sql = """
            INSERT INTO transactions
                (account_id, related_account_id,
                 transaction_type, amount, status, description)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {
            ps.setInt(1, accountId);

            if (relatedAccountId == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, relatedAccountId);
            }

            ps.setString(3, type);
            ps.setBigDecimal(4, amount);
            ps.setString(5, status);
            ps.setString(6, description);
            ps.executeUpdate();
        }
    }

//    private void validateAmount(BigDecimal amount,
//                                boolean allowZero)
//            throws BankingException {
//
//        if (amount == null || amount.scale() > 2
//                || amount.signum() < 0
//                || (!allowZero && amount.signum() == 0)) {
//            throw new BankingException(
//                    allowZero
//                            ? "Initial deposit must be non-negative "
//                            + "and have at most two decimal places."
//                            : "Amount must be positive with at most "
//                            + "two decimal places.");
//        }
//    }

    private void addFailedHistory(int accountId, Integer toAccount, String type, BigDecimal amount, String message) throws SQLException {

        String sql = """
        INSERT INTO transactions
            (account_id, related_account_id,
             transaction_type, amount, status, description)
        VALUES (?, NULL, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountId);
            ps.setString(2, type);
            ps.setBigDecimal(3, amount);
            ps.setString(4, "FAILED");
            ps.setString(5, message);

            ps.executeUpdate();
        }
    }



    private void rollback(Connection con, Exception original) {
        try {
            con.rollback();
        } catch (SQLException rollbackError) {
            original.addSuppressed(rollbackError);
        }
    }
}

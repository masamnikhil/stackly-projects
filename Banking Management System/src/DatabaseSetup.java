import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseSetup {

    private static final String URL =
            "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "Nikhil@123";

    public static void main(String[] args) {

        String createDatabase =
                "CREATE DATABASE IF NOT EXISTS banking_system_db";

        try (Connection con = DriverManager.getConnection(
                URL, USER, PASSWORD);
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(createDatabase);
            System.out.println("Database created successfully.");

        } catch (Exception e) {
            System.out.println("Database setup failed: "
                    + e.getMessage());
            return;
        }

        String dbUrl = URL + "banking_system_db";

        try (Connection con = DriverManager.getConnection(
                dbUrl, USER, PASSWORD);
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate("""
                
                    CREATE TABLE IF NOT EXISTS customers (
                    customer_id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    username VARCHAR(100) NOT NULL UNIQUE,
                    email VARCHAR(150) NOT NULL UNIQUE,
                    password VARCHAR(100) NOT NULL,    
                    phone VARCHAR(15) NOT NULL
                ) ENGINE=InnoDB
                """);

            stmt.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS accounts (
                        account_id INT PRIMARY KEY AUTO_INCREMENT,
                        customer_id INT NOT NULL,
                        account_number VARCHAR(20) NOT NULL UNIQUE,
                        balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                
                        CONSTRAINT fk_account_customer
                            FOREIGN KEY (customer_id)
                            REFERENCES customers(customer_id),
                
                        CONSTRAINT chk_account_balance
                            CHECK (balance >= 0)
                    ) ENGINE=InnoDB
                    """
            );

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    account_id INT NOT NULL,
                    related_account_id INT NULL,
                    transaction_type VARCHAR(20) NOT NULL,
                    amount DECIMAL(15,2) NOT NULL,
                    status VARCHAR(20) NOT NULL,
                    description VARCHAR(255),
                    transaction_time TIMESTAMP
                        DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (account_id)
                        REFERENCES accounts(account_id),
                    FOREIGN KEY (related_account_id)
                        REFERENCES accounts(account_id),
                    CHECK (amount > 0)
                ) ENGINE=InnoDB
                """);

            System.out.println("All tables created successfully.");

        } catch (Exception e) {
            System.out.println("Table creation failed: "
                    + e.getMessage());
        }
    }
}

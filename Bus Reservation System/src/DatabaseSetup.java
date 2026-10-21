import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "Nikhil@123";

        try (Connection con =
                     DriverManager.getConnection(url, user, password);
             Statement st = con.createStatement()) {

            st.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS bus_reservation_db");

            System.out.println("Database created successfully.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return;
        }

        String[] queries = {

                """
            CREATE TABLE IF NOT EXISTS buses (
                bus_id INT PRIMARY KEY AUTO_INCREMENT,
                bus_name VARCHAR(100) NOT NULL,
                source VARCHAR(100) NOT NULL,
                destination VARCHAR(100) NOT NULL,
                departure_time DATETIME NOT NULL,
                arrival_time DATETIME NOT NULL,
                total_seats INT NOT NULL CHECK (total_seats > 0),
                fare DECIMAL(10,2) NOT NULL CHECK (fare > 0)
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS passengers (
                passenger_id INT PRIMARY KEY AUTO_INCREMENT,
                passenger_name VARCHAR(100) NOT NULL,
                age INT NOT NULL CHECK (age > 0 AND age <= 120),
                gender VARCHAR(20),
                phone VARCHAR(15) NOT NULL
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS bookings (
                booking_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                bus_id INT NOT NULL,
                passenger_id INT NOT NULL,
                seat_number INT NOT NULL,
                booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
                active_seat_number INT GENERATED ALWAYS AS (
                    CASE
                        WHEN status = 'CONFIRMED' THEN seat_number
                        ELSE NULL
                    END
                ) STORED,
                FOREIGN KEY (bus_id) REFERENCES buses(bus_id),
                FOREIGN KEY (passenger_id)
                    REFERENCES passengers(passenger_id),
                UNIQUE (bus_id, active_seat_number),
                CHECK (status IN ('CONFIRMED', 'CANCELLED'))
            )
            """
        };

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {

            for (String query : queries) {
                st.executeUpdate(query);
            }

            System.out.println("Tables created successfully.");
            insertSampleBuses(con);

        } catch (SQLException e) {
            System.out.println("Setup failed: " + e.getMessage());
        }
    }

    private static void insertSampleBuses(Connection con)
            throws SQLException {

        String sql = """
                INSERT INTO buses
                (bus_name, source, destination, departure_time,
                 arrival_time, total_seats, fare)
                SELECT ?, ?, ?, ?, ?, ?, ?
                WHERE NOT EXISTS (
                    SELECT 1 FROM buses
                    WHERE bus_name = ? AND departure_time = ?
                )
                """;

        try (var ps = con.prepareStatement(sql)) {

            addBus(ps, "Orange Travels", "Hyderabad", "Bangalore",
                    "2027-01-10 21:00:00", "2027-01-11 07:00:00",
                    40, 900.00);

            addBus(ps, "VRL Travels", "Hyderabad", "Chennai",
                    "2027-01-10 20:00:00", "2027-01-11 06:00:00",
                    35, 800.00);

            addBus(ps, "Kaveri Travels", "Bangalore", "Hyderabad",
                    "2027-01-10 22:00:00", "2027-01-11 08:00:00",
                    40, 950.00);

            addBus(ps, "SRS Travels", "Chennai", "Hyderabad",
                    "2027-01-12 19:00:00", "2027-01-13 06:00:00", 30, 750.00);
        }

        System.out.println("Sample buses inserted successfully.");
    }

    private static void addBus(
            java.sql.PreparedStatement ps,
            String name, String source, String destination,
            String departure, String arrival,
            int seats, double fare) throws SQLException {

        ps.setString(1, name);
        ps.setString(2, source);
        ps.setString(3, destination);
        ps.setString(4, departure);
        ps.setString(5, arrival);
        ps.setInt(6, seats);
        ps.setBigDecimal(7, java.math.BigDecimal.valueOf(fare));
        ps.setString(8, name);
        ps.setString(9, departure);

        ps.executeUpdate();
    }
}

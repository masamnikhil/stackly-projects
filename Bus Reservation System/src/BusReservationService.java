import java.math.BigDecimal;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class BusReservationService {

    public void searchBuses(String source, String destination) {

        String sql = """
                SELECT *
                FROM buses
                WHERE LOWER(source) = LOWER(?)
                  AND LOWER(destination) = LOWER(?)
                ORDER BY departure_time
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, source.trim());
            ps.setString(2, destination.trim());

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                System.out.println("\n--- AVAILABLE BUSES ---");

                while (rs.next()) {
                    found = true;

                    System.out.println(
                            "Bus ID: " + rs.getInt("bus_id") +
                                    " | Name: " + rs.getString("bus_name") +
                                    " | From: " + rs.getString("source") +
                                    " | To: " + rs.getString("destination") +
                                    " | Departure: " + rs.getTimestamp("departure_time") +
                                    " | Arrival: " + rs.getTimestamp("arrival_time") +
                                    " | Seats: " + rs.getInt("total_seats") +
                                    " | Fare: Rs. " + rs.getBigDecimal("fare")
                    );
                }

                if (!found) {
                    System.out.println("No buses found for this route.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }

    // View available seats
    public void viewAvailableSeats(int busId) {

        String sql = """
                SELECT total_seats
                FROM buses
                WHERE bus_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, busId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("Bus not found.");
                    return;
                }

                int totalSeats = rs.getInt("total_seats");
                Set<Integer> booked = getBookedSeats(con, busId);

                System.out.println("\n--- SEAT AVAILABILITY ---");

                for (int seat = 1; seat <= totalSeats; seat++) {
                    System.out.println(
                            "Seat " + seat + ": " +
                                    (booked.contains(seat)
                                            ? "BOOKED" : "AVAILABLE")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Unable to view seats: " + e.getMessage());
        }
    }

    // Helper method to retrieve confirmed seats
    private Set<Integer> getBookedSeats(Connection con, int busId)
            throws SQLException {

        Set<Integer> seats = new HashSet<>();

        String sql = """
                SELECT seat_number
                FROM bookings
                WHERE bus_id = ? AND status = 'CONFIRMED'
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, busId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(rs.getInt("seat_number"));
                }
            }
        }

        return seats;
    }

    // Book a seat using a JDBC transaction
    public void bookSeat(
            int busId, String name, int age,
            String gender, String phone, int seatNumber) {

        if (name == null || name.isBlank()) {
            System.out.println("Passenger name cannot be empty.");
            return;
        }

        if (age < 1 || age > 120) {
            System.out.println("Enter a valid passenger age.");
            return;
        }

        if (phone == null || !phone.matches("\\d{10}")) {
            System.out.println("Phone must contain 10 digits.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                // Lock the bus row to coordinate concurrent reservations
                int totalSeats;
                BigDecimal fare;

                String busSql = """
                        SELECT total_seats, fare
                        FROM buses
                        WHERE bus_id = ?
                        FOR UPDATE
                        """;

                try (PreparedStatement ps =
                             con.prepareStatement(busSql)) {

                    ps.setInt(1, busId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Bus not found.");
                        }

                        totalSeats = rs.getInt("total_seats");
                        fare = rs.getBigDecimal("fare");
                    }
                }

                // Validate seat number
                if (seatNumber < 1 || seatNumber > totalSeats) {
                    throw new SQLException("Invalid seat number.");
                }

                // Check whether the seat is already booked
                Set<Integer> booked = getBookedSeats(con, busId);

                if (booked.contains(seatNumber)) {
                    throw new SQLException(
                            "Seat " + seatNumber + " is already booked.");
                }

                // Insert passenger
                int passengerId;

                String passengerSql = """
                        INSERT INTO passengers
                        (passenger_name, age, gender, phone)
                        VALUES (?, ?, ?, ?)
                        """;

                try (PreparedStatement ps = con.prepareStatement(
                        passengerSql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, name.trim());
                    ps.setInt(2, age);
                    ps.setString(3, gender);
                    ps.setString(4, phone);

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                    "Passenger ID could not be generated.");
                        }

                        passengerId = keys.getInt(1);
                    }
                }

                // Insert booking and generate ticket ID
                long bookingId;

                String bookingSql = """
                        INSERT INTO bookings
                        (bus_id, passenger_id, seat_number, status)
                        VALUES (?, ?, ?, 'CONFIRMED')
                        """;

                try (PreparedStatement ps = con.prepareStatement(
                        bookingSql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, busId);
                    ps.setInt(2, passengerId);
                    ps.setInt(3, seatNumber);

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                    "Ticket ID could not be generated.");
                        }

                        bookingId = keys.getLong(1);
                    }
                }

                // Commit passenger and booking together
                con.commit();

                System.out.println("\nBooking successful!");
                System.out.println("Ticket ID: " + bookingId);
                System.out.println("Passenger: " + name);
                System.out.println("Seat Number: " + seatNumber);
                System.out.println("Fare: Rs. " + fare);
                System.out.println("Status: CONFIRMED");

            } catch (Exception e) {
                con.rollback();

                System.out.println(
                        "Booking failed : " +
                                e.getMessage());

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // View ticket and passenger details
    public void viewTicket(long bookingId) {

        String sql = """
                SELECT b.booking_id, b.seat_number, b.booking_time,
                       b.status, p.passenger_name, p.age,
                       p.gender, p.phone, bus.bus_name,
                       bus.source, bus.destination,
                       bus.departure_time, bus.fare
                FROM bookings b
                JOIN passengers p ON b.passenger_id = p.passenger_id
                JOIN buses bus ON b.bus_id = bus.bus_id
                WHERE b.booking_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("Ticket not found.");
                    return;
                }

                System.out.println("\n======= BUS TICKET =======");
                System.out.println("Ticket ID: " + rs.getLong("booking_id"));
                System.out.println("Passenger: " + rs.getString("passenger_name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Gender: " + rs.getString("gender"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Bus: " + rs.getString("bus_name"));
                System.out.println("Route: " + rs.getString("source") +
                        " to " + rs.getString("destination"));
                System.out.println("Departure: " + rs.getTimestamp("departure_time"));
                System.out.println("Seat: " + rs.getInt("seat_number"));
                System.out.println("Fare: Rs. " + rs.getBigDecimal("fare"));
                System.out.println("Booked at: " + rs.getTimestamp("booking_time"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("==========================");
            }

        } catch (SQLException e) {
            System.out.println("Ticket lookup failed: " + e.getMessage());
        }
    }

    // Cancel ticket and release the seat
    public void cancelTicket(long bookingId) {

        String sql = """
                UPDATE bookings
                SET status = 'CANCELLED'
                WHERE booking_id = ? AND status = 'CONFIRMED'
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, bookingId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Ticket cancelled successfully.");
                System.out.println("The seat is now available.");
            } else {
                System.out.println(
                        "Ticket not found or already cancelled.");
            }

        } catch (SQLException e) {
            System.out.println("Cancellation failed: " + e.getMessage());
        }
    }
}

import java.util.Scanner;

public class BusReservationSystem {

    private static final Scanner sc = new Scanner(System.in);
    private static final BusReservationService service = new BusReservationService();

        public static void main(String[] args) {

            int choice;

            do {
                System.out.println("\n===== BUS RESERVATION SYSTEM =====");
                System.out.println("1. Search Buses");
                System.out.println("2. View Available Seats");
                System.out.println("3. Book Ticket");
                System.out.println("4. View Ticket");
                System.out.println("5. Cancel Ticket");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");

                choice = readInt();

                switch (choice) {

                    case 1: {
                        System.out.print("Enter source: ");
                        String source = sc.nextLine().trim();

                        System.out.print("Enter destination: ");
                        String destination = sc.nextLine().trim();

                        service.searchBuses(source, destination);
                        break;
                    }

                    case 2: {
                        System.out.print("Enter bus ID: ");
                        int busId = readInt();

                        service.viewAvailableSeats(busId);
                        break;
                    }

                    case 3: {
                        System.out.print("Enter bus ID: ");
                        int busId = readInt();

                        System.out.print("Enter passenger name: ");
                        String name = sc.nextLine().trim();

                        System.out.print("Enter age: ");
                        int age = readInt();

                        System.out.print("Enter gender: ");
                        String gender = sc.nextLine().trim();

                        System.out.print("Enter phone number: ");
                        String phone = sc.nextLine().trim();

                        System.out.print("Enter seat number: ");
                        int seatNumber = readInt();

                        service.bookSeat(
                                busId, name, age, gender, phone, seatNumber);
                        break;
                    }

                    case 4: {
                        System.out.print("Enter ticket ID: ");
                        long ticketId = readLong();

                        service.viewTicket(ticketId);
                        break;
                    }

                    case 5: {
                        System.out.print("Enter ticket ID to cancel: ");
                        long ticketId = readLong();

                        service.cancelTicket(ticketId);
                        break;
                    }

                    case 0:
                        System.out.println("Thank you for using the system!");
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }

            } while (choice != 0);

            sc.close();
        }

        private static int readInt() {
            while (true) {
                String input = sc.nextLine().trim();

                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.print("Enter a valid integer: ");
                }
            }
        }

        private static long readLong() {
            while (true) {
                String input = sc.nextLine().trim();

                try {
                    return Long.parseLong(input);
                } catch (NumberFormatException e) {
                    System.out.print("Enter a valid ticket ID: ");
                }
            }
    }
}
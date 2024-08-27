import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDate;

public class HotelReservationSystem {

    static class Room {
        private final String category;
        private final int roomNumber;
        private boolean isAvailable;
        private double pricePerNight;

        public Room(String category, int roomNumber, double pricePerNight) {
            if (category == null || category.isEmpty()) {
                throw new IllegalArgumentException("Room category cannot be null or empty.");
            }
            if (pricePerNight <= 0) {
                throw new IllegalArgumentException("Price per night must be positive.");
            }
            this.category = category;
            this.roomNumber = roomNumber;
            this.pricePerNight = pricePerNight;
            this.isAvailable = true;
        }

        public String getCategory() {
            return category;
        }

        public int getRoomNumber() {
            return roomNumber;
        }

        public boolean isAvailable() {
            return isAvailable;
        }

        public double getPricePerNight() {
            return pricePerNight;
        }

        public void reserveRoom() {
            if (!isAvailable) {
                throw new IllegalStateException("Room is already reserved.");
            }
            this.isAvailable = false;
        }

        public void freeRoom() {
            this.isAvailable = true;
        }
    }

    static class Reservation {
        private final Room room;
        private final LocalDate checkInDate;
        private final LocalDate checkOutDate;
        private final String customerName;
        private final double totalPrice;

        public Reservation(Room room, LocalDate checkInDate, LocalDate checkOutDate, String customerName) {
            this.room = room;
            this.checkInDate = checkInDate;
            this.checkOutDate = checkOutDate;
            this.customerName = customerName;
            this.totalPrice = room.getPricePerNight() * (checkOutDate.toEpochDay() - checkInDate.toEpochDay());
        }

        public Room getRoom() {
            return room;
        }

        public LocalDate getCheckInDate() {
            return checkInDate;
        }

        public LocalDate getCheckOutDate() {
            return checkOutDate;
        }

        public String getCustomerName() {
            return customerName;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void viewReservationDetails() {
            System.out.printf("Reservation for %s%n", customerName);
            System.out.printf("Room Number: %d, Category: %s%n", room.getRoomNumber(), room.getCategory());
            System.out.printf("Check-in Date: %s, Check-out Date: %s%n", checkInDate, checkOutDate);
            System.out.printf("Total Price: $%.2f%n", totalPrice);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Room categories and prices
        Map<Integer, Room> roomMap = new HashMap<>();
        roomMap.put(101, new Room("Single", 101, 100.00));
        roomMap.put(102, new Room("Double", 102, 150.00));
        roomMap.put(103, new Room("Suite", 103, 250.00));

        // Reservations map to store reservations based on room number
        Map<Integer, Reservation> reservations = new HashMap<>();

        while (true) {
            displayMenu();

            int choice = getUserChoice(scanner);
            if (choice == -1) continue;

            switch (choice) {
                case 1:
                    viewAvailableRooms(roomMap);
                    break;
                case 2:
                    makeReservation(scanner, roomMap, reservations);
                    break;
                case 3:
                    viewReservationDetails(scanner, reservations);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n--- Hotel Reservation System ---");
        System.out.println("1. View Available Rooms");
        System.out.println("2. Make a Reservation");
        System.out.println("3. View Reservation Details");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    private static int getUserChoice(Scanner scanner) {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
            return -1;
        }
    }

    private static void viewAvailableRooms(Map<Integer, Room> roomMap) {
        System.out.println("Available Rooms:");
        for (Room room : roomMap.values()) {
            if (room.isAvailable()) {
                System.out.printf("Room Number: %d, Category: %s, Price per Night: $%.2f%n", room.getRoomNumber(), room.getCategory(), room.getPricePerNight());
            }
        }
    }

    private static void makeReservation(Scanner scanner, Map<Integer, Room> roomMap, Map<Integer, Reservation> reservations) {
        System.out.print("Enter room number to reserve: ");
        int roomNumber = getPositiveInteger(scanner);

        if (roomMap.containsKey(roomNumber)) {
            Room room = roomMap.get(roomNumber);

            if (room.isAvailable()) {
                System.out.print("Enter your name: ");
                String customerName = scanner.next();

                System.out.print("Enter check-in date (YYYY-MM-DD): ");
                LocalDate checkInDate = LocalDate.parse(scanner.next());

                System.out.print("Enter check-out date (YYYY-MM-DD): ");
                LocalDate checkOutDate = LocalDate.parse(scanner.next());

                if (!checkOutDate.isAfter(checkInDate)) {
                    System.out.println("Check-out date must be after check-in date.");
                    return;
                }

                Reservation reservation = new Reservation(room, checkInDate, checkOutDate, customerName);
                reservations.put(roomNumber, reservation);
                room.reserveRoom();
                System.out.println("Reservation successful!");
            } else {
                System.out.println("Room is already reserved.");
            }
        } else {
            System.out.println("Invalid room number.");
        }
    }

    private static void viewReservationDetails(Scanner scanner, Map<Integer, Reservation> reservations) {
        System.out.print("Enter room number to view reservation: ");
        int roomNumber = getPositiveInteger(scanner);

        if (reservations.containsKey(roomNumber)) {
            Reservation reservation = reservations.get(roomNumber);
            reservation.viewReservationDetails();
        } else {
            System.out.println("No reservation found for this room.");
        }
    }

    private static int getPositiveInteger(Scanner scanner) {
        while (true) {
            try {
                int value = scanner.nextInt();
                if (value > 0) {
                    return value;
                } else {
                    System.out.print("Please enter a positive number: ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.next();
            }
        }
    }
}

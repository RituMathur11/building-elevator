package elevator;

public class Main {
    public static void main(String[] args) {
        ElevatorSystem system = new ElevatorSystem();

        System.out.println("Initial state:");
        printState(system);

        System.out.println("\nSubmitting hall calls:");
        System.out.println("Floor 5 UP: " +
                system.requestElevator(5, Direction.UP));
        System.out.println("Floor 8 DOWN: " +
                system.requestElevator(8, Direction.DOWN));
        System.out.println("Floor 3 UP: " +
                system.requestElevator(3, Direction.UP));

        System.out.println("Invalid floor 12: " +
                system.requestElevator(12, Direction.UP));
        System.out.println("Invalid ID 99: " +
                system.selectDestination(99, 4));

        // Demonstrate multiple destinations in one elevator.
        System.out.println("Elevator 1 destination 7: " +
                system.selectDestination(1, 7));
        System.out.println("Elevator 1 destination 9: " +
                system.selectDestination(1, 9));

        for (int time = 1; time <= 15; time++) {
            System.out.println("\n--- TIME STEP " + time + " ---");
            system.step();
            printState(system);
        }
    }

    private static void printState(ElevatorSystem system) {
        for (Elevator elevator : system.getElevators()) {
            System.out.println(elevator);
        }
    }
}

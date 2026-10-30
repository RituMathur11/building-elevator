package elevator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Coordinates the fixed set of three elevators and simulation steps. */
public final class ElevatorSystem {
    public static final int FLOOR_COUNT = 10;
    public static final int ELEVATOR_COUNT = 3;

    private final List<Elevator> elevators;
    private final ElevatorDispatcher dispatcher;

    public ElevatorSystem() {
        this(new DirectionAwareStrategy());
    }

    public ElevatorSystem(DispatchStrategy strategy) {
        this.elevators = new ArrayList<>();
        this.dispatcher = new ElevatorDispatcher(strategy);

        for (int id = 1; id <= ELEVATOR_COUNT; id++) {
            elevators.add(new Elevator(id, 0));
        }
    }

    /**
     * Submits a hall call. Returns false for invalid floors/directions.
     * Repeated identical pending calls are accepted without duplicate dispatch.
     */
    public boolean requestElevator(int floor, Direction direction) {
        if (!isValidFloor(floor)
                || direction == null
                || direction == Direction.IDLE) {
            return false;
        }

        HallCall call = new HallCall(floor, direction);

        // If this exact request is already assigned, accept it without
        // dispatching a second elevator.
        for (Elevator elevator : elevators) {
            if (elevator.hasHallCall(call)) return true;
        }

        // A request at the current floor is a no-op if an idle elevator
        // is already there. Doors and passenger boarding are out of scope.
        for (Elevator elevator : elevators) {
            if (elevator.getCurrentFloor() == floor && elevator.isIdle()) {
                return true;
            }
        }

        Elevator selected = dispatcher.dispatch(
                Collections.unmodifiableList(elevators), floor, direction);

        if (selected == null) return false;
        return selected.addHallCall(call);
    }

    public boolean selectDestination(int elevatorId, int floor) {
        if (!isValidFloor(floor)) return false;

        Elevator elevator = findElevator(elevatorId);
        if (elevator == null) return false;

        return elevator.addDestination(floor);
    }

    /** Advances every elevator by one discrete time step. */
    public void step() {
        for (Elevator elevator : elevators) {
            elevator.step();
        }
    }

    public List<Elevator> getElevators() {
        return Collections.unmodifiableList(elevators);
    }

    public Elevator getElevator(int elevatorId) {
        return findElevator(elevatorId);
    }

    private Elevator findElevator(int id) {
        return elevators.stream()
                .filter(elevator -> elevator.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private boolean isValidFloor(int floor) {
        return floor >= 0 && floor < FLOOR_COUNT;
    }
}

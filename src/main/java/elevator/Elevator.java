package elevator;

import java.util.HashSet;
import java.util.Set;

/** One elevator car. Movement advances by at most one floor per step(). */
public final class Elevator {
    public static final int MIN_FLOOR = 0;
    public static final int MAX_FLOOR = 9;

    private final int id;
    private int currentFloor;
    private Direction direction = Direction.IDLE;

    private final Set<Integer> destinationFloors = new HashSet<>();
    private final Set<HallCall> hallCalls = new HashSet<>();

    public Elevator(int id, int initialFloor) {
        if (id < 1)
            throw new IllegalArgumentException("Elevator ID must be positive");
        if (!isValidFloor(initialFloor)) {
            throw new IllegalArgumentException("Invalid initial floor: " + initialFloor);
        }
        this.id = id;
        this.currentFloor = initialFloor;
    }

    public boolean addDestination(int floor) {
        if (!isValidFloor(floor))
            return false;
        if (floor == currentFloor)
            return true; // already served / no-op
        destinationFloors.add(floor);
        return true;
    }

    public boolean addHallCall(HallCall call) {
        if (call == null || !isValidFloor(call.getFloor()))
            return false;
        if (call.getFloor() == currentFloor)
            return true; // already served / no-op
        return hallCalls.add(call);
    }

    /**
     * Advances this elevator by at most one floor.
     * Destinations can be served in either direction. Hall calls are served
     * only when the elevator is travelling in the requested direction.
     */
    public void step() {
        serviceCurrentFloor();

        if (!hasPendingRequests()) {
            direction = Direction.IDLE;
            return;
        }

        if (direction == Direction.IDLE) {
            direction = chooseDirection();
        }

        serviceCurrentFloor();
        if (!hasPendingRequests()) {
            direction = Direction.IDLE;
            return;
        }

        if (!hasRequestAhead(direction)) {
            direction = opposite(direction);
        }

        // A reversal can make a hall call at the current floor eligible.
        int before = getWorkload();
        serviceCurrentFloor();
        if (getWorkload() < before) {
            if (!hasPendingRequests())
                direction = Direction.IDLE;
            return;
        }

        if (direction == Direction.UP && currentFloor < MAX_FLOOR) {
            currentFloor++;
        } else if (direction == Direction.DOWN && currentFloor > MIN_FLOOR) {
            currentFloor--;
        }

        serviceCurrentFloor();
        if (!hasPendingRequests())
            direction = Direction.IDLE;
    }

    private void serviceCurrentFloor() {
        destinationFloors.remove(currentFloor);
        if (direction == Direction.IDLE)
            return;

        Direction travelDirection = direction;
        hallCalls.removeIf(call -> call.getFloor() == currentFloor
                && call.getDirection() == travelDirection);
    }

    private Direction chooseDirection() {
        int nearestFloor = -1;
        int nearestDistance = Integer.MAX_VALUE;

        for (int floor : destinationFloors) {
            int distance = Math.abs(floor - currentFloor);
            if (distance > 0 && distance < nearestDistance) {
                nearestDistance = distance;
                nearestFloor = floor;
            }
        }

        for (HallCall call : hallCalls) {
            int floor = call.getFloor();
            int distance = Math.abs(floor - currentFloor);
            if (distance > 0 && distance < nearestDistance) {
                nearestDistance = distance;
                nearestFloor = floor;
            }
        }

        if (nearestFloor < 0)
            return Direction.IDLE;

        // When the nearest target is a hall call, approach it in its requested
        // direction only after reaching the floor; until then move toward it.
        return nearestFloor > currentFloor ? Direction.UP : Direction.DOWN;
    }

    private boolean hasRequestAhead(Direction travelDirection) {
        for (int floor : destinationFloors) {
            if (travelDirection == Direction.UP && floor > currentFloor)
                return true;
            if (travelDirection == Direction.DOWN && floor < currentFloor)
                return true;
        }

        for (HallCall call : hallCalls) {
            if (call.getDirection() != travelDirection)
                continue;
            if (travelDirection == Direction.UP && call.getFloor() > currentFloor)
                return true;
            if (travelDirection == Direction.DOWN && call.getFloor() < currentFloor)
                return true;
        }
        return false;
    }

    private Direction opposite(Direction value) {
        if (value == Direction.UP)
            return Direction.DOWN;
        if (value == Direction.DOWN)
            return Direction.UP;
        return Direction.IDLE;
    }

    private boolean hasPendingRequests() {
        return !destinationFloors.isEmpty() || !hallCalls.isEmpty();
    }

    private boolean isValidFloor(int floor) {
        return floor >= MIN_FLOOR && floor <= MAX_FLOOR;
    }

    public boolean isIdle() {
        return direction == Direction.IDLE && !hasPendingRequests();
    }

    public boolean hasHallCall(HallCall call) {
        return hallCalls.contains(call);
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getWorkload() {
        return destinationFloors.size() + hallCalls.size();
    }

    public Set<Integer> getDestinationFloors() {
        return Set.copyOf(destinationFloors);
    }

    public Set<HallCall> getHallCalls() {
        return Set.copyOf(hallCalls);
    }

    @Override
    public String toString() {
        return "Elevator{id=" + id + ", floor=" + currentFloor
                + ", direction=" + direction + ", destinations="
                + destinationFloors + ", hallCalls=" + hallCalls + '}';
    }
}

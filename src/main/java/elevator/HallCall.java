package elevator;

import java.util.Objects;

/** A pickup request made from a floor in a particular direction. */
public final class HallCall {
    private final int floor;
    private final Direction direction;

    public HallCall(int floor, Direction direction) {
        this.floor = floor;
        this.direction = Objects.requireNonNull(direction, "direction");
        if (direction == Direction.IDLE) {
            throw new IllegalArgumentException("A hall call direction must be UP or DOWN");
        }
    }

    public int getFloor() {
        return floor;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HallCall other)) return false;
        return floor == other.floor && direction == other.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(floor, direction);
    }

    @Override
    public String toString() {
        return "HallCall{floor=" + floor + ", direction=" + direction + '}';
    }
}

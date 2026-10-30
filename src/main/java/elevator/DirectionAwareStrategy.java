package elevator;

import java.util.Comparator;
import java.util.List;

/**
 * Selects an elevator using distance, workload, and direction compatibility.
 */
public final class DirectionAwareStrategy implements DispatchStrategy {
        private static final int REVERSAL_PENALTY = 10;
        private static final int WORKLOAD_WEIGHT = 3;

        @Override
        public Elevator dispatch(List<Elevator> elevators, int requestedFloor,
                        Direction requestedDirection) {
                return elevators.stream()
                                .filter(e -> e.getCurrentFloor() != requestedFloor)
                                .min(Comparator
                                                .comparingInt((Elevator e) -> score(e, requestedFloor,
                                                                requestedDirection))
                                                .thenComparingInt(Elevator::getId))
                                .orElse(null);
        }

        private int score(Elevator elevator, int floor, Direction requestedDirection) {
                int distance = Math.abs(elevator.getCurrentFloor() - floor);
                int score = distance + elevator.getWorkload() * WORKLOAD_WEIGHT;

                if (!elevator.isIdle()) {
                        boolean movingInRequestedDirection = elevator.getDirection() == requestedDirection;
                        boolean floorAhead = requestedDirection == Direction.UP
                                        ? floor > elevator.getCurrentFloor()
                                        : floor < elevator.getCurrentFloor();

                        if (!movingInRequestedDirection || !floorAhead) {
                                score += REVERSAL_PENALTY;
                        }
                }
                return score;
        }
}

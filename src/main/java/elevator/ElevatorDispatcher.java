package elevator;

import java.util.List;
import java.util.Objects;

/** Delegates hall-call assignment to a replaceable scheduling strategy. */
public final class ElevatorDispatcher {
    private final DispatchStrategy strategy;

    public ElevatorDispatcher(DispatchStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public Elevator dispatch(List<Elevator> elevators, int floor,
                             Direction direction) {
        return strategy.dispatch(elevators, floor, direction);
    }
}

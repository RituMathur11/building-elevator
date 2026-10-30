package elevator;

import java.util.List;

/** Strategy abstraction for selecting an elevator for a hall call. */
public interface DispatchStrategy {
    Elevator dispatch(List<Elevator> elevators, int requestedFloor,
                      Direction requestedDirection);
}

package elevator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevatorSystemTest {

    @Test
    void createsThreeElevatorsAtFloorZero() {
        ElevatorSystem system = new ElevatorSystem();

        assertEquals(3, system.getElevators().size());
        for (Elevator elevator : system.getElevators()) {
            assertEquals(0, elevator.getCurrentFloor());
            assertTrue(elevator.isIdle());
        }
    }

    @Test
    void rejectsInvalidHallCallFloorsAndDirection() {
        ElevatorSystem system = new ElevatorSystem();

        assertFalse(system.requestElevator(-1, Direction.UP));
        assertFalse(system.requestElevator(10, Direction.DOWN));
        assertFalse(system.requestElevator(5, Direction.IDLE));
        assertFalse(system.requestElevator(5, null));
    }

    @Test
    void rejectsInvalidDestinationAndElevatorId() {
        ElevatorSystem system = new ElevatorSystem();

        assertFalse(system.selectDestination(1, -1));
        assertFalse(system.selectDestination(1, 10));
        assertFalse(system.selectDestination(99, 5));
    }

    @Test
    void destinationAtCurrentFloorIsNoOp() {
        ElevatorSystem system = new ElevatorSystem();

        assertTrue(system.selectDestination(1, 0));
        assertEquals(0, system.getElevator(1).getWorkload());
    }

    @Test
    void repeatedSameHallCallIsNotDuplicated() {
        ElevatorSystem system = new ElevatorSystem();

        assertTrue(system.requestElevator(5, Direction.UP));
        assertTrue(system.requestElevator(5, Direction.UP));

        long assignments = system.getElevators().stream()
                .filter(e -> e.hasHallCall(new HallCall(5, Direction.UP)))
                .count();

        assertEquals(1, assignments);
    }

    @Test
    void eachStepMovesElevatorAtMostOneFloor() {
        Elevator elevator = new Elevator(1, 0);
        assertTrue(elevator.addDestination(5));

        elevator.step();
        assertEquals(1, elevator.getCurrentFloor());

        elevator.step();
        assertEquals(2, elevator.getCurrentFloor());
    }

    @Test
    void reachesDestinationAndBecomesIdle() {
        Elevator elevator = new Elevator(1, 0);
        assertTrue(elevator.addDestination(2));

        elevator.step();
        elevator.step();

        assertEquals(2, elevator.getCurrentFloor());
        assertTrue(elevator.isIdle());
        assertEquals(0, elevator.getWorkload());
    }

    @Test
    void hallCallIsStoredOnExactlyOneElevator() {
        ElevatorSystem system = new ElevatorSystem();

        assertTrue(system.requestElevator(6, Direction.DOWN));

        long assignments = system.getElevators().stream()
                .filter(e -> e.hasHallCall(new HallCall(6, Direction.DOWN)))
                .count();

        assertEquals(1, assignments);
    }
}

import java.util.*;

interface ElevatorOperations {
    void requestFloor(int floor);
    void requestFromOutside(int floor, Direction direction);
    void start();
}

enum Direction {
    UP,
    DOWN,
    IDLE
}

class Elevator implements ElevatorOperations {
    private int currentFloor;
    private Direction direction;
    private final int MAX_FLOOR = 10;
    private final int MIN_FLOOR = 1;

    private final TreeSet<Integer> upQueue = new TreeSet<>();
    private final TreeSet<Integer> downQueue = new TreeSet<>(Collections.reverseOrder());

    public Elevator() {
        currentFloor = 1;
        direction = Direction.IDLE;
    }

    @Override
    public void requestFloor(int floor) {
        if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
            System.out.println("Invalid floor requested: " + floor);
            return;
        }
        if (floor > currentFloor) {
            upQueue.add(floor);
        } else if (floor < currentFloor) {
            downQueue.add(floor);
        } else {
            System.out.println("Elevator is already at floor " + floor);
        }
    }

    @Override
    public void requestFromOutside(int floor, Direction dir) {
        if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
            System.out.println("Invalid floor requested: " + floor);
            return;
        }
        if (dir == Direction.UP) {
            upQueue.add(floor);
        } else {
            downQueue.add(floor);
        }
    }

    @Override
    public void start() {
        while (!upQueue.isEmpty() || !downQueue.isEmpty()) {
            if (direction == Direction.IDLE) {
                direction = !upQueue.isEmpty() ? Direction.UP : Direction.DOWN;
            }

            if (direction == Direction.UP) {
                moveUp();
                direction = downQueue.isEmpty() ? Direction.IDLE : Direction.DOWN;
            } else if (direction == Direction.DOWN) {
                moveDown();
                direction = upQueue.isEmpty() ? Direction.IDLE : Direction.UP;
            }
        }

        System.out.println("All requests completed. Elevator is now IDLE.");
    }

    private void moveUp() {
        System.out.println("Moving UP...");
        for (int floor : new TreeSet<>(upQueue)) {
            while (currentFloor < floor) {
                currentFloor++;
                printStatus();
                sleep();
            }
            stopAtFloor();
            upQueue.remove(floor);
        }
    }

    private void moveDown() {
        System.out.println("Moving DOWN...");
        for (int floor : new TreeSet<>(downQueue)) {
            while (currentFloor > floor) {
                currentFloor--;
                printStatus();
                sleep();
            }
            stopAtFloor();
            downQueue.remove(floor);
        }
    }

    private void stopAtFloor() {
        System.out.println("Stopping at floor " + currentFloor + ". Doors opening...");
        sleep(); // 2 seconds stop
        System.out.println("Doors closing...");
    }

    private void sleep() {
        try {
            Thread.sleep(2000); // 2 seconds
        } catch (InterruptedException e) {
            System.out.println("Elevator interrupted during sleep.");
        }
    }

    private void printStatus() {
        System.out.println("Current floor: " + currentFloor + " | Direction: " + direction);
    }
}

public class ElevatorSimulationWithInput {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Elevator elevator = new Elevator();

        System.out.println("=== Elevator Simulation Started ===");
        System.out.println("Type 'start' to run elevator, or continue adding requests.");

        while (true) {
            System.out.print("\nEnter request type (inside/outside/start): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("start")) {
                break;
            }

            if (input.equals("inside")) {
                System.out.print("Enter destination floor: ");
                int floor = scanner.nextInt();
                scanner.nextLine(); // consume newline
                elevator.requestFloor(floor);
            } else if (input.equals("outside")) {
                System.out.print("Enter floor number: ");
                int floor = scanner.nextInt();
                scanner.nextLine(); // consume newline

                System.out.print("Enter direction (up/down): ");
                String dir = scanner.nextLine().trim().toLowerCase();

                if (dir.equals("up")) {
                    elevator.requestFromOutside(floor, Direction.UP);
                } else if (dir.equals("down")) {
                    elevator.requestFromOutside(floor, Direction.DOWN);
                } else {
                    System.out.println("Invalid direction. Please enter 'up' or 'down'.");
                }
            } else {
                System.out.println("Invalid request type. Please enter 'inside', 'outside' or 'start'.");
            }
        }

        elevator.start();
        scanner.close();
    }
}
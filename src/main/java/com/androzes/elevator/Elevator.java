package com.androzes.elevator;

import java.util.TreeSet;

public class Elevator {

	public static final int MAX_FLOORS = 10;

	public static final int TIME_MOVE_FLOOR = 1000;
	public static final int TIME_DOOR_OPEN = 500;
	public static final int TIME_DOOR_CLOSE = 500;

	public Direction currentDirection;
	public State currentState;
	public int currentFloor;

	TreeSet<Integer> requests;
	TreeSet<Integer> otherRequests;

	private int moveFloorTime;
	private int doorOpenTime;
	private int doorCloseTime;

	public Elevator() {
		this.currentState = State.IDLE;
		this.currentFloor = 0;
		this.currentDirection = Direction.UP;
		this.requests = new TreeSet<Integer>();
		this.otherRequests = new TreeSet<Integer>();
		this.setMoveDurations(new int[]{TIME_MOVE_FLOOR, TIME_DOOR_OPEN, TIME_DOOR_CLOSE});
	}

	public void setMoveDurations(int[] times) {
		if (times.length != 3) {
			throw new IllegalArgumentException("Invalid times");
		}
		this.moveFloorTime = times[0];
		this.doorOpenTime = times[1];
		this.doorCloseTime = times[2];
	}

	public Direction getCurrentDirection() {
		return currentDirection;
	}

	public void setCurrentDirection(Direction currentDirection) {
		this.currentDirection = currentDirection;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	private Integer getDestinationFloor() {
		Integer destFloor;

		// find next stop in current direction
		if (currentDirection == Direction.UP) {
			// get the least floor greater than current floor
			destFloor = requests.ceiling(currentFloor);
		} else {
			// get the greatest floor less than current floor
			destFloor = requests.floor(currentFloor);
		}

		// if not found, find stop in other direction
		if (destFloor == null) {
			requests.addAll(otherRequests);
			otherRequests.clear();
			if (currentDirection == Direction.UP) {
				// get the greatest floor less than current floor
				destFloor = requests.floor(currentFloor);
			} else {
				// get the least floor greater than current floor
				destFloor = requests.ceiling(currentFloor);
			}
		}

		return destFloor;
	}

	private int getNextFloor() {
		Integer destFloor = getDestinationFloor();
		if (destFloor == null || destFloor == currentFloor) {
			return currentFloor;
		} else if (destFloor < currentFloor) {
			return currentFloor - 1;
		} else {
			return currentFloor + 1;
		}
	}

	public void run() throws InterruptedException {
		System.out.println("Elevator started");
		while (true) {

			// if stops are empty, sleep for x seconds
			if (requests.isEmpty() && otherRequests.isEmpty()) {
				System.out.println("Nowhere to go. Elevator waiting at " + currentFloor);
				Thread.sleep(moveFloorTime);
				continue;
			}

			System.out.println("Current stops: " + requests + " other stops: " +  otherRequests);
			moveToFloor(getNextFloor());

			Integer destFloor = getDestinationFloor();
			if (destFloor != null && currentFloor == destFloor) {
				requests.remove(destFloor);
				openDoor();
				closeDoor();
			}
		}
	}

	public void runSimple() throws InterruptedException {
		System.out.println("Elevator started in simple mode");
		while (true) {
			// if stops are empty, sleep for x seconds
			if (requests.isEmpty()) {
				currentState = State.IDLE;
				drawElevator();
				Thread.sleep(moveFloorTime);
				continue;
			}

			System.out.println("Current stops: " + requests);
			Integer destFloor = null;

			if (currentDirection == Direction.UP) {
				destFloor = requests.pollFirst();
				for (int i = currentFloor + 1; i <= destFloor; i++) {
					moveToFloor(i);
					if (checkForNewRequests(destFloor)) {
						break;
					}
				}

			} else {
				destFloor = requests.pollFirst();
				for (int i = currentFloor - 1; i >= destFloor; i--) {
					moveToFloor(i);
					if (checkForNewRequests(destFloor)) {
						break;
					}
				}
			}

			if (destFloor != null && currentFloor == destFloor) {
				openDoor();
				closeDoor();
			}
		}
	}

	public boolean checkForNewRequests(int currentDestinationFloor) {
		if (requests.isEmpty()) return false;

		if (currentDirection == Direction.UP) {
			int nextRequestFloor = requests.pollFirst();
			if (nextRequestFloor == currentDestinationFloor) {
				return false;
			} else if (nextRequestFloor < currentDestinationFloor) {
				requests.add(nextRequestFloor);
				requests.add(currentDestinationFloor);
				return true;
			}
			requests.add(nextRequestFloor);

		} else {
			int nextRequestFloor = requests.pollLast();
			if (nextRequestFloor == currentDestinationFloor) {
				return false;
			} else if (nextRequestFloor > currentDestinationFloor) {
				requests.add(currentDestinationFloor);
				return true;
			}
			requests.add(nextRequestFloor);
		}

		return false;
	}

	public void moveToFloor(int floor) throws InterruptedException {
		if(floor < 0) throw new IllegalArgumentException("Negative floor not allowed");

		System.out.println("Moving " + currentFloor + " -> " + floor);

		if (floor == currentFloor) {
			currentState = State.WAITING;
			//System.out.println("Already at floor " + currentFloor);
			drawElevator();
			return;
		}

		if (floor > currentFloor) {
			currentDirection = Direction.UP;
			currentState = State.MOVING;
			for (int i = currentFloor + 1; i <= floor; i++) {
				Thread.sleep(moveFloorTime);
				currentFloor = i;
				drawElevator();
				//System.out.println("Reached floor " + currentFloor);
			}
		} else {
			currentDirection = Direction.DOWN;
			currentState = State.MOVING;
			for (int i = currentFloor - 1; i >= floor; i--) {
				Thread.sleep(moveFloorTime);
				currentFloor = i;
				drawElevator();
				//System.out.println("Reached floor " + currentFloor);
			}
		}
	}

	public void openDoor() throws InterruptedException {
		currentState = State.WAITING;
		System.out.println("Opening Door at floor " + currentFloor);
		Thread.sleep(doorOpenTime);
		
	}

	public void closeDoor() throws InterruptedException {
		currentState = State.WAITING;
		System.out.println("Closing Door at floor " + currentFloor);
		Thread.sleep(doorCloseTime);
	}

	public void dropRequest(int floor) {
		requests.add(floor);
	}

	public void pickupRequest(int sourceFloor, Direction dir) {
		if (currentDirection == dir) {
			requests.add(sourceFloor);
		} else {
			otherRequests.add(sourceFloor);
		}
	}

	public void addRequest(Request request) {
		requests.add(request.getPickupRequest().getFloor());
		requests.add(request.getDropRequest().getDropFloor());
	}

	public boolean isIdle() {
		return currentState == State.IDLE;
	}

	public boolean isMoving() {
		return currentState == State.MOVING;
	}

	public boolean canSwitchDirection() {
		return requests.isEmpty() && isIdle();
	}

	public void toggleDirection() {
		if (currentDirection == Direction.UP) {
			currentDirection = Direction.DOWN;
		} else {
			currentDirection = Direction.UP;
		}
	}

	public void drawElevator() {
		System.out.println("");

		StringBuilder sb = new StringBuilder();
		StringBuilder fl = new StringBuilder();
		for(int i=0; i<=MAX_FLOORS; i++) {
			fl.append(String.format("%2d ", i));
			if (i == currentFloor) {
				sb.append("xx");
			} else {
				sb.append("--");
			}
			sb.append(" ");
		}

		System.out.println(fl.toString());
		System.out.println(sb.toString());
		
	}

}
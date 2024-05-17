package com.androzes.elevator;

import java.util.PriorityQueue;
import java.util.Queue;

public class Elevator2 {

	public static int NULL_FLOOR = -1;
	public static final int MAX_FLOOR = 10;
	public static final int MIN_FLOOR = 0;

	public static final int TIME_MOVE_FLOOR = 1000;
	public static final int TIME_DOOR_OPEN = 500;
	public static final int TIME_DOOR_CLOSE = 500;

	public String id;
	public State currentState;
	public Direction currentDirection;
	public int currentFloor;
	Queue<Request> upRequests;
	Queue<Request> downRequests;

	private int moveFloorTime;
	private int doorOpenTime;
	private int doorCloseTime;

	public Elevator2(String id) {
		this.id = id;
		this.currentState = State.IDLE;
		this.currentFloor = 0;
		this.currentDirection = Direction.UP;
		this.upRequests = new PriorityQueue<>(RequestComparator.withNormalOrder());
		this.downRequests = new PriorityQueue<>(RequestComparator.withReverseOrder());
		this.setMoveDurations(new int[]{TIME_MOVE_FLOOR, TIME_DOOR_OPEN, TIME_DOOR_CLOSE});
	}

	public int getNextDestFloor() {
		if (isGoingUp()) {
			while(upRequests.size() > 0) {
				Request nextDestReq = upRequests.peek();
				int nextDestFloor = nextDestReq.getPickupRequest().getFloor();
				if (nextDestFloor >= currentFloor) {
					return nextDestFloor;	
				}
				downRequests.offer(upRequests.poll());
			}
			return NULL_FLOOR;
		} else if (isGoingDown()) {
			while(downRequests.size() > 0) {
				Request nextDestReq = downRequests.peek();
				int nextDestFloor = nextDestReq.getPickupRequest().getFloor();
				if (nextDestFloor <= currentFloor) {
					return nextDestFloor;
				}
				upRequests.offer(downRequests.poll());
			}
			return NULL_FLOOR;
		}

		return NULL_FLOOR;
	}

	public int distanceToRequest(Request request) {
		int requestFloor = request.getPickupRequest().getFloor();
		Direction requestDirection = request.getPickupRequest().getDirection();

		if (isRequestInDirection(request)) {
			return Math.abs(currentFloor - requestFloor);
		}

		if (isGoingUp())
			if (requestDirection == Direction.DOWN) 
				return 2*MAX_FLOOR - requestFloor - currentFloor;
			else
				return 2*MAX_FLOOR - currentFloor + requestFloor;
		
		if (isGoingDown())
			if (requestDirection == Direction.UP)
				return requestFloor + currentFloor;
			else 
				return 2*MAX_FLOOR + currentFloor - requestFloor;

		return Integer.MAX_VALUE;
		
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
		return this.currentDirection;
	}

	public void addDestRequest(Request request) {
		int floor = request.getPickupRequest().getFloor();
		if(isGoingUp()) {
			if (floor >= currentFloor) {
				upRequests.offer(request);
			} else {
				downRequests.offer(request);
			}
		} else if (isGoingDown()) {
			if (floor <= currentFloor) {
				downRequests.offer(request);
			} else {
				upRequests.offer(request);
			}
		} else {
			if (floor >= currentFloor) {
				upRequests.offer(request);
			} else {
				downRequests.offer(request);
			}
		}
	}

	public State getCurrentState() {
		return currentState;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public boolean isRequestInDirection(Request request) {
		int requestFloor = request.getPickupRequest().getFloor();
		Direction requestDirection = request.getPickupRequest().getDirection();
		if (isGoingUp() && requestDirection == Direction.UP && requestFloor >= currentFloor) {
			return true;
		} else if (isGoingDown() && requestDirection == Direction.DOWN && requestFloor <= currentFloor) {
			return true;
		}
		return false;
	}

	private boolean isGoingUp() {
		return currentDirection == Direction.UP;
	}

	private boolean isGoingDown() {
		return currentDirection == Direction.DOWN;
	}
 
	private int getNextFloor() {
		if (isGoingDown()) {
			return currentFloor - 1;
		} else {
			return currentFloor + 1;
		}
	}

	private void reverseDirection() {
		if (this.currentFloor == MIN_FLOOR) {
			this.currentDirection = Direction.UP;
		} else if (this.currentFloor == MAX_FLOOR) {
			this.currentDirection = Direction.DOWN;
		} else {
			this.currentDirection = this.currentDirection == Direction.UP ? Direction.DOWN: Direction.UP;
		}
	}

	private Request removeRequest() {
		return isGoingDown() ? downRequests.poll() : upRequests.poll();
	}

	public void run() throws InterruptedException {
		System.out.println("Elevator: " + id + " started");
		drawElevator();
		while (true) {
			int nextDestFloor = getNextDestFloor();
			if (nextDestFloor == NULL_FLOOR) {
				//System.out.println("Reversing direction for " + this);
				reverseDirection();
				continue;
			}

			if (nextDestFloor == currentFloor) {
				openDoor();
				closeDoor();
				
				Request removedRequest = removeRequest();
				int removedFloor = removedRequest.getPickupRequest().getFloor();
				if (removedFloor != currentFloor) {
					throw new RuntimeException("removedFloor: " + removedFloor + " is not same as currentFloor: " + currentFloor);
				}

				handleCompletedRequest(removedRequest);
				continue;
			}

			int nextFloor = getNextFloor();			
			moveToFloor(nextFloor);	
		}
	}

	public void moveToFloor(int floor) throws InterruptedException {
		if(floor < 0) throw new IllegalArgumentException("Negative floor not allowed");

		if (floor == currentFloor) {
			currentState = State.IDLE;
			return;
		}

		currentState = State.MOVING;
		if (floor > currentFloor) {
			for (int i = currentFloor + 1; i <= floor; i++) {
				Thread.sleep(moveFloorTime);
				currentFloor = i;
				//System.out.println("Reached floor " + currentFloor);
			}
		} else {
			for (int i = currentFloor - 1; i >= floor; i--) {
				Thread.sleep(moveFloorTime);
				currentFloor = i;
				//System.out.println("Reached floor " + currentFloor);
			}
		}

		drawElevator();
	}

	public void openDoor() throws InterruptedException {
		currentState = State.DOOR_OPENING;
		drawElevator();
		Thread.sleep(doorOpenTime);
		
	}

	public void closeDoor() throws InterruptedException {
		currentState = State.DOOR_CLOSING;
		drawElevator();
		Thread.sleep(doorCloseTime);
	}

	private void handleCompletedRequest(Request request) {
		DropRequest dropRequest = request.getDropRequest();
		if (dropRequest == null) {
			return;
		}
		request.setPickupRequest(new PickRequest(dropRequest.getDropFloor(), currentDirection));
		request.setDropRequest(null);
		addDestRequest(request);
		System.out.println("Added drop: " +  dropRequest + " to " +  this);
	}

	public boolean isIdle() {
		return currentState == State.IDLE;
	}

	public boolean isMoving() {
		return currentState == State.MOVING;
	}

	public void drawElevator() {
		System.out.println("");

		StringBuilder elevatorLine = new StringBuilder();
		StringBuilder floorGuide = new StringBuilder();

		elevatorLine = elevatorLine.append(id + " ");
		floorGuide = floorGuide.append(" ".repeat(id.length()+1));

		for(int i=0; i<=MAX_FLOOR; i++) {
			floorGuide = floorGuide.append(String.format("%2d ", i));
			if (i == currentFloor) {
				if (currentState == State.DOOR_OPENING) {
					elevatorLine = elevatorLine.append("<>");
				} else if (currentState == State.DOOR_CLOSING) {
					elevatorLine = elevatorLine.append("><");
				} else {
					elevatorLine = elevatorLine.append(currentDirection == Direction.UP ? "^" : "v");
					elevatorLine = elevatorLine.append("x");
				}
			} else {
				elevatorLine = elevatorLine.append("--");
			}
			elevatorLine = elevatorLine.append(" ");
		}

		System.out.println(floorGuide.toString());
		System.out.println(elevatorLine.toString());
		
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb = sb.append("Elevator " + id);

		// add direction
		sb = sb.append(" (" + currentFloor + ", " + currentDirection.name() + ") --- ");

		sb = sb.append("Down Req:" + downRequests + ", ");
		sb = sb.append("UP Req:" + upRequests);
		return sb.toString();
	}

}
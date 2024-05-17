package com.androzes.elevator;

import java.util.TreeSet;

public class ElevatorManager {

	
	Elevator elevator;

	TreeSet<Request> upPendingRequests;
	TreeSet<Request> downPendingRequests;

	public  ElevatorManager(Elevator e) {
		this.elevator = e;
		this.upPendingRequests = new TreeSet<Request>();
		this.downPendingRequests = new TreeSet<Request>();
	}

	public void runElevator() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					elevator.runSimple();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						assignPendingRequests();
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void assignPendingRequests() {
		if (elevator.canSwitchDirection() && (!downPendingRequests.isEmpty() || !upPendingRequests.isEmpty())) {
			if (elevator.getCurrentDirection() == Direction.UP) {

				TreeSet<Request> tempSet = downPendingRequests;
				downPendingRequests = new TreeSet<>();
				elevator.setCurrentDirection(Direction.DOWN);

				while(!tempSet.isEmpty()){
					Request req = tempSet.pollLast();
					addRequest(req);
				}
			} else {
				TreeSet<Request> tempSet = upPendingRequests;
				upPendingRequests = new TreeSet<>();
				elevator.setCurrentDirection(Direction.UP);

				while (!tempSet.isEmpty()) {
					Request req = tempSet.pollFirst();
					addRequest(req);
				}
			}
		}
	}

	private String printTreeSet(TreeSet<Request> set) {
		StringBuffer sb = new StringBuffer();
		for (Object req : set.toArray()) {
			Request t = (Request) req;
			sb.append(t);
			sb.append(", ");
		}
		return sb.toString();
	}

	public void addRequest(Request request) {
		if (elevator.getCurrentDirection() == Direction.UP) {
			addUpRequest(request);
		} else {
			addDownRequest(request);
		}
	}

	private void addUpRequest(Request request) {
		PickRequest pickupRequest = request.getPickupRequest();
		if (pickupRequest.getDirection() == Direction.UP) {
			if (pickupRequest.getFloor() >= elevator.getCurrentFloor() && !elevator.isMoving()) {
				System.out.println("Add request " + request + " to elevator");
				elevator.addRequest(request);
			} else {
				System.out.println("Add request " + request + " to up pending requests");
				upPendingRequests.add(request);
			}
		} else {
			System.out.println("Add request " + request + " to down pending requests");
			downPendingRequests.add(request);
		}
	}

	private void addDownRequest(Request request) {
		PickRequest pickupRequest = request.getPickupRequest();
		if (pickupRequest.getDirection() == Direction.DOWN) {
			if (pickupRequest.getFloor() <= elevator.getCurrentFloor() && !elevator.isMoving()) {
				System.out.println("Add request " + request + " to elevator");
				elevator.addRequest(request);
			} else {
				System.out.println("Add request " + request + " to down pending requests");
				downPendingRequests.add(request);
			}
		} else {
			System.out.println("Add request " + request + " to up pending requests");
			upPendingRequests.add(request);
		}
	}

	public void requestPickup(int sourceFloor, Direction direction) {
		System.out.println("Pickup at " + sourceFloor + " " + direction.name());
		elevator.pickupRequest(sourceFloor, direction);
	}

	public void requestDrop(int destFloor) {
		System.out.println("Drop at " + destFloor);
		elevator.dropRequest(destFloor);
	}

	

}
package com.androzes.elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class MultiElevatorManager {

	public static final int MAX_FLOORS = 10;

	List<Elevator2> elevators;
	TreeSet<Request> pendingRequests;

	public MultiElevatorManager() {
		this.elevators = new ArrayList<>();
		this.pendingRequests = new TreeSet<Request>();
	}

	public void addElevator(Elevator2 el) {
		elevators.add(el);
	}

	public Elevator2 getNearestElevator(Request request) {
		Elevator2 minElevator = null;
		int minDistance = Integer.MAX_VALUE;
		
		for(Elevator2 elevator : elevators) {
			int distance = elevator.distanceToRequest(request);

			System.out.println("distance(" + distance + ") to " + elevator + " for request: " + request);

			if (distance < minDistance) {
				minElevator = elevator;
				minDistance = distance;
			}
		}

		return minElevator;
	}

	public void runElevators() {
		if (elevators.size() == 0) throw new IllegalStateException("No elevators found");

		for (Elevator2 elevator: elevators) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						elevator.run();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	public void assignPendingRequests() {
		// System.out.println("Assign pending requests:" + pendingRequests.size());
		Request request = pendingRequests.pollFirst();

		while (request != null) {
			System.out.println("New Request: " + request);
			Elevator2 nearestElevator = getNearestElevator(request);
			nearestElevator.addDestRequest(request);
			System.out.println("Added Request: " + request + "to elevator: " + nearestElevator);

			request = pendingRequests.pollFirst();
		}
				
	}

	public void addRequest(Request request) {
		pendingRequests.add(request);
		//System.out.println("Pending requests in em: " + pendingRequests);
	}

}
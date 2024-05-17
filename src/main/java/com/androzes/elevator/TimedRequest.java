package com.androzes.elevator;

import java.util.TimerTask;

public class TimedRequest extends TimerTask {
	
	MultiElevatorManager em;
	Request request;

	public TimedRequest(MultiElevatorManager em, Request request) {
		this.em = em;
		this.request = request;
	}

	public void run() {
		//System.out.println("Adding request" + request + " to elevator Manager");
		em.addRequest(request);
	}
	
}
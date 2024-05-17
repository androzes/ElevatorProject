package com.androzes.elevator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            runMultiElevator();
            //runSingleElevator();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void runMultiElevator() throws InterruptedException {
        MultiElevatorManager em = new MultiElevatorManager();

        Elevator2 el1 = new Elevator2("A");
        Elevator2 el2 = new Elevator2("B");
        em.addElevator(el1);
        em.addElevator(el2);


        em.runElevators();

        Timer timer = new Timer();
        TimerTask assignPendingRequestsTask = new TimerTask() {
            public void run() {
                em.assignPendingRequests();
            }
        };
        timer.schedule(assignPendingRequestsTask, 0, 200);

        int[][] requests = new int[][]{
            // { time, pickupFloor, direction(0-UP, 1-DOWN), dropFloor }
            {0, 3, 0, 5},
            {0, 1, 1, 0},
            {0, 2, 0, 6},
            {6000, 3, 0, 3},
            {14000, 7, 0, 8}
        };

        for(int[] request: requests) {
            Direction dir = request[2] == 1 ? Direction.DOWN : Direction.UP;
            TimedRequest req = new TimedRequest(em, new Request(new PickRequest(request[1], dir), new DropRequest(request[3])));
            timer.schedule(req, request[0]);
        }

        //Thread.sleep(3600)
        System.out.println("Main finished");

    }

    public static void runSingleElevator() throws InterruptedException {
        ElevatorManager em = new ElevatorManager(new Elevator());

        Request request1 = new Request(new PickRequest(3, Direction.UP), new DropRequest(5));
        Request request2 = new Request(new PickRequest(1, Direction.DOWN), new DropRequest(0));
        Request request3 = new Request(new PickRequest(2, Direction.UP), new DropRequest(6));
        Request request4 = new Request(new PickRequest(7, Direction.UP), new DropRequest(8));
        Request request5 = new Request(new PickRequest(3, Direction.UP), new DropRequest(3));

        em.runElevator();

        em.addRequest(request1);
        em.addRequest(request2);
        em.addRequest(request3);

        Thread.sleep(6000);
        em.addRequest(request5);

        Thread.sleep(8000);
        em.addRequest(request4);   
    }


}

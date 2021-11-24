package com.androzes.elevator;

import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

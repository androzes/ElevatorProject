package com.androzes.elevator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ElevatorTest2
{
    Elevator e;

    @Before
    public void setUp() {
        e = new Elevator();
        e.currentFloor = 1;
        e.setMoveDurations(new int[]{0,0,0});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveToFloorException() throws IllegalArgumentException, InterruptedException{
        int destFloor = -1;
        e.moveToFloor(destFloor);
    }

}

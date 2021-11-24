package com.androzes.elevator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ElevatorTest1
{
    Elevator e;

    @Before
    public void setUp() {
        e = new Elevator();
        e.currentFloor = 1;
        e.setMoveDurations(new int[]{0,0,0});
    }

    @Test
    public void testMoveToFloor() {
        try {
            int destFloor = 5;
            e.moveToFloor(destFloor);
            assertEquals(destFloor, e.currentFloor);
        } catch (InterruptedException e) {
            fail("move to floor should not throw exception");
        }
    }

}

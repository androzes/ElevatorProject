package com.androzes.elevator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ElevatorTest
{
    Elevator e;
    int startFloor;
    int destFloor;

    public ElevatorTest(int startFloor, int destFloor) {
        this.startFloor = startFloor;
        this.destFloor = destFloor;
    }

    @Parameters
    public static Collection inputs() {
        return Arrays.asList(new Integer[][] {
                {0, 1},
                {3, 8},
                {5, 1},
                {6, 6},
                {0, 0},
                {0, 3}
        });
    }

    @Before
    public void setUp() {
        e = new Elevator();
        e.currentFloor = startFloor;
        e.setMoveDurations(new int[]{0,0,0});
    }

    @Test
    public void testMoveToFloor() {
        try {
            e.moveToFloor(destFloor);
            assertEquals(destFloor, e.currentFloor);
        } catch (InterruptedException e) {
            fail("move to floor should not throw exception");
        }
    }

}

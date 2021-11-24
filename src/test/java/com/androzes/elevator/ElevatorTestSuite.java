package com.androzes.elevator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        ElevatorTest1.class,
        ElevatorTest2.class
})

/**
 *  run this test suite with following command
 *      mvn test -Dtest=com.androzes.elevator.ElevatorTestSuite
 */

public class ElevatorTestSuite
{

}

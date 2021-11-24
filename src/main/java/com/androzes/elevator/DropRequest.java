package com.androzes.elevator;

public class DropRequest {
    int destFloor;

    public DropRequest(int destFloor) {
        this.destFloor = destFloor;
    }

    public int getDestinationFloor() {
        return destFloor;
    }
}

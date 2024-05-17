package com.androzes.elevator;

public class DropRequest {
    int dropFloor;

    public DropRequest(int destFloor) {
        this.dropFloor = destFloor;
    }

    public int getDropFloor() {
        return dropFloor;
    }

    public String toString() {
        return "D(" + getDropFloor() + ")";
    }
}

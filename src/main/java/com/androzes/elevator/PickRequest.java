package com.androzes.elevator;

public class PickRequest {
    int sourceFloor;
    Direction direction;

    public PickRequest(int sourceFloor, Direction d) {
        this.sourceFloor = sourceFloor;
        this.direction = d;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getFloor() {
        return sourceFloor;
    }

    public void setSourceFloor(int sourceFloor) {
        this.sourceFloor = sourceFloor;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String toString() {
        return "P(" + getFloor() + "," + getDirection().name() + ")";
    }
}

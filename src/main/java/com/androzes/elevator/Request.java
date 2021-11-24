package com.androzes.elevator;

public class Request implements Comparable<Request>{
    PickRequest pickupRequest;
    DropRequest dropRequest;

    public Request(PickRequest pr, DropRequest dr) {
        this.pickupRequest = pr;
        this.dropRequest = dr;
    }

    public PickRequest getPickupRequest() {
        return pickupRequest;
    }

    public void setPickupRequest(PickRequest pickupRequest) {
        this.pickupRequest = pickupRequest;
    }

    public DropRequest getDropRequest() {
        return dropRequest;
    }

    public void setDropRequest(DropRequest dropRequest) {
        this.dropRequest = dropRequest;
    }

    public int compareTo(Request other) {
        return this.pickupRequest.getSourceFloor() - other.getPickupRequest().getSourceFloor();
    }

    public String toString() {
        return "[ P(" + pickupRequest.getSourceFloor()+","+pickupRequest.getDirection().name()+")"+
                ", D("+dropRequest.getDestinationFloor()+") ]";
    }
}

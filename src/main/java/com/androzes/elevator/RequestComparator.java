package com.androzes.elevator;

import java.util.Comparator;

public class RequestComparator implements Comparator<Request> {
    
    boolean descOrder;

    private RequestComparator(boolean descOrder) {
        this.descOrder = descOrder;
    }

    public static RequestComparator withNormalOrder() {
        return new RequestComparator(false);
    }
    
    public static RequestComparator withReverseOrder() {
        return new RequestComparator(true);
    }

    @Override
    public int compare(Request o1, Request o2) {
        int diff = o1.getPickupRequest().getFloor() - o2.getPickupRequest().getFloor(); 
        return descOrder ? -diff : diff;
    }
    
}

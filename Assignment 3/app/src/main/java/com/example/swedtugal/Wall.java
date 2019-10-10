package com.example.swedtugal;

public class Wall {

    private Location startLocation, endLocation;

    Wall(int startX, int startY, int endX, int endY) {
        this(new Location(startX, startY), new Location(endX, endY));
    }

    public Wall(Location startLocation, Location endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    Location getStartLocation() {
        return startLocation;
    }

    Location getEndLocation() {
        return endLocation;
    }
}

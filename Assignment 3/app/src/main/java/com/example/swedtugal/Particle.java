package com.example.swedtugal;

public class Particle {
    private Location currentLocation, previousLocation;
    private int liveTime;

    Particle(int x, int y, int liveTime) {
        this(new Location(x, y), liveTime);
    }

    public Particle (Location location, int liveTime) {
        this.currentLocation = location;
        try {
            this.previousLocation = (Location) location.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.liveTime = liveTime;
    }

    public int getLiveTime() {
        return this.liveTime;
    }

    private void incLiveTime() {
        this.liveTime++;
    }

    Location getCurrentLocation() {
        return this.currentLocation;
    }

    Location getPreviousLocation() {
        return this.previousLocation;
    }

    void setCurrentLocation(Location currentLocation) { this.currentLocation = currentLocation; }

    void setPreviousLocation(Location previousLocation) { this.previousLocation = previousLocation; }

    void updateLocation(int x, int y) {
        try {
            this.previousLocation = (Location) currentLocation.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.currentLocation.setX(x);
        this.currentLocation.setY(y);
    }

    public void updateLocation(Location newLocation) {
        try {
            this.previousLocation = (Location) currentLocation.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.currentLocation = newLocation;
    }
}

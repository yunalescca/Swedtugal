package com.example.swedtugal;

public class Location implements Cloneable {

    private int x, y;

    Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Location(Location location) {
        this(location.getX(), location.getY());
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
    }

    void setX(int x) {
        this.x = x;
    }

    void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Location)) return false;
        if (o == this) return true;

        Location l = (Location) o;

        return this.getX() == l.getX() && this.getY() == l.getY();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

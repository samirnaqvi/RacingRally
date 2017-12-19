package com.game.model;

public class CarStats {
    final int maxSpeed;
    final int offRoad;
    final int strength;

    public CarStats(int maxSpeed, int offRoad, int strength) {
        this.maxSpeed = maxSpeed;
        this.offRoad = offRoad;
        this.strength = strength;
    }
/*Below method are each used to return attributes about each car, which is seen in the Car Selection menu, as the Max Speed, Off-Road Capability, and Strength of the car*/
    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getOffRoad() {
        return offRoad;
    }

    public int getStrength() {
        return strength;
    }
    public String toString(){
        return "Max speed: "+maxSpeed+"\n"+
                "Off road capability: "+offRoad+"\n"+
                "Strength: "+strength;
    }
}

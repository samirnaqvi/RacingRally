package com.game.model;

import com.game.model.enums.TerrainType;

public class CarEngine {
    private double acceleration;
    private double currentSpeed;
    private double onTerrain;
    private int accelerateCycle = 1;
    /* Must be in [1..10] range */
    private TerrainType terrainType;
    private int processorStatus = 0;
    private double maxSpeed;
    private int timer; //1000 == 1 second.
    /* if processor Thread in getProcessor speed
    already works, then processorStatus=1.
     */


    public CarEngine(double defaultCarAcceleration) {
        this.acceleration = defaultCarAcceleration;
        onTerrain = 1; //by default car spawns at road.
        processMaxSpeed();
        timer = 100;
    }

    private void processMaxSpeed() {
        maxSpeed = Math.round((acceleration - 0.901) * 100 * onTerrain);//used to calculate the max speed of car
    }

    public double getMaxSpeed() {
        processMaxSpeed();
        return maxSpeed;//returns max speed of car
    }

    private void processSpeed() {
        //utilizes an acceleration algorithm to accelerate the car in order to increase speed
        if (currentSpeed != maxSpeed && processorStatus == 0) {
            processorStatus = 1;
            double tmpAcceleration = acceleration;
            Thread speedProcessor = new Thread(new Runnable() {
                public void run() {
                    System.out.println("Process created");
                    for (int i = accelerateCycle; i <= 10; i++) {
                        try {
                            Thread.sleep(timer);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        currentSpeed = acceleration * i * i * onTerrain;
                        System.out.println(String.format("On %d cycle, speed is: %f", accelerateCycle, currentSpeed));
                        acceleration -= 0.1;
                        System.out.println(acceleration + " acceleration ");
                        accelerateCycle++;
                        processorStatus = 0;
                        accelerateCycle = 1;
                    }
                }
            });
            speedProcessor.start();
            try {
                speedProcessor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            acceleration = tmpAcceleration;
            System.out.println(currentSpeed + " after processing");
            currentSpeed = Math.round(currentSpeed);

        } else currentSpeed = Math.round(currentSpeed);
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setTerrainType(TerrainType terrainType) {
        if (terrainType == TerrainType.ROAD) onTerrain = 1;
        if (terrainType == TerrainType.GROUND) onTerrain = 0.75;//accounts for slower speed on an offroad track
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
        accelerateCycle = 1; //set as default to reprocess right value.
    }

    public void appendAcceleration(double accelerationToAppend) {
        this.acceleration += accelerationToAppend;
    }


    public double getCurrentSpeed() {
        if (processorStatus == 0) processSpeed();
        return currentSpeed;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public double getOnTerrain() {
        return onTerrain;
    }

}

package com.game.model.objects;

import com.game.model.CarEngine;
import com.game.model.User;
import javafx.scene.image.Image;

public class Car extends GameObject {

    private String carName;
    private Image carModel;
    private CarEngine engine;
    private User user;

    public Image getCarModel() {
        return carModel;
    }

    /*This Constructor displays the main 4 attributes of the car we are most concerned with in the game, the Name, Model, Engine, and the user.*/
    public Car(String carName, Image carModel, CarEngine engine, User user) {
        this.carName = carName;
        this.carModel = carModel;
        this.engine = engine;
        this.user = user;
    }

    @Override
    public String toString() {
        return "Car{" +
                "carName='" + carName + '\'' +
                ", user=" + user +
                '}';
    }
}

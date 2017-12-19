package com.game.view;

import com.game.Main;
import com.game.controller.DataController;
import com.game.model.CarEngine;
import com.game.model.CarStats;
import com.game.model.User;
import com.game.model.objects.Car;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;

public class CarMenuView {
    private int currentCounter = 0;
    private int maxCounter = 0;
    static private boolean music = false;

    public Scene getScene() {// This method imports all the media files necessary for thr car menu view andbasically demonstrates how it will look
        try {
            StackPane stackPane = new StackPane();
            stackPane.setMinSize(400, 550);
            Button nextCar = new Button();
            Button lastCar = new Button();
            final AudioClip music_background = new AudioClip(Main.class.getResource("/carmenu.wav").toURI().toString()); //audio
            final AudioClip soundClick = new AudioClip(Main.class.getResource("/click.mp3").toURI().toString());
            if (!music) {
                music_background.play();
                music = true;
            }
            lastCar.setGraphic(new ImageView(new Image(Main.class.getResource("/leftArrow.png").toURI().toString()))); // arrow buttons
            nextCar.setGraphic(new ImageView(new Image(Main.class.getResource("/rightArrow.png").toURI().toString())));
            nextCar.setPrefSize(50, 50);
            lastCar.setPrefSize(50, 50);
            Button select = new Button("Select");
            Button mainMenu = new Button("Main menu");
            mainMenu.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    TitleView.changeScene(MainMenuView.getScene());
                    music_background.stop();
                    music = false;
                }
            });
            final Image[] cars = {
                    new Image(Main.class.getResource("/Aston_Martin.png").toURI().toString()),
                    new Image(Main.class.getResource("/Audi_R8.png").toURI().toString()), //cars
                    new Image(Main.class.getResource("/Dodge Viper.png").toURI().toString()),
                    new Image(Main.class.getResource("/Dodge_Challenger.png").toURI().toString()),
                    new Image(Main.class.getResource("/Buggati_Veyron.png").toURI().toString()),
                    new Image(Main.class.getResource("/Chevrole_Camaro.png").toURI().toString()),
            };
            final String[] nameOfCars = {"Aston Martin", "Audi R8", "Dodge Viper",
                    "Dodge Challenger", "Bugatti Veyron", "Chevrole Camaro"};
            final CarStats[] carStats = {new CarStats(110, 10, 10), new CarStats(120, 8, 9), new CarStats(115, 8, 10),
                    new CarStats(105, 11, 11), new CarStats(125, 8, 8), new CarStats(115, 9, 9)};
            final Label carStatsLabel = new Label(carStats[0].toString());
            final Label carName = new Label(nameOfCars[0]);
            carName.setStyle("-fx-font-size: 18pt;\n" +
                    "    -fx-font-family: \"Segoe UI Semibold\";\n" + // background and default settings
                    "    -fx-text-fill: red;\n" +
                    "    -fx-stroke-width: 2;\n" +
                    "    -fx-opacity: 1;");
            carStatsLabel.setStyle("-fx-font-size: 11pt;\n" +
                    "    -fx-font-family: \"Segoe UI Semibold\";\n" +
                    "    -fx-text-fill: white;\n" +
                    "    -fx-background-color: lightslategray;\n" +
                    "    -fx-opacity: 1;");
            final ImageView carViewer = new ImageView();
            carViewer.setImage(cars[0]);
            //by default showing first of array
            maxCounter = cars.length;
            //selecting process
            nextCar.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    soundClick.play();
                    if (currentCounter + 1 == maxCounter) currentCounter = -1;
                    carViewer.setImage(cars[++currentCounter]);
                    carName.setText(nameOfCars[currentCounter]);
                    carStatsLabel.setText(carStats[currentCounter].toString());

                }
            });
            lastCar.setOnAction(new EventHandler<ActionEvent>() { // Here, It plots every object on the screen
                public void handle(ActionEvent actionEvent) {
                    soundClick.play();
                    if (currentCounter == 0) currentCounter = maxCounter;
                    carViewer.setImage(cars[--currentCounter]);
                    carName.setText(nameOfCars[currentCounter]);
                    carStatsLabel.setText(carStats[currentCounter].toString());
                }
            });
            double coef = 1.93;
            double height = 300;
            carViewer.setFitHeight(height);
            carViewer.setFitWidth(height / coef);
            carViewer.setSmooth(true);
            carViewer.setCache(true);
            stackPane.getChildren().add(carStatsLabel);
            stackPane.getChildren().add(nextCar);
            stackPane.getChildren().add(lastCar);
            stackPane.getChildren().add(select);
            stackPane.getChildren().add(mainMenu);
            stackPane.getChildren().add(carViewer);
            stackPane.getChildren().add(carName);
            stackPane.setMargin(carName, new Insets(62, 0, 0, 0));
            select.setPrefSize(100, 50);
            mainMenu.setPrefSize(100, 50);
            stackPane.setAlignment(carViewer, Pos.CENTER);
            stackPane.setAlignment(nextCar, Pos.CENTER_RIGHT);
            stackPane.setAlignment(lastCar, Pos.CENTER_LEFT);
            stackPane.setAlignment(select, Pos.BOTTOM_RIGHT);
            stackPane.setAlignment(mainMenu, Pos.BOTTOM_LEFT);
            stackPane.setAlignment(carName, Pos.TOP_CENTER);
            stackPane.setAlignment(carStatsLabel, Pos.BOTTOM_CENTER);
            stackPane.setStyle("-fx-background-image: url('background_garage1.jpg');");
            select.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    soundClick.play();
                    DataController.car = new Car(nameOfCars[currentCounter], cars[currentCounter], new CarEngine(2), DataController.user);
                    DataController.enemyCar = new Car(nameOfCars[4], cars[4], new CarEngine(2), new User("Jackie"));
                    DataController.usLogger.log(DataController.car + " car selected");
                    music_background.stop();
                    music = false;
                    TitleView.changeScene(MapMenuView.getScene());
                }
            });

            return new Scene(stackPane);
        } catch (Exception e) {
            DataController.exLogger.log(e.getMessage());
            e.printStackTrace();
            return MainMenuView.getScene();
        }
    }
}

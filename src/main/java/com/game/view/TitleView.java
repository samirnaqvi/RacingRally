package com.game.view;

import com.game.Main;
import com.game.controller.DataController;
import com.game.model.CarEngine;
import com.game.model.User;
import com.game.model.objects.Car;
import com.game.model.objects.Map;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.net.URISyntaxException;

public class TitleView extends Application {
    //Launch method for debug-mode (instance of game with main JAVAFX thread
    public void launch() {
        Application.launch();
    }

    private static Stage currentStage;
    //Main method for changing Views
    public static void changeScene(Scene sceneToChange) {
        currentStage.setScene(sceneToChange);

    }

    @Override
    public void start(final Stage stage) {
        try {
            //Customizing game-window.
            currentStage = stage;
            stage.setWidth(405);
            stage.setHeight(570);
            StackPane stackPane = View.initialize(stage);
            final Label label = new Label("Cognitive Thought Media"); // First Title Screem
            label.setFont(Font.font("Impact", 36));
            stackPane.getChildren().add(label);
            stage.show();
            stage.setResizable(false);
            //Logging and closing application when clicked cross ('Exit') in top right corner.
            stage.setOnCloseRequest(
                    new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    DataController.usLogger.log("Game instance closed.\n");
                    System.exit(0);
                }
            });
            //Animation of 'SS developers'.
            FadeTransition ft = new FadeTransition(Duration.millis(2500), label);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setCycleCount(1);
            ft.setAutoReverse(true);
            ft.play();
            Thread runnable = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(5000);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                label.setText("SS Developers");
                                label.setFont(Font.font("Impact", 42));
                            }
                        });
                        //after 5 seconds (5000ms) change View for InitialView.
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        DataController.exLogger.log(e.getMessage());
                    }
                    Platform.runLater(new Runnable() {
                        public void run() {
                            changeScene(new InitialView().getScene());
                        }
                    });
                }
            });
            runnable.start();
        } catch (Exception e) {
            DataController.exLogger.log(e.getMessage());
            e.printStackTrace();
        }
    }

    /*
    Method for debug mode - insert this instead of 66--81 line for instant start of game.
    Currently has different parameters of map.
     */
    private static void loadTestData() {
        try {
            DataController.car = new Car("test",
                    new Image(Main.class.getResource("/Aston_Martin.png").toURI().toString()),
                    new CarEngine(2),
                    new User("guest"));
            DataController.enemyCar = new Car("AI",
                    new Image(Main.class.getResource("/Dodge_Challenger.png").toURI().toString()),
                    new CarEngine(2),
                    new User("AI"));
            DataController.difficulty = 3;
            DataController.map = new Map(6500,
                    "SinX",
                    new Image(Main.class.getResource("/map1.png").toURI().toString()));
        } catch (URISyntaxException e) {

            DataController.exLogger.log(e.getMessage());
        }
    }

}

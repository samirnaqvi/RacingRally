package com.game.view;

import com.game.Main;
import com.game.controller.DataController;
import com.game.model.objects.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;

import java.util.Optional;

public class MapMenuView {
    private static int currentCounter = 0;
    private static int maxCounter = 0;

    public static Scene getScene() { //This method displays the menu for when the user selects maps
        try {
            StackPane stackPane = new StackPane(); // Imports the audio files, creates the buttons
            Button nextMap = new Button();
            Button lastMap = new Button();
            final AudioClip soundClick = new AudioClip(Main.class.getResource("/click.mp3").toURI().toString());
            lastMap.setGraphic(new ImageView(new Image(Main.class.getResource("/leftArrow.png").toURI().toString())));
            nextMap.setGraphic(new ImageView(new Image(Main.class.getResource("/rightArrow.png").toURI().toString())));
            nextMap.setPrefSize(35, 50);
            lastMap.setPrefSize(35, 50);
            Button select = new Button("Select");
            Button mainMenu = new Button("Main menu");
            mainMenu.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    TitleView.changeScene(MainMenuView.getScene());
                }
            });
            final Image[] maps = {
                    new Image(Main.class.getResource("/map1.jpg").toURI().toString()),
                    new Image(Main.class.getResource("/map2.jpg").toURI().toString()),//maps
                    new Image(Main.class.getResource("/map3.jpg").toURI().toString()),
                    new Image(Main.class.getResource("/map4.jpg").toURI().toString()),
            };
            final Map[] createdMaps =
                    {new Map(13300,"SinX",maps[0]),
                            new Map(16200,"TanX",maps[1]),
                            new Map(13100,"SqrX",maps[2]),
                            new Map(15100,"SqrtX",maps[3])
                    };
            final ImageView mapViewer = new ImageView();
            mapViewer.setImage(maps[0]);
            //by default showing first of array
            maxCounter = maps.length;
            //selecting process
            nextMap.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    soundClick.play();
                    if (currentCounter + 1 == maxCounter) currentCounter = -1;
                    mapViewer.setImage(maps[++currentCounter]);

                }
            });
            lastMap.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    soundClick.play();
                    if (currentCounter == 0) currentCounter = maxCounter;
                    mapViewer.setImage(maps[--currentCounter]);

                }
            });
            double coef = 1;
            double height = 300; // sets the entire view of the page, position of maps, buttons etc
            mapViewer.setFitHeight(height);
            mapViewer.setFitWidth(height / coef);
            mapViewer.setSmooth(true);
            mapViewer.setCache(true);
            stackPane.getChildren().add(nextMap);
            stackPane.getChildren().add(lastMap);
            stackPane.getChildren().add(select);
            stackPane.getChildren().add(mainMenu);
            stackPane.getChildren().add(mapViewer);
            select.setPrefSize(100, 50);
            mainMenu.setPrefSize(100, 50);
            stackPane.setAlignment(mapViewer, Pos.CENTER);
            stackPane.setAlignment(nextMap, Pos.CENTER_RIGHT);
            stackPane.setAlignment(lastMap, Pos.CENTER_LEFT);
            stackPane.setAlignment(select, Pos.BOTTOM_RIGHT);
            stackPane.setAlignment(mainMenu, Pos.BOTTOM_LEFT);
            stackPane.setStyle("-fx-background-image: url('mapbg.jpg');");
            select.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) { // This method is for the pop up box asking what difficulty the user would like to play on
                    soundClick.play();
                    DataController.map = createdMaps[currentCounter];
                    DataController.usLogger.log(DataController.map.toString());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Difficulty");
                    alert.setHeaderText("Racing Rally 2D");
                    alert.setContentText("Select difficulty : ");
                    ButtonType buttonTypeOne = new ButtonType("Easy");
                    ButtonType buttonTypeTwo = new ButtonType("Medium");
                    ButtonType buttonTypeThree = new ButtonType("Hard");
                    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree, buttonTypeCancel);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonTypeOne){
                        soundClick.play();
                        DataController.difficulty=1;
                        TitleView.changeScene(GameView.getScene());
                    } else if (result.get() == buttonTypeTwo) {
                        soundClick.play();
                        DataController.difficulty=2;
                        TitleView.changeScene(GameView.getScene());
                    } else if (result.get() == buttonTypeThree) {
                        soundClick.play();
                        DataController.difficulty=3;
                        TitleView.changeScene(GameView.getScene());
                    } else {
                       TitleView.changeScene(MainMenuView.getScene());
                    }
                }
            });
            return new Scene(stackPane);
        } catch (Exception e) {
            DataController.exLogger.log(e.getMessage()); // Log The Error
            e.printStackTrace();
            return MainMenuView.getScene();
        }
    }
}

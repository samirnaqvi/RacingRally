package com.game.view;

import com.game.Main;
import com.game.controller.DataController;
import com.game.model.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Collections;

public class HighscoreMenu { // This is the menu for the high score screen

    private static Label highscore;
    private static StringBuilder stringBuilder;

    public static Scene getScene() {
        try {
            StackPane stackPane = new StackPane();
            stackPane.setMinSize(400, 550);
            //Sort users by scores.
            Collections.sort(DataController.loadedUsers);
            stringBuilder = new StringBuilder();
            highscore = new Label();
            //get actual size of loaded users
            int size = DataController.loadedUsers.size();
            if (size > 10) size = 10;
            System.out.println(size);
            int countOfRealUsers = 1;
            int lastScores = 0;
            for (int i = 1; i <= size; i++) {
                //get user from controller by index and load into StringBuilder
                User fetch = DataController.loadedUsers.get(i - 1);
                countOfRealUsers++;
                stringBuilder.append(i + ". [" + fetch.getHighestScore() + "] " + fetch.getName() + System.getProperty("line.separator"));
                lastScores = fetch.getHighestScore();
            }
            if (lastScores < 100) lastScores = 100;
            User[] fakeUsers = {new User("Jack1990", lastScores - 10),
                    new User("SamKavinsky", lastScores - 20), /// These are the fake users in the leaderboard who inspire the user to do better and follow him in the leaderboard.
                    new User("Driver", lastScores - 30),
                    new User("Razor", lastScores - 40),
                    new User("Levi9", lastScores - 50),
                    new User("condoR", lastScores - 60),
                    new User("YYF", lastScores - 70),
                    new User("Letov55", lastScores - 80),
                    new User("Pelevin05", lastScores - 90),
                    new User("Morro", lastScores - 100)
            };
            for (int i = countOfRealUsers-1; i < 10; i++) {
                stringBuilder.append(countOfRealUsers + ". [" + fakeUsers[i].getHighestScore() + "] " + fakeUsers[i].getName() + System.getProperty("line.separator"));
                countOfRealUsers++;
            }
            String result = stringBuilder.toString();
            highscore.setText(result);
            //initializing view
            final ImageView laurel = new ImageView(
                    new javafx.scene.image.Image(
                            Main.class.getResource("/laurel.png").toURI().toString()));
            laurel.setFitHeight(85);
            laurel.setFitWidth(100);
            Button menu = new Button("Menu");
            menu.setStyle(" -fx-padding: 5 22 5 22;\n" +
                    "    -fx-border-color: #e2e2e2;\n" +
                    "    -fx-border-width: 1;\n" +
                    "    -fx-background-radius: 0;\n" +
                    "    -fx-background-color: #1d1d1d;\n" +
                    "    -fx-font-family: \"Segoe UI\", Helvetica, Arial, sans-serif;\n" +
                    "    -fx-font-size: 11pt;\n" +
                    "    -fx-text-fill: #d8d8d8;\n" +
                    "    -fx-background-insets: 0 0 0 0, 0, 1, 2;");
            menu.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    highscore.setText("");
                    TitleView.changeScene(MainMenuView.getScene());
                }
            });
            menu.setPrefSize(150, 50);
            //add interface elements
            stackPane.getChildren().add(highscore);
            stackPane.getChildren().add(laurel);
            stackPane.getChildren().add(menu);
            stackPane.setAlignment(highscore, Pos.TOP_CENTER);
            stackPane.setAlignment(laurel, Pos.TOP_CENTER);
            stackPane.setAlignment(menu, Pos.BOTTOM_CENTER);
            stackPane.setMargin(laurel, new Insets(5, 0, 0, 0));
            stackPane.setMargin(highscore, new Insets(100, 0, 0, 0));
            stackPane.setMargin(menu, new Insets(0, 0, 30, 0));
            stackPane.setFocusTraversable(true);
            highscore.setStyle("-fx-font-size: 16pt;" +
                    "    -fx-font-family: \"Segoe UI Semibold\";" +
                    "    -fx-text-fill: whitesmoke;" +
                    "    -fx-opacity: 1;");
            stackPane.setStyle("-fx-background-image: url('background_highscore.jpg');");
            //logging
            DataController.usLogger.log("opened high-score menu.");
            return new Scene(stackPane);
        } catch (Exception e) {
            DataController.exLogger.log(e.getMessage());
            e.printStackTrace();
            return MainMenuView.getScene();
        }
    }
}

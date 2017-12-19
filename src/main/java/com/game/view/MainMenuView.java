package com.game.view;

import com.game.Main;
import com.game.controller.DataController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.media.AudioClip;

import java.net.URISyntaxException;
import java.util.Optional;


public class MainMenuView {
    private static boolean music = false;
    public static Scene getScene() {
        try {
            GridPane stackPane = new GridPane();
            //creating columns for buttons
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPrefWidth(40);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPrefWidth(100);
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setPrefWidth(120);
            //adding columns to pane
            stackPane.getColumnConstraints().addAll(col1, col2, col3);
            Scene scene = new Scene(stackPane);

            final AudioClip background_music = new AudioClip(Main.class.getResource("/menu.wav").toURI().toString());//audio
            final AudioClip soundClick = new AudioClip(Main.class.getResource("/click.mp3").toURI().toString());
            //activating css theme
            if (!music) {
                background_music.play();
                music=true;
            }

            scene.getStylesheets().add("darktheme.css");
            //loading buttons from ButtonFactory by names.
            Button[] buttons = ButtonFactory.getArrayOfButtons(
                    "Single player",
                    "Multi player",
                    "High score",
                    "Inform");
        /*
        Multi player button - redirects to initial view, but in game currently
        invisible. For make this button visible - change 'false' to 'true' below.
         */
            buttons[1].setVisible(true);
            buttons[1].setText("User menu");
            //Single player button - change View to CarMenu
            buttons[0].setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    soundClick.play();
                    background_music.stop();
                    TitleView.changeScene(new CarMenuView().getScene());
                    music=false;
                }
            });
            //Multi player button - change View to InitialView
            buttons[1].setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    soundClick.play();
                    TitleView.changeScene(new InitialView().getScene());
                }
            });
            // Highscore button - change View to HighscoreView
            buttons[2].setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    soundClick.play();
                    TitleView.changeScene(HighscoreMenu.getScene());
                }
            });
            //Report dialog creating
            buttons[3].setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) { // This method displays the box in which the user can send complaints or seek help for issues.
                    soundClick.play();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Inform developers");
                    alert.setHeaderText("Describe the issues encountered while playing.");
                    alert.setContentText("Press <Show details> to add message or press OK for send current logs.");
                    GridPane pane = new GridPane();
                    TextArea textArea = new TextArea();
                    pane.getChildren().add(textArea);
                    alert.getDialogPane().setExpandableContent(pane);
                    Optional<ButtonType> buttons = alert.showAndWait();

//                if (buttons.get()==ButtonType.OK){
//                    soundClick.play();
//                    DataController.sendLogs(textArea.getText());
//                }
                }
            });
            for (Button button : buttons) button.setPrefSize(120, 40);
            RowConstraints con = new RowConstraints();
            con.setPrefHeight(150);
            stackPane.getRowConstraints().addAll(con);
            //add buttons
            stackPane.add(buttons[0], 2, 6);
            stackPane.add(buttons[1], 2, 7);
            stackPane.add(buttons[2], 2, 8);
            stackPane.add(buttons[3], 2, 9);
            //setting background image
            stackPane.setStyle("-fx-background-image: url('background.jpg');");
            return scene;
        } catch (URISyntaxException e) {
            DataController.exLogger.log(e.getMessage());
            e.printStackTrace();
            return null;
        }

    }


}

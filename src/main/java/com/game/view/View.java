package com.game.view;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class View {
    //Sample View scene.
    public static StackPane initialize(Stage stage) {
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(stackPane);
        scene.getStylesheets().add("darktheme.css");
        stage.setScene(scene);
        stackPane.setMinSize(400, 550);
        stage.setTitle("Rally Racing v0.983a"); // Small Title at the Top
        return stackPane;
    }
}

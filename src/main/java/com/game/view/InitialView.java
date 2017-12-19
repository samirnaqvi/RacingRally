package com.game.view;

import com.game.Main;
import com.game.controller.DataController;
import com.game.controller.DatabaseController;
import com.game.model.User;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

import javax.xml.crypto.Data;
import java.net.URISyntaxException;
import java.util.Optional;

public class InitialView {

    //Combo box with loaded users
    private static ComboBox<User> users = new ComboBox<User>();
    //Boolean variable flag for controlling loading.
    private static boolean usersLoaded = false;

    public Scene getScene() {
        try {
            final AudioClip soundClick = new AudioClip(Main.class.getResource("/click.mp3").toURI().toString());
//audio
            StackPane stackPane = new StackPane();
            Button createNewUser = new Button("Create a new user");
            Button select = new Button("Select");
            select.setPrefSize(120, 40);
            createNewUser.setPrefSize(120, 40);
            stackPane.getStylesheets().add("darktheme.css");
            stackPane.getChildren().add(users);
            stackPane.getChildren().add(createNewUser);
            stackPane.getChildren().add(select);
            users.setMinWidth(180);
            //Method for loading users from Data Controller (from user directory)
            if (!usersLoaded) {
                usersLoaded = true;
                User[] loadedUsers = DataController.findUsers();
                if (null != loadedUsers && loadedUsers.length != 0) {
                    for (int i = 0; i < loadedUsers.length; i++) {
                        users.getItems().add(loadedUsers[i]);
                    }
                    users.setValue(loadedUsers[0]);
                    users.getItems().set(0, loadedUsers[0]);
                } //Method for loading users from Data Controller (from database)
                if (null != DataController.databaseConnection)
                    for (int i = 0; i < DataController.usersFromDatabase.size(); i++) {
                        users.getItems().add(DataController.usersFromDatabase.get(i));
                    }
            }
            //Initializing dialog of creating new-user.
            createNewUser.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    soundClick.play();
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.setTitle("Creating a new user");
                    dialog.setHeaderText("User creating");
                    dialog.setContentText("Please enter your name: ");
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        soundClick.play();
                        //Adding new user to current user in DataController
                        if (null != DataController.databaseConnection)
                            new DatabaseController().createNewUser(result.get());
                        User newUser = new User(result.get(),999);
                        users.getItems().add(newUser);
                        users.setValue(newUser);
                        //Add new user to loaded users list.
                        DataController.loadedUsers.add(newUser);
                        DataController.user = newUser;
                        DataController.writeStats();
                    }
                }
            });
            users.valueProperty().addListener(new ChangeListener<User>() {
                @Override
                public void changed(ObservableValue<? extends User> observableValue, User user, User t1) {
                    DataController.user = users.getValue();
                }
            });

            //Selecting using from combo box.
            select.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    soundClick.play();
                    DataController.user = users.getValue();
                    DataController.usLogger.log(DataController.user + " user selected");
                    //After selecting user - changing View of window to MainMenuView.
                    TitleView.changeScene(MainMenuView.getScene());
                }
            });
            //Creating label and animation.
            final Label tip = new Label("Create or select a user");
            tip.setId("label-header");
            stackPane.getChildren().add(tip);
            stackPane.setStyle("-fx-background-image: url('background_initial.jpg');");
            stackPane.setMargin(tip, new Insets(0, 0, 200, 0));
            stackPane.setMargin(users, new Insets(0, 0, 40, 0));
            stackPane.setMargin(createNewUser, new Insets(80, 0, 0, 0));
            stackPane.setMargin(select, new Insets(160, 0, 0, 0));
            FadeTransition ft = new FadeTransition(Duration.millis(8000), tip);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setAutoReverse(true);
            ft.setCycleCount(Animation.INDEFINITE);
            ft.play();
            return new Scene(stackPane);
        } catch (URISyntaxException e) {
            DataController.exLogger.log(e.getMessage());
            e.printStackTrace();
            return null;
        }

    }
}

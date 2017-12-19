package com.game;

import com.game.controller.DataController;
import com.game.controller.DatabaseController;
import com.game.view.TitleView;

public class Main {
    public static void main(String[] args) {
        //Logging creating of game instance.
        DataController.usLogger.log("Created game instance.");
        // Starting separated thread of View part
        Thread thread = new Thread(new Runnable() {
            public void run() {
                TitleView title = new TitleView();
                title.launch();
            }
        });
        thread.start();
        // Trying to communicate with MySQL database.
        DatabaseController databaseController = new DatabaseController();
        databaseController.getConnection();
        databaseController.getUsers();
    }
}

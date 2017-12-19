package com.game.view;

import javafx.scene.control.Button;

public class ButtonFactory {  // This is a constructor for creating buttons in our game
    public static Button getButton(String param) {
        return new Button(param);
    }

    public static Button[] getArrayOfButtons(String... params) {// this creates an array of the buttons which are eventually displayed on the screen
        Button[] output = new Button[params.length];
        int counter = 0;
        for (String param : params) {
            output[counter] = new Button(param);
            counter++;
        }
        return output;
    }
}

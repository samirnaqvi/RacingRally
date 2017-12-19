package com.game.controller;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameLogger {

    private File file;
    private FileWriter fileWriter;
    private DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy [hh:mm:ss] : ");
    /*Allows for logging in game to occur through the creation of a File for specific logging, as well as try catch implementation*/
    public GameLogger(String PATH) {
        try {
            file = new File(PATH);
            file.createNewFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void log(String message) {
        try {
            fileWriter = new FileWriter(file,true);
            fileWriter.write(dateFormat.format(new Date()));
            fileWriter.write(message+"\n");
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

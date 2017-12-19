package com.game.controller;

import com.game.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {
    Connection connection;
    /*The method below is implemented to check for the connection to the Database through a try catch system, where if the connection is not found, a message will be logged that the Database could not connect.*/
    public void getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/rallyracing?user=root&password=cricket");
            DataController.databaseConnection = connection;
            System.out.println("Successfully connected to database.");//Prints a successfull connection
        } catch (Exception e) {
            if (e.getMessage().contains("failure")) DataController.exLogger.log("Cannot connect to database.");//In course that the connection is not found
        }
    }
/*This method retrives the user, and other specific information about them, including the id, name, highestscore, etc*/
    public void getUsers() {
        List<User> user = new ArrayList<>();
        try {
            Statement queryStatement = connection.createStatement();
            String query = "select * from user";
            ResultSet resultSet = queryStatement.executeQuery(query);
            if (!resultSet.isBeforeFirst()) {
                System.out.println("No data");
            }
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int highestScore = resultSet.getInt("highestscore");
                user.add(new User(id, name, highestScore));
            }
            DataController.usersFromDatabase = user;
        } catch (Exception e) {
            DataController.exLogger.log(e.getMessage());
        }


    }
/*This method is utilized to create a new user to be logged in the log files upong adding the new user to the database*/
    public void createNewUser(String nick) {
        try {
            Statement statement = DataController.databaseConnection.createStatement();
            statement.execute("insert into user values(null,'" + nick + "',0)");
            System.out.println("Added new user to database.");
            System.out.println("Refreshing combobox.");

        } catch (Exception e) {
            DataController.exLogger.log(e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage() + " in creating of new user");

        }

    }
}

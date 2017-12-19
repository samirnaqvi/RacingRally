package com.game.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.function.Function;


public class MapController {
    private int length;
    private int playerPos;
    private int enemyPos;
    private int timer = length / 330;
    private Function<Double, Double> function;

    public MapController(Function<Double, Double> f, int mapLength) {
        length = mapLength;
        function = f;
    }
/*Allows for the map to appear to be a moving road by using a coordinate system to create a scrolling effect of the backrgound to cause the illusion of the car going forward.*/
    public Scene getScene() {
        double xStep = 0.1;
        double x = -0.1;
        double max = 2 * Math.PI;
        double y = function.apply(x);
        final Pane plot = new Pane();
        final Path graph = new Path();
        final StackPane stackPane = new StackPane(plot);
        graph.setStroke(Color.ORANGE);
        graph.setStrokeWidth(5);
        graph.getElements().add(new MoveTo(x * 48 + 25, -y * 48 + 25));
        while (x < max) {
            x += xStep;
            y = function.apply(x);
            graph.getElements().add(new LineTo(x * 48 + 25, -y * 48 + 25));
        }
        plot.getChildren().add(graph);
        stackPane.setPadding(new Insets(50, 50, 50, 50));
//        try {
//            Thread.sleep(4000);
            Platform.runLater(move(plot));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return new Scene(stackPane);
    }

    private Thread move(final Pane plot) {
       Thread pointMoving = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    double xStep = 0.01;
                    double x = -0.1;
                    double max = 2 * Math.PI;
                    double y = function.apply(x);
                    Path graph = new Path();
                    graph.setStroke(Color.BLACK);
                    graph.setStrokeWidth(2);
                    Line fatPoint = new Line(x * 48 + 25, -y * 48 + 25, x * 48 + 25, -y * 48 + 25);
                    /*Coordinate system to move the map according to create the sense of moving forward in a car*/
                    add(plot, fatPoint, true);
                    fatPoint.setStroke(Color.BLACK);
                    fatPoint.setStrokeWidth(3);
                    while (x < max) {
                        System.out.println("qq");
                        fatPoint.relocate(x * 48 + 25, -y * 48 + 25);
                        Thread.sleep(timer);
                        y = function.apply(x);
                        x += xStep;
                    }
                } catch (InterruptedException e) {
                    DataController.exLogger.log(e.getMessage());
                }

            }
        });
        return pointMoving;
    }


    private void add(final Pane pane, final Node node, final boolean add) {
        Platform.runLater(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (add) pane.getChildren().add(node);
                else pane.getChildren().remove(node);
                return null;
            }
        });
    }
}

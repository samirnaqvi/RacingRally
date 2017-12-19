package com.game.model.objects;

import javafx.scene.image.Image;

public class Map {
    private int length;
    private String name;
    private Image mapImage;

    @Override
    public String toString() { //attributes of the map, seen in the Map Selection Screen and Gameplay
        return "Map{" +
                "length=" + length +
                ", name='" + name + '\'' +
                ", mapImage=" + mapImage +
                '}';
    }

    public Map(int length, String name, Image mapImage) {
        this.length = length;
        this.name = name;
        this.mapImage = mapImage;
    }

    public int getLength() {
        return length;//Distance of track in kilometers
    }

    public String getName() {
        return name;//Name of the track
    }

    public Image getMapImage() {
        return mapImage;//Cover image of the track
    }
}

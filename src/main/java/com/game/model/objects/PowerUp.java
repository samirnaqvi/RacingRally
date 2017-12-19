package com.game.model.objects;

import com.game.model.enums.PowerupType;
import javafx.scene.image.Image;

public class PowerUp extends GameObject { //extends the GamObject abstract class

    private PowerupType type;
    private Image image;
    private boolean onMap;

    public PowerUp(PowerupType type, Image image) {
        this.type = type;
        this.image = image;
        onMap = false;
    }

    public boolean isOnMap() {
        return onMap;
    }

    public void setOnMap(boolean onMap) {
        this.onMap = onMap;
    }

    public PowerupType getType() {
        return type;//gets whcih one of the three types of powerups it is
    }

    public Image getImage() {
        return image;//gets the image of the powerup that is seen by the users
    }

}

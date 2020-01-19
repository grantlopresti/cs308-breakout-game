package breakout;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class Brick extends MovingObject {
    public static final double POWER_UP_PROBABILITY = 1.0;
    public int myHits;
    public PowerUp myPowerUp;
    public boolean moving = false;
    public boolean hasPowerUp = false;

    public Brick(Image image, int hits) {
        super(image);
        myHits = hits;

        //gives the block a PowerUp with a 10% chance
        determinePowerUp();

        //decodes hits==4 as a moving block with 2 hits to break
        if (hits == 4) {
            handleMovingBlock();
        }
    }

    public void addPowerUp(PowerUp powerUp){
        myPowerUp = powerUp;
    }

    private void determinePowerUp() {
        if (Math.random() < POWER_UP_PROBABILITY){
            hasPowerUp = true;
        }
    }

    private void handleMovingBlock() {
        moving = true;
        setXDir(1);
        myHits = 2;
    }
}

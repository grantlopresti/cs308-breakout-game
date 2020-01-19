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
    public int myHits;
    public boolean moving = false;

    public Brick(Image image, int hits) {
        super(image);
        myHits = hits;
        if (hits == 4) {
            moving = true;
            setXDir(1);
            myHits = 2;
        }
    }
}

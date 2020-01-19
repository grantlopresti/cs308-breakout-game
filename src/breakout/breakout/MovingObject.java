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

public class MovingObject extends ImageView {
    public int xDir = 0;
    public int yDir = 0;

    public MovingObject(Image image) {
        super(image);
    }

    public void setXDir(int newXDir) {
        xDir = newXDir;
    }

    public void setYDir(int newYDir) {
        yDir = newYDir;
    }

    public void setDirection(int newXDir, int newYDir) {
        xDir = newXDir;
        yDir = newYDir;
    }
}
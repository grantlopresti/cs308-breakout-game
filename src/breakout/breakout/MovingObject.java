package breakout;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MovingObject extends ImageView {
    public int xVelocity = 0;
    public int yVelocity = 0;

    public MovingObject(Image image) {
        super(image);
    }

    public void setXDir(int newXVel) {
        xVelocity = newXVel;
    }

    public void setYDir(int newYVel) {
        yVelocity = newYVel;
    }

    public void setDirection(int newXVel, int newYVel) {
        xVelocity = newXVel;
        yVelocity = newYVel;
    }
}
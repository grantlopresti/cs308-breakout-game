package breakout;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MovingObject extends ImageView {
    public double xVelocity = 0;
    public double yVelocity = 0;

    public MovingObject(Image image) {
        super(image);
    }

    public void setXVel(double newXVel) {
        xVelocity = newXVel;
    }

    public void setYVel(double newYVel) {
        yVelocity = newYVel;
    }

    public void setDirection(double newXVel, double newYVel) {
        xVelocity = newXVel;
        yVelocity = newYVel;
    }
}
package breakout;

import javafx.scene.image.Image;

public class PowerUp extends MovingObject {
    public boolean moving = false;
    public int myType;
    public double yVel = 0.4;

    public PowerUp(Image image, int powerUpType) {
        super(image);
        myType = powerUpType;
    }

    public void setMoving(boolean state) {
        moving = state;
    }
}
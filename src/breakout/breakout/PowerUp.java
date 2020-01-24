package breakout;

import javafx.scene.image.Image;

import static breakout.Main.reactToPowerUpCollision;

public class PowerUp extends MovingObject {
    public boolean moving = false;
    public String myType;
    public double yVel = 0.5;

    public PowerUp(Image image, String powerUpType) {
        super(image);
        myType = powerUpType;
    }

    public void setMoving(boolean state) {
        moving = state;
    }

    public void reactToPaddleCollision() {
        reactToPowerUpCollision(myType);
        setX(1000);
        setY(1000);
        moving = false;
    }
}

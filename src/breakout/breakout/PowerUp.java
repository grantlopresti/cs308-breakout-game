package breakout;

import javafx.scene.image.Image;

import static breakout.Main.reactToPowerUpCollision;

public class PowerUp extends MovingObject {
    public boolean moving = false;
    public String myType;
    public Brick myBrick;
    public double yVel = 0.5;

    public PowerUp(Image image, String powerUpType, Brick brick) {
        super(image);
        myType = powerUpType;
        myBrick = brick;
        //positions power up in center of its brick (or off the screen if brick doesn't have powerup)
        centerInBrick();
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

    public void centerInBrick() {
        double centerX = myBrick.getX() + myBrick.getBoundsInParent().getWidth()/2;
        double centerY = myBrick.getY() + myBrick.getBoundsInParent().getHeight()/2;
        if (myBrick.hasPowerUp){
            setX(centerX - getBoundsInParent().getWidth()/2);
            setY(centerY - getBoundsInParent().getHeight()/2);
        } else {
            setX(1000);
            setY(1000);
        }
    }
}

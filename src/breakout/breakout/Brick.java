package breakout;

import javafx.scene.image.Image;

import static breakout.Main.MOVING_OBJECT_SPEED;

public class Brick extends MovingObject {
    public static final double POWER_UP_PROBABILITY = 0.1;
    public int myHits;
    public PowerUp myPowerUp;
    public boolean moving = false;
    public boolean hasPowerUp = false;
    public int pointVal;

    public Brick(Image image, int hits) {
        super(image);
        myHits = hits;

        //gives each brick its specified number of points
        assignBrickPoints(hits);

        //gives the block a PowerUp with a 10% chance
        determinePowerUp();

        //decodes hits==4 as a moving block with 2 hits to break
        if (hits == 4) {
            handleMovingBlock();
        }
    }

    private void assignBrickPoints(int hits) {
        if (hits == 1){
            pointVal = 100;
        } else if (hits == 2){
            pointVal = 250;
        } else if (hits == 3){
            pointVal = 500;
        } else if (hits == 4){
            pointVal = 1000;
        }
    }

    public void addPowerUp(PowerUp powerUp){
        myPowerUp = powerUp;
    }

    private void determinePowerUp() {
        if (myHits > 0 && myHits != 4 && Math.random() < POWER_UP_PROBABILITY){
            hasPowerUp = true;
        }
    }

    private void handleMovingBlock() {
        moving = true;
        setXVel(1);
        myHits = 2;
    }

    public void removeIfBroken() {
        if (myHits <= 0){
            //moves brick off screen
            setX(1000);
            setY(1000);
        }
    }

    public void updatePowerUp() {
        if (hasPowerUp && myHits == 0) {
            setPowerUpFalling();
        }
    }

    private void setPowerUpFalling() {
        myPowerUp.setMoving(true);
        myPowerUp.checkPaddleCollision();
        myPowerUp.setY(myPowerUp.getY() + MOVING_OBJECT_SPEED * myPowerUp.yVel);
    }
}

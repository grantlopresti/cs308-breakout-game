package breakout;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static breakout.Main.*;

public class MovingObject extends ImageView {
    public double xVelocity = 0;
    public double yVelocity = 0;

    /**
     * All MovingObject objects are simply extensions of ImageView (meaning they are objects visible on the screen)
     * They have the bonus instance variables of x and y velocity which are used to update the object's position
     *
     *
     * @param image is simply the image based off of the .gif file for the MovingObject
     */
    public MovingObject(Image image) {
        super(image);
    }

    public void setXVel(double newXVel) {
        xVelocity = newXVel;
    }

    public void setYVel(double newYVel) {
        yVelocity = newYVel;
    }

    public void checkWallCollision() {
        double minX = getBoundsInParent().getMinX();
        double maxX = getBoundsInParent().getMaxX();
        double minY = getBoundsInParent().getMinY();
        double maxY = getBoundsInParent().getMaxY();
        if (maxX >= SIZE || minX <= 0){
            bounceOffSideWalls();
        }
        if (this instanceof Bouncer) {
            if (minY <= GameSetup.STATUS_BAR_HEIGHT ){
                bounceOffHorzSurface();
            }
            if (maxY >= WINDOW_HEIGHT){
                bounceOffBottom();
                loseLife();
            }
        }
        incrementObjectsPosition();
    }

    public void checkPaddleCollision () {
        double objMinX = getBoundsInParent().getMinX();
        double objMaxX = getBoundsInParent().getMaxX();
        double objMaxY = getBoundsInParent().getMaxY();
        double paddleMinX = myPaddle.getBoundsInParent().getMinX();
        double paddleMaxX = myPaddle.getBoundsInParent().getMaxX();
        double paddleMinY = myPaddle.getBoundsInParent().getMinY();

        double paddleWidth = paddleMaxX - paddleMinX;
        boolean onPaddleLevel = objMaxY >= paddleMinY;
        boolean objIntersectsPaddle = onPaddleLevel && objMaxX >= paddleMinX && objMinX <= paddleMaxX;

        if (objIntersectsPaddle){
            if (this instanceof Bouncer) {
                ((Bouncer) this).reactToPaddleCollision(objMinX, objMaxX, paddleMinX, paddleMaxX, paddleWidth);
            } else if (this instanceof PowerUp){
                ((PowerUp) this).reactToPaddleCollision();
            }
        }
    }

    private void incrementObjectsPosition() {
        setX(getX() + MOVING_OBJECT_SPEED * xVelocity);
        setY(getY() + MOVING_OBJECT_SPEED * yVelocity);
    }

    public void bounceOffBottom() {
        xVelocity = 0;
        yVelocity = 0;
    }

    public void bounceOffHorzSurface() {
        yVelocity *= -1;
    }

    public void bounceOffSideWalls() {
        xVelocity *= -1;
    }
}
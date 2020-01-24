package breakout;

import javafx.scene.image.Image;

import static breakout.Main.MOVING_OBJECT_SPEED;

public class Bouncer extends MovingObject {
    public Bouncer(Image image) {
        super(image);
    }

    public void shootBall() {
        setYVel(-1);
        //50-50 chance of right or left release
        if (Math.random() < 0.5){
            setXVel(1);
        } else {
            setXVel(-1);
        }
    }

    public void reactToPaddleCollision(double objMinX, double objMaxX, double paddleMinX, double paddleMaxX,
                                    double paddleWidth) {
        boolean intersectsLeft = objMaxX <= paddleMinX + (paddleWidth / 3);
        boolean intersectsRight = objMinX >= paddleMaxX - (paddleWidth / 3);
        if (intersectsLeft) {
            xVelocity = -1;
        } else if (intersectsRight) {
            xVelocity = 1;
        }
        bounceOffHorzSurface();
        //give the ball a little boost off of the paddle
        setY(getY() + 2 * MOVING_OBJECT_SPEED * yVelocity);
    }
}
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


/**
 * A basic example JavaFX program for the first lab.
 *
 * @author Robert C. Duvall
 */
public class Main extends Application {
    public static final String TITLE = "BREAKOUT (gjl13)";
    public static final int SIZE = 400;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = Color.MEDIUMPURPLE;
    public static final Paint HIGHLIGHT = Color.OLIVEDRAB;
    public static final String BOUNCER_IMAGE = "ball.gif";
    public static final String PADDLE_IMAGE = "paddle.gif";
    public static final int PADDLE_SPEED = 5;
    public static final int BOUNCER_SPEED = 2;
    public static final Paint MOVER_COLOR = Color.PLUM;
    public static final int MOVER_SIZE = 50;
    public static final Paint GROWER_COLOR = Color.BISQUE;
    public static final double GROWER_RATE = 1.1;
    public static final int GROWER_SIZE = 50;

    // some things needed to remember during game
    private Scene myScene;
    private ImageView myBouncer;
    private ImageView myPaddle;
    public static double bouncerXDir = 1;
    public static double bouncerYDir = -1;
    private Rectangle myMover;
    private Rectangle myGrower;


    /**
     * Initialize what will be displayed and how it will be updated.
     */
    @Override
    public void start (Stage stage) {
        // attach scene to the stage and display it
        myScene = setupGame(SIZE, SIZE, BACKGROUND);
        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();
        // attach "game loop" to timeline to play it (basically just calling step() method repeatedly forever)
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    // Create the game's "scene": what shapes will be in the game and their starting properties
    private Scene setupGame (int width, int height, Paint background) {
        // create one top level collection to organize the things in the scene
        Group root = new Group();
        // make some shapes and set their properties
        Image bouncerImage = new Image(this.getClass().getClassLoader().getResourceAsStream(BOUNCER_IMAGE));
        myBouncer = new ImageView(bouncerImage);
        Image paddleImage = new Image(this.getClass().getClassLoader().getResourceAsStream(PADDLE_IMAGE));
        myPaddle = new ImageView(paddleImage);
        // x and y represent the top left corner, so center it in window
        myBouncer.setX(width / 2 - myBouncer.getBoundsInLocal().getWidth() / 2);
        myBouncer.setY(height - myPaddle.getBoundsInLocal().getHeight() - myBouncer.getBoundsInLocal().getHeight() /2);
        myPaddle.setX(width / 2 - myPaddle.getBoundsInLocal().getWidth() / 2);
        myPaddle.setY(height - myPaddle.getBoundsInLocal().getHeight());
        myMover = new Rectangle(width / 2 - MOVER_SIZE / 2, height / 2 - 100, MOVER_SIZE, MOVER_SIZE);
        myMover.setFill(MOVER_COLOR);
        myGrower = new Rectangle(width / 2 - GROWER_SIZE / 2, height / 2 + 50, GROWER_SIZE, GROWER_SIZE);
        myGrower.setFill(GROWER_COLOR);
        // order added to the group is the order in which they are drawn
        root.getChildren().add(myBouncer);
        root.getChildren().add(myMover);
        root.getChildren().add(myGrower);
        root.getChildren().add(myPaddle);
        // create a place to see the shapes
        Scene scene = new Scene(root, width, height, background);
        // respond to input
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        return scene;
    }

    // Change properties of shapes in small ways to animate them over time
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start
    private void step (double elapsedTime) {
        // update "actors" attributes
        checkWallCollision();
        myMover.setRotate(myMover.getRotate() - 1);
        myGrower.setRotate(myGrower.getRotate() + 1);

        // check for collisions
        // with shapes, can check precisely
        // NEW Java 10 syntax that simplifies things (but watch out it can make code harder to understand)
        // var intersection = Shape.intersect(myMover, myGrower);
        Shape intersection = Shape.intersect(myMover, myGrower);
        if (intersection.getBoundsInLocal().getWidth() != -1) {
            myMover.setFill(HIGHLIGHT);
        }
        else {
            myMover.setFill(MOVER_COLOR);
        }
        // with images can only check bounding
        checkPaddleCollision();
    }

    private void checkWallCollision(){
        double minX = myBouncer.getBoundsInParent().getMinX();
        double maxX = myBouncer.getBoundsInParent().getMaxX();
        double minY = myBouncer.getBoundsInParent().getMinY();
        double maxY = myBouncer.getBoundsInParent().getMaxY();

        if (maxX >= SIZE || minX <= 0){
            bouncerXDir = -1*bouncerXDir;
        }
        if (minY <= 0 ){
            bouncerYDir = -1*bouncerYDir;
        }
        if (maxY >= SIZE){
            bouncerXDir = 0;
            bouncerYDir = 0;
        }

        myBouncer.setX(myBouncer.getX() + BOUNCER_SPEED*bouncerXDir);
        myBouncer.setY(myBouncer.getY() + BOUNCER_SPEED*bouncerYDir);
    }

    private void checkPaddleCollision () {
        if (myBouncer.getBoundsInParent().intersects(myPaddle.getBoundsInParent())){
            bouncerYDir = -1*bouncerYDir;
            myBouncer.setY(myBouncer.getY() + BOUNCER_SPEED*bouncerYDir);
        }
    }

    // What to do each time a key is pressed
    private void handleKeyInput (KeyCode code) {
        if (code == KeyCode.RIGHT) {
            myPaddle.setX(myPaddle.getX() + PADDLE_SPEED);
        }
        else if (code == KeyCode.LEFT) {
            myPaddle.setX(myPaddle.getX() - PADDLE_SPEED);
        }
        // NEW Java 12 syntax that some prefer (but watch out for the many special cases!)
        //   https://blog.jetbrains.com/idea/2019/02/java-12-and-intellij-idea/
        // Note, must set Project Language Level to "13 Preview" under File -> Project Structure
        // switch (code) {
        //     case RIGHT -> myMover.setX(myMover.getX() + MOVER_SPEED);
        //     case LEFT -> myMover.setX(myMover.getX() - MOVER_SPEED);
        //     case UP -> myMover.setY(myMover.getY() - MOVER_SPEED);
        //     case DOWN -> myMover.setY(myMover.getY() + MOVER_SPEED);
        // }
    }

    // What to do each time a key is pressed
    private void handleMouseInput (double x, double y) {
        if (myGrower.contains(x, y)) {
            myGrower.setScaleX(myGrower.getScaleX() * GROWER_RATE);
            myGrower.setScaleY(myGrower.getScaleY() * GROWER_RATE);
        }
    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}

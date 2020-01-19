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
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;
import javafx.scene.text.*;
import java.util.ArrayList;


/**
 * A basic example JavaFX program for the first lab.
 *
 * @author Robert C. Duvall
 */
public class Main extends Application {
    public static final String TITLE = "BREAKOUT (gjl13)";
    public static final int SIZE = 455;
    public static final int STATUS_BAR_HEIGHT = 50;
    public static final int WINDOW_HEIGHT = SIZE + STATUS_BAR_HEIGHT;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint SPLASH_SCREEN_BACKGROUND = Color.LIGHTGOLDENRODYELLOW;
    public static final Paint LEVEL_ONE_BACKGROUND = Color.MEDIUMPURPLE;
    public static final Paint LEVEL_TWO_BACKGROUND = Color.MEDIUMPURPLE;
    public static final Paint LEVEL_THREE_BACKGROUND = Color.MEDIUMPURPLE;
    public static final String BOUNCER_IMAGE = "ball.gif";
    public static final String PADDLE_IMAGE = "paddle.gif";
    public static final String BRICK_IMAGE_0 = "brick3.gif";
    public static final String BRICK_IMAGE_1 = "brick8.gif";
    public static final String BRICK_IMAGE_2 = "brick7.gif";
    public static final String BRICK_IMAGE_3 = "brick9.gif";
    public static final String BRICK_IMAGE_4 = "brick6.gif";
    public static final String POWER_UP_IMAGE_0 = "sizepower.gif";
    public static final String POWER_UP_IMAGE_1 = "laserpower.gif";
    public static final String POWER_UP_IMAGE_2 = "pointspower.gif";
    public static final String LEVEL_ONE_BRICKS = "resources/levelOneBricks.txt";
    public static final String LEVEL_TWO_BRICKS = "resources/levelTwoBricks.txt";
    public static final String LEVEL_THREE_BRICKS = "resources/levelThreeBricks.txt";
    public static final int PADDLE_SPEED = 10;
    public static final int MOVING_OBJECT_SPEED = 2;
    public static final int BRICK_SPACING = 5;
    public static final int MAX_BRICKS_TALL = 8;
    public static final int MAX_BRICKS_WIDE = 6;

    // some things needed to remember during game
    private Scene splashScreen;
    private Scene levelOne;
    private ImageView myBall;
    private ImageView myPaddle;
    private Rectangle myStatusBar = new Rectangle(0, 0, SIZE, STATUS_BAR_HEIGHT);
    private ArrayList<Brick> myBricks = new ArrayList<Brick>();
    private static int playerLives = 3;
    private static int playerPoints = 0;
    private static int numBricksInLevel = 0;
    private static int numBricksBroken = 0;
    public static Text playerLivesTxt;
    public static Text playerPointsTxt;


    /**
     * Initialize what will be displayed and how it will be updated.
     */
    @Override
    public void start (Stage stage) throws FileNotFoundException {
        // attach scene to the stage and display it
        splashScreen = setupSplashScreen(SPLASH_SCREEN_BACKGROUND);
        stage.setScene(splashScreen);
        stage.setTitle(TITLE);
        stage.show();
        levelOne = setupGame(LEVEL_ONE_BACKGROUND);
        splashScreen.setOnMouseClicked(e -> stage.setScene(levelOne));
        splashScreen.setOnKeyPressed(e -> stage.setScene(levelOne));
        // attach "game loop" to timeline to play it (basically just calling step() method repeatedly forever)
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    private Scene setupSplashScreen(Paint background) throws FileNotFoundException {
        // create one top level collection to organize the things in the scene
        Group root = new Group();
        //create and format text in status bar
        Text welcomeTxt = new Text(10, SIZE/2, "Welcome to Breakout!");
        Text subTxt = new Text(100,  SIZE/2 + 50,  "Grant LoPresti (gjl13)");
        welcomeTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
        welcomeTxt.setTextAlignment(TextAlignment.CENTER);
        subTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        subTxt.setTextAlignment(TextAlignment.CENTER);
        //add elements to the root
        root.getChildren().add(welcomeTxt);
        root.getChildren().add(subTxt);
        // create a place to see the shapes
        Scene scene = new Scene(root, Main.SIZE, Main.WINDOW_HEIGHT, background);
        // respond to input`
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        return scene;
    }

    // Create the game's "scene": what shapes will be in the game and their starting properties
    private Scene setupGame(Paint background) throws FileNotFoundException {
        // create one top level collection to organize the things in the scene
        Group root = new Group();
        myStatusBar.setFill(Color.YELLOW);
        //create and format text in status bar
        playerLivesTxt = new Text(10, 30, "Lives: " + playerLives);
        playerPointsTxt = new Text(SIZE - 150, 30,  "Points: " + playerPoints);
        playerLivesTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        playerPointsTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        makeBricksFromFile(LEVEL_TWO_BRICKS);
        makePowerUps();
        myBall = new Bouncer(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(BOUNCER_IMAGE))));
        myPaddle = new ImageView(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(PADDLE_IMAGE))));
        // x and y represent the top left corner, so center it in window
        initializePaddleAndBouncerPositions(Main.SIZE, Main.WINDOW_HEIGHT);
        // order added to the group is the order in which they are drawn
        addAllObjectsToGameRoot(root);
        // create a place to see the shapes
        Scene scene = new Scene(root, Main.SIZE, Main.WINDOW_HEIGHT, background);
        // respond to input
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        return scene;
    }

    private void addAllObjectsToGameRoot(Group root) {
        root.getChildren().add(myStatusBar);
        root.getChildren().add(playerLivesTxt);
        root.getChildren().add(playerPointsTxt);
        root.getChildren().add(myBall);
        root.getChildren().add(myPaddle);
        for (int i = 0; i < myBricks.size(); i++){
            //Add powerUps underneath bricks]
            root.getChildren().add(myBricks.get(i).myPowerUp);
            //Add bricks to root
            root.getChildren().add(myBricks.get(i));
        }
    }

    private void makePowerUps(){
        Image powerUpImage0 = new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(POWER_UP_IMAGE_0)));
        Image powerUpImage1 = new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(POWER_UP_IMAGE_1)));
        Image powerUpImage2 = new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(POWER_UP_IMAGE_2)));
        for (Brick myBrick:myBricks) {
            double centerX = myBrick.getX() + myBrick.getBoundsInParent().getWidth()/2;
            double centerY = myBrick.getY() + myBrick.getBoundsInParent().getHeight()/2;
            //creates power up with random type
            PowerUp myPowerUp = getPowerUp(powerUpImage0, powerUpImage1, powerUpImage2);
            //positions power up in center of brick or off the screen
            positionPowerUp(myBrick, centerX, centerY, myPowerUp);
            //add power up to respective Brick object
            myBrick.addPowerUp(myPowerUp);
        }
    }

    private void positionPowerUp(Brick myBrick, double centerX, double centerY, PowerUp myPowerUp) {
        if (myBrick.hasPowerUp){
            myPowerUp.setX(centerX - myPowerUp.getBoundsInParent().getWidth()/2);
            myPowerUp.setY(centerY - myPowerUp.getBoundsInParent().getHeight()/2);
        } else {
            myPowerUp.setX(1000);
            myPowerUp.setY(1000);
        }
    }

    private PowerUp getPowerUp(Image powerUpImage0, Image powerUpImage1, Image powerUpImage2) {
        int powerUpType;
        Image image;
        double tempRandom = Math.random();
        if (tempRandom < 1.0/3){
            powerUpType = 0;
            image = powerUpImage0;
        } else if (tempRandom < 2.0/3){
            powerUpType = 1;
            image = powerUpImage1;
        } else {
            powerUpType = 2;
            image = powerUpImage2;
        }
        return new PowerUp(image, powerUpType);
    }

    private void initializePaddleAndBouncerPositions(int width, int height) {
        myBall.setX(width / 2 - myBall.getBoundsInLocal().getWidth() / 2);
        myBall.setY(height - myPaddle.getBoundsInLocal().getHeight() - myBall.getBoundsInLocal().getHeight() /2);
        myPaddle.setX(width / 2 - myPaddle.getBoundsInLocal().getWidth() / 2);
        myPaddle.setY(height - myPaddle.getBoundsInLocal().getHeight() - 5);
    }

    private void makeBricksFromFile(String levelLayoutFile) throws FileNotFoundException {
        //Read text file
        File file = new File(levelLayoutFile);
        Scanner sc = new Scanner(file);
        int[][] levelBricks = new int[MAX_BRICKS_TALL][MAX_BRICKS_WIDE];
        for (int i = 0; i < MAX_BRICKS_TALL; i++) {
            for (int j = 0; j < MAX_BRICKS_WIDE; j++) {
                levelBricks[i][j] = Integer.parseInt(sc.next());
            }
        }
        // make some shapes and set their properties
        double brickHeight = 20.0;
        for (int i = 0; i < MAX_BRICKS_TALL; i++){
            createBricksRow(BRICK_SPACING*(i+1) + (int)brickHeight*i + STATUS_BAR_HEIGHT, levelBricks[i]);
        }
    }

    private void createBricksRow(int placementHeight, int[] blockTypes){
        Brick myBrick;
        Image brickImage0 = new Image(this.getClass().getClassLoader().getResourceAsStream(BRICK_IMAGE_0));
        Image brickImage1 = new Image(this.getClass().getClassLoader().getResourceAsStream(BRICK_IMAGE_1));
        Image brickImage2 = new Image(this.getClass().getClassLoader().getResourceAsStream(BRICK_IMAGE_2));
        Image brickImage3 = new Image(this.getClass().getClassLoader().getResourceAsStream(BRICK_IMAGE_3));
        Image brickImage4 = new Image(this.getClass().getClassLoader().getResourceAsStream(BRICK_IMAGE_4));
        double brickWidth = 70;
        for (int i = 0; i < MAX_BRICKS_WIDE; i++){
            int blockType = blockTypes[i];
            if (blockType == 1) {
                myBrick = new Brick(brickImage1,1);
            } else if (blockType == 2){
                myBrick = new Brick(brickImage2,2);
            } else if (blockType == 3) {
                myBrick = new Brick(brickImage3,3);
            } else if (blockType == 4){
                myBrick = new Brick(brickImage4,4);
            } else {
                myBrick = new Brick(brickImage0, 0);
            }
            myBrick.setX(BRICK_SPACING*(i+1) + i*brickWidth);
            myBrick.setY(placementHeight);
            if (myBrick.myHits != 0){
                numBricksInLevel += 1;
            }
            myBricks.add(myBrick);
        }

    }
    // Change properties of shapes in small ways to animate them over time
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start
    private void step (double elapsedTime) {
        // update "actors" attributes
        checkWallCollision((MovingObject) myBall);
        checkPaddleCollision((MovingObject) myBall);
        updatePowerUps();
        makeMovingBricksMove();
        checkBrickCollision();
    }

    private void updatePowerUps() {
        for (int i = 0; i < myBricks.size(); i++){
            Brick myBrick = myBricks.get(i);
            PowerUp myPowerUp = myBrick.myPowerUp;
            if (myBrick.hasPowerUp && myBrick.myHits == 0) {
                myBrick.myPowerUp.setMoving(true);
                checkPaddleCollision(myBrick.myPowerUp);
                myPowerUp.setY(myPowerUp.getY() + MOVING_OBJECT_SPEED *myPowerUp.yVel);
            }
        }
    }

    private void makeMovingBricksMove() {
        for (int i = 0; i < myBricks.size(); i++) {
            if (myBricks.get(i).moving) {
                checkWallCollision(myBricks.get(i));
            }
        }
    }

    private void checkWallCollision(MovingObject object){
        double minX = object.getBoundsInParent().getMinX();
        double maxX = object.getBoundsInParent().getMaxX();
        double minY = object.getBoundsInParent().getMinY();
        double maxY = object.getBoundsInParent().getMaxY();

        if (maxX >= SIZE || minX <= 0){
            object.setXDir(-1*object.xVelocity);
        }
        if (object instanceof Bouncer) {
            if (minY <= STATUS_BAR_HEIGHT ){
                object.setYDir(-1*object.yVelocity);
            }
            if (maxY >= WINDOW_HEIGHT){
                object.setXDir(0);
                object.setYDir(0);
                playerLives -= 1;
                playerLivesTxt.setText("Lives: " + playerLives);
                object.setY(WINDOW_HEIGHT - object.getBoundsInParent().getHeight() - 5);
            }
        }
        object.setX(object.getX() + MOVING_OBJECT_SPEED *object.xVelocity);
        object.setY(object.getY() + MOVING_OBJECT_SPEED *object.yVelocity);
    }

    private void checkPaddleCollision (MovingObject object) {
        double objMinX = object.getBoundsInParent().getMinX();
        double objMaxX = object.getBoundsInParent().getMaxX();
        double objMaxY = object.getBoundsInParent().getMaxY();
        double paddleMinX = myPaddle.getBoundsInParent().getMinX();
        double paddleMaxX = myPaddle.getBoundsInParent().getMaxX();
        double paddleMinY = myPaddle.getBoundsInParent().getMinY();

        double paddleWidth = paddleMaxX - paddleMinX;
        boolean onPaddleLevel = objMaxY >= paddleMinY;
        boolean objIntersectsPaddle = onPaddleLevel && objMaxX >= paddleMinX && objMinX <= paddleMaxX;

        if (objIntersectsPaddle){
            if (object instanceof Bouncer) {
                reactToBouncerCollision(object, objMinX, objMaxX, paddleMinX, paddleMaxX, paddleWidth);
            } else if (object instanceof PowerUp){
                reactToPowerUpCollision(object);
            }
        }
    }

    private void reactToPowerUpCollision(MovingObject object) {
        if (((PowerUp) object).myType == 0){
            //doFirstPowerUp
            System.out.println("PowerUp1");
        } else if (((PowerUp) object).myType == 1){
            //doSecondPowerUp
            System.out.println("PowerUp2");
        } else if (((PowerUp) object).myType == 2){
            //doThirdPowerUp
            System.out.println("PowerUp3");
        }
        object.setX(1000);
        object.setY(1000);
        ((PowerUp) object).setMoving(false);
    }

    private void reactToBouncerCollision(MovingObject object, double objMinX, double objMaxX, double paddleMinX, double paddleMaxX, double paddleWidth) {
        boolean intersectsLeft = objMaxX <= paddleMinX + (paddleWidth / 3);
        boolean intersectsRight = objMinX >= paddleMaxX - (paddleWidth / 3);
        if (intersectsLeft) {
            (object).setXDir(-1);
        } else if (intersectsRight) {
            (object).setXDir(1);
        }
        (object).setYDir(-1 * (object).yVelocity);
        //give the ball a little boost off of the paddle
        object.setY(object.getY() + 2 * MOVING_OBJECT_SPEED * object.yVelocity);
    }

    private void checkBrickCollision() {
        double minBallX = myBall.getBoundsInParent().getMinX();
        double maxBallX = myBall.getBoundsInParent().getMaxX();
        double minBallY = myBall.getBoundsInParent().getMinY();
        double maxBallY = myBall.getBoundsInParent().getMaxY();
        for (Brick myBrick : myBricks) {
            boolean doesIntersect = myBall.getBoundsInParent().intersects(myBrick.getBoundsInParent());
            if (doesIntersect) {
                myBrick.myHits -= 1;
                if (myBrick.myHits == 0){
                    numBricksBroken += 1;
                }
                if (numBricksBroken == numBricksInLevel){
                    nextLevel();
                }
                ((MovingObject) myBall).setYDir(1);
                playerPoints += 100;
                playerPointsTxt.setText("Points: " + playerPoints);
            }
            updateBrick(myBrick);
        }
    }

    private void nextLevel() {

    }

    private void updateBrick(Brick myBrick) {
        if (myBrick.myHits <= 0){
            if (myBrick.hasPowerUp){
                //myPowerUps.get(myBrick.id).setMoving(true);
            }
            //moves brick off screen
            myBrick.setX(1000);
            myBrick.setY(1000);
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
        else if (code == KeyCode.R) {
            myPaddle.setX(SIZE/2.0 - myPaddle.getBoundsInLocal().getWidth() / 2);
            myPaddle.setY(SIZE + STATUS_BAR_HEIGHT - myPaddle.getBoundsInLocal().getHeight() - 5);
            myBall.setX(SIZE/2.0 - myBall.getBoundsInLocal().getWidth() / 2);
            myBall.setY(SIZE + STATUS_BAR_HEIGHT - myBall.getBoundsInLocal().getHeight() - myPaddle.getBoundsInLocal().getHeight() - 10);
            ((MovingObject)myBall).setYDir(-1);
            ((MovingObject)myBall).setXDir(1);
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

    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}

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

import java.io.FileNotFoundException;
import java.util.Objects;

import javafx.scene.text.*;

import java.util.ArrayList;


/**
 * An implementation of the game Breakout
 *
 * @author Grant J. LoPresti
 */
public class Main extends Application {
    public static final String TITLE = "BREAKOUT (gjl13)";
    public static final int SIZE = 455;
    public static final int WINDOW_HEIGHT = SIZE + GameSetup.STATUS_BAR_HEIGHT;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final Paint SPLASH_SCREEN_BACKGROUND = Color.LIGHTGOLDENRODYELLOW;
    public static final Paint LEVEL_SCREEN_BACKGROUND = Color.MEDIUMPURPLE;
    public static final String BOUNCER_IMAGE = "ball.gif";
    public static final String PADDLE_IMAGE = "paddle.gif";
    public static final String LEVEL_ONE_BRICKS = "resources/levelOneBricks.txt";
    public static final String LEVEL_TWO_BRICKS = "resources/levelTwoBricks.txt";
    public static final String LEVEL_THREE_BRICKS = "resources/levelThreeBricks.txt";
    public static final String LEVEL_FOUR_BRICKS = "resources/levelFourBricks.txt";
    public static final int PADDLE_SPEED = 10;
    public static final int MOVING_OBJECT_SPEED = 3;
    private static Bouncer myBall;

    private static Scene levelScreen;
    public static ImageView myPaddle;
    private static final Rectangle myStatusBar = new Rectangle(0, 0, SIZE, GameSetup.STATUS_BAR_HEIGHT);
    private static GameSetup myGame = new GameSetup();
    private static int playerLives = 3;
    private static int playerPoints = 0;
    private static int numBricksBroken = 0;
    private static int currentLevel = 1;
    private static final Group levelsRoot = new Group();
    private static Text playerLivesTxt;
    private static Text playerPointsTxt;
    public final Image powerUpImageS =
            new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("sizepower.gif")));
    public final Image powerUpImageL =
            new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("laserpower.gif")));
    public final Image powerUpImageP =
            new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("pointspower.gif")));
    public final Image powerUpImageB =
            new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("extraballpower.gif")));


    /**
     * Initialize what will be displayed and how it will be updated.
     */
    @Override
    public void start (Stage stage) throws FileNotFoundException {
        // attach scene to the stage and display it
        // some things needed to remember during game
        Scene splashScreen = setupSplashScreen();
        stage.setScene(splashScreen);
        stage.setTitle(TITLE);
        stage.show();
        levelScreen = setupGame();
        splashScreen.setOnMouseClicked(e -> stage.setScene(levelScreen));
        // attach "game loop" to timeline to play it (basically just calling step() method repeatedly forever)
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> {
            try {
                step();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    private Scene setupSplashScreen() {
        // create one top level collection to organize the things in the scene
        Group root = new Group();
        //create and format text in status bar
        Text welcomeTxt = new Text(10, SIZE/2.0, "Welcome to Breakout!");
        Text subTxt = new Text(100,  SIZE/2.0 + 50,  "Grant LoPresti (gjl13)");
        Text instructionTxt = new Text(55,  SIZE - 5,  "click anywhere to begin\npress SPACE to start ball on " +
                "next screen\nuse arrow keys to control the paddle");
        setWelcomeScreenFonts(welcomeTxt, subTxt, instructionTxt);
        //add elements to the root
        addWelcomeTextToRoot(root, welcomeTxt, subTxt, instructionTxt);
        // create a place to see the shapes
        Scene scene = new Scene(root, Main.SIZE, Main.WINDOW_HEIGHT, Main.SPLASH_SCREEN_BACKGROUND);
        // respond to input`
        scene.setOnKeyPressed(e -> {
            try {
                handleKeyInput(e.getCode());
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        return scene;
    }

    private void setWelcomeScreenFonts(Text welcomeTxt, Text subTxt, Text instructionTxt) {
        welcomeTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
        subTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        instructionTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        instructionTxt.setTextAlignment(TextAlignment.CENTER);
    }

    private void addWelcomeTextToRoot(Group root, Text welcomeTxt, Text subTxt, Text instructionTxt) {
        root.getChildren().add(welcomeTxt);
        root.getChildren().add(subTxt);
        root.getChildren().add(instructionTxt);
    }

    // Create the game's "scene": what shapes will be in the game and their starting properties
    private Scene setupGame() throws FileNotFoundException {
        //Creates upper status bar
        myStatusBar.setFill(Color.YELLOW);
        //create and format text in status bar
        playerLivesTxt = new Text(10, 30, "Lives: " + playerLives);
        playerPointsTxt = new Text(SIZE - 150, 30,  "Points: " + playerPoints);
        playerLivesTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        playerPointsTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        myGame.makeBricksFromFile(LEVEL_ONE_BRICKS);
        makePowerUps();
        myBall =
                new Bouncer(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(BOUNCER_IMAGE))));
        myPaddle = new ImageView(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(PADDLE_IMAGE))));
        //Place paddle and ball in center
        sendBallAndPaddleToCenter();
        // order added to the group is the order in which they are drawn
        addAllObjectsToGameRoot();
        // create a place to see the shapes
        Scene scene = new Scene(levelsRoot, Main.SIZE, Main.WINDOW_HEIGHT, Main.LEVEL_SCREEN_BACKGROUND);
        // respond to input
        scene.setOnKeyPressed(e -> {
            try {
                handleKeyInput(e.getCode());
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        return scene;
    }

    private void makePowerUps(){
        for (Brick myBrick: GameSetup.myBricks) {
            //creates power up with random type
            PowerUp myPowerUp = createPowerUp(myBrick);
            //add power up to respective Brick object
            myBrick.addPowerUp(myPowerUp);
        }
    }

    private PowerUp createPowerUp(Brick myBrick) {
        String powerUpType;
        Image image;
        //random number for random powerup assignment
        double tempRandom = Math.random();
        if (tempRandom < 0.25){
            powerUpType = "SLOW";
            image = powerUpImageS;
        } else if (tempRandom < 0.5){
            powerUpType = "LIFE";
            image = powerUpImageL;
        } else if (tempRandom < 0.75) {
            powerUpType = "PADDLE";
            image = powerUpImageP;
        } else {
            powerUpType = "BONUS";
            image = powerUpImageB;
        }
        // creates and returns new powerup
        return new PowerUp(image, powerUpType, myBrick);
    }

    private static void sendBallAndPaddleToCenter() {
        myPaddle.setX(SIZE/2.0 - myPaddle.getBoundsInLocal().getWidth() / 2);
        myPaddle.setY(SIZE + GameSetup.STATUS_BAR_HEIGHT - myPaddle.getBoundsInLocal().getHeight() - 5);
        myBall.setX(SIZE/2.0 - myBall.getBoundsInLocal().getWidth() / 2);
        myBall.setY(SIZE + GameSetup.STATUS_BAR_HEIGHT - myBall.getBoundsInLocal().getHeight() - myPaddle.getBoundsInLocal().getHeight() - 10);
        myBall.setXVel(0);
        myBall.setYVel(0);
    }

    private void addAllObjectsToGameRoot() {
        Main.levelsRoot.getChildren().add(myStatusBar);
        Main.levelsRoot.getChildren().add(playerLivesTxt);
        Main.levelsRoot.getChildren().add(playerPointsTxt);
        Main.levelsRoot.getChildren().add(myBall);
        Main.levelsRoot.getChildren().add(myPaddle);
        addBricksAndPowerUpsToRoot();
    }

    private void addBricksAndPowerUpsToRoot() {
        for (Brick myBrick : GameSetup.myBricks) {
            //Add powerUps underneath bricks]
            Main.levelsRoot.getChildren().add(myBrick.myPowerUp);
            //Add bricks to root
            Main.levelsRoot.getChildren().add(myBrick);
        }
    }

    // Change properties of shapes in small ways to animate them over time
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start
    private void step() throws FileNotFoundException {
        // update "actors" attributes
        myBall.checkWallCollision();
        myBall.checkPaddleCollision();
        updatePowerUps();
        makeMovingBricksMove();
        checkBricksCollision();
        checkEndGame();
    }

    private void updatePowerUps() {
        for (Brick myBrick : GameSetup.myBricks) {
            myBrick.updatePowerUp();
        }
    }

    private void makeMovingBricksMove() {
        for (Brick myBrick : GameSetup.myBricks) {
            if (myBrick.moving) {
                myBrick.checkWallCollision();
            }
        }
    }

    private static void moveImageViewOffScreen(ImageView myPowerUp) {
        myPowerUp.setX(1000);
        myPowerUp.setY(1000);
    }

    private void checkBricksCollision() throws FileNotFoundException {
        for (Brick myBrick : GameSetup.myBricks) {
            boolean doesIntersect = myBall.getBoundsInParent().intersects(myBrick.getBoundsInParent());
            if (doesIntersect) {
                handleBrickCollision(myBrick);
            }
            myBrick.removeIfBroken();
        }
    }

    private void handleBrickCollision(Brick myBrick) throws FileNotFoundException {
        myBrick.myHits -= 1;
        if (myBrick.myHits == 0){
            breakBrick(myBrick);
        }
        if (numBricksBroken == GameSetup.numBricksInLevel){
            nextLevel();
        }
        myBall.bounceOffHorzSurface();
    }

    private void breakBrick(Brick myBrick) {
        numBricksBroken += 1;
        playerPoints += myBrick.pointVal;
        playerPointsTxt.setText("Points: " + playerPoints);
    }

    private void nextLevel() throws FileNotFoundException {
        clearPreviousLevel();
        if (currentLevel >= 4) {
            endGame("You Win!");
            return;
        }
        makeBricksForNextLevel();
        currentLevel++;
        makePowerUps();
        sendBallAndPaddleToCenter();
        addBricksAndPowerUpsToRoot();
    }

    private static void clearPreviousLevel() {
        for (Brick myBrick: GameSetup.myBricks){
            moveImageViewOffScreen(myBrick);
            moveImageViewOffScreen(myBrick.myPowerUp);
        }
        GameSetup.myBricks = new ArrayList<>();
    }

    private void makeBricksForNextLevel() throws FileNotFoundException {
        if (currentLevel == 0){
            myGame.makeBricksFromFile(LEVEL_ONE_BRICKS);
        }else if (currentLevel == 1){
            myGame.makeBricksFromFile(LEVEL_TWO_BRICKS);
        } else if (currentLevel == 2){
            myGame.makeBricksFromFile(LEVEL_THREE_BRICKS);
        } else if (currentLevel == 3){
            myGame.makeBricksFromFile(LEVEL_FOUR_BRICKS);
        }
    }

    private static void checkEndGame() {
        if (playerLives <= 0) {
            endGame("You Lose");
        }
    }

    private static void endGame(String message) {
        clearPreviousLevel();
        displayEndGameMessage(message);
        clearEntireScreen();
    }

    private static void displayEndGameMessage(String s) {
        System.out.println(s);
        Text textMessage = new Text(70, 250, s);
        textMessage.setFont(Font.font("Verdana", FontWeight.BOLD, 55));
        levelsRoot.getChildren().add(textMessage);
    }

    private static void clearEntireScreen() {
        playerPointsTxt.setX(1000);
        playerLivesTxt.setX(1000);
        myStatusBar.setX(1000);
        moveImageViewOffScreen(myPaddle);
        moveImageViewOffScreen(myBall);
    }

    // What to do each time a key is pressed
    private void handleKeyInput (KeyCode code) throws FileNotFoundException {
        boolean ballStationary = myBall.xVelocity == 0 && myBall.yVelocity == 0;
        if (code == KeyCode.RIGHT) {
            handleRightKeyPress(ballStationary);
        }
        else if (code == KeyCode.LEFT) {
            handleLeftKeyPress(ballStationary);
        }
        else if (code == KeyCode.R) {
            sendBallAndPaddleToCenter();
        }
        else if (code == KeyCode.SPACE){
            if (ballStationary) {
                myBall.shootBall();
            }
        }
        else if (code == KeyCode.D) {
            playerLives --;
            playerLivesTxt.setText("Lives: " + playerLives);
        }
        else if (code == KeyCode.P) {
            myPaddle.setFitWidth(myPaddle.getBoundsInParent().getWidth() + 50);
            myPaddle.setX(myPaddle.getX() - 25);
        }
        else if (code == KeyCode.L) {
            playerLives ++;
            playerLivesTxt.setText("Lives: " + playerLives);
        } else if (code == KeyCode.N) {
            nextLevel();
        } else if (code == KeyCode.DIGIT1) {
            currentLevel = 0;
            nextLevel();
        } else if (code == KeyCode.DIGIT2) {
            currentLevel = 1;
            nextLevel();
        } else if (code == KeyCode.DIGIT3) {
            currentLevel = 2;
            nextLevel();
        } else if (code == KeyCode.DIGIT4) {
            currentLevel = 3;
            nextLevel();
        }
    }

    private void handleLeftKeyPress(boolean ballStationary) {
        if (myPaddle.getBoundsInParent().getMinX() > 0) {
            moveBallAndPaddle(ballStationary, -1);
        } else {
            phasePaddleBallLeftToRight(ballStationary);
        }
    }

    private void phasePaddleBallLeftToRight(boolean ballStationary) {
        myPaddle.setX(SIZE - myPaddle.getBoundsInParent().getWidth());
        if (ballStationary){
            myBall.setX(SIZE - myPaddle.getBoundsInParent().getWidth()/2 - myBall.getBoundsInParent().getWidth()/2);
        }
    }

    private void handleRightKeyPress(boolean ballStationary) {
        if (myPaddle.getBoundsInParent().getMaxX() < SIZE) {
            moveBallAndPaddle(ballStationary, 1);
        } else {
            phasePaddleBallRightToLeft(ballStationary);
        }
    }

    private void phasePaddleBallRightToLeft(boolean ballStationary) {
        myPaddle.setX(0);
        if (ballStationary){
            myBall.setX(0 + myPaddle.getBoundsInParent().getWidth()/2 - myBall.getBoundsInParent().getWidth()/2);
        }
    }

    private void moveBallAndPaddle(boolean ballStationary, int direction) {
        myPaddle.setX(myPaddle.getX() + PADDLE_SPEED * direction);
        if (ballStationary) {
            myBall.setX(myBall.getX() + PADDLE_SPEED * direction);
        }
    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }

    public static void reactToPowerUpCollision(String myType) {
        switch (myType) {
            case "SLOW":
                //set ball speed to 60%
                myBall.setXVel(myBall.xVelocity * 0.6);
                myBall.setYVel(myBall.yVelocity * 0.6);
                break;
            case "LIFE":
                //give 5 bonus lives
                playerLives += 5;
                playerLivesTxt.setText("Lives: " + playerLives);
                break;
            case "PADDLE":
                //increase paddle size by 50
                myPaddle.setFitWidth(myPaddle.getBoundsInParent().getWidth() + 50);
                myPaddle.setX(myPaddle.getX() - 25);
                break;
            case "BONUS":
                //give 2500 bonus points
                playerPoints += 2500;
                playerPointsTxt.setText("Points: " + playerPoints);
                break;
        }
    }

    public static void loseLife(){
        playerLives -= 1;
        playerLivesTxt.setText("Lives: " + playerLives);
        sendBallAndPaddleToCenter();
        checkEndGame();
    }
}
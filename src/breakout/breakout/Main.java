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
    public static final Paint SPLASH_SCREEN_BACKGROUND = Color.LIGHTGOLDENRODYELLOW;
    public static final Paint LEVEL_SCREEN_BACKGROUND = Color.MEDIUMPURPLE;
    public static final String BOUNCER_IMAGE = "ball.gif";
    public static final String PADDLE_IMAGE = "paddle.gif";
    public static final String BRICK_IMAGE_0 = "brick3.gif";
    public static final String BRICK_IMAGE_1 = "brick8.gif";
    public static final String BRICK_IMAGE_2 = "brick7.gif";
    public static final String BRICK_IMAGE_3 = "brick9.gif";
    public static final String BRICK_IMAGE_4 = "brick6.gif";
    public static final String POWER_UP_IMAGE_S = "sizepower.gif";
    public static final String POWER_UP_IMAGE_L = "laserpower.gif";
    public static final String POWER_UP_IMAGE_P = "pointspower.gif";
    public static final String POWER_UP_IMAGE_B = "extraballpower.gif";
    public static final String LEVEL_ONE_BRICKS = "resources/levelOneBricks.txt";
    public static final String LEVEL_TWO_BRICKS = "resources/levelTwoBricks.txt";
    public static final String LEVEL_THREE_BRICKS = "resources/levelThreeBricks.txt";
    public static final String LEVEL_FOUR_BRICKS = "resources/levelFourBricks.txt";
    public static final int PADDLE_SPEED = 10;
    public static final int MOVING_OBJECT_SPEED = 3;
    public static final int BRICK_SPACING = 5;
    public static final int MAX_BRICKS_TALL = 8;
    public static final int MAX_BRICKS_WIDE = 6;

    private Scene levelScreen;
    private ImageView myBall;
    private ImageView myPaddle;
    private final Rectangle myStatusBar = new Rectangle(0, 0, SIZE, STATUS_BAR_HEIGHT);
    private ArrayList<Brick> myBricks = new ArrayList<>();
    private static int playerLives = 3;
    private static int playerPoints = 0;
    private static int numBricksInLevel = 0;
    private static int numBricksBroken = 0;
    private static int currentLevel = 1;
    private final Group levelsRoot = new Group();
    public static Text playerLivesTxt;
    public static Text playerPointsTxt;


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

    private void addWelcomeTextToRoot(Group root, Text welcomeTxt, Text subTxt, Text instructionTxt) {
        root.getChildren().add(welcomeTxt);
        root.getChildren().add(subTxt);
        root.getChildren().add(instructionTxt);
    }

    private void setWelcomeScreenFonts(Text welcomeTxt, Text subTxt, Text instructionTxt) {
        welcomeTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
        subTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        instructionTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        instructionTxt.setTextAlignment(TextAlignment.CENTER);
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
        makeBricksFromFile(LEVEL_ONE_BRICKS);
        makePowerUps();
        myBall =
                new Bouncer(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(BOUNCER_IMAGE))));
        myPaddle = new ImageView(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(PADDLE_IMAGE))));
        //Place paddle and ball in center
        sendBallAndPaddleToCenter();
        // order added to the group is the order in which they are drawn
        addAllObjectsToGameRoot(levelsRoot);
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

    private void addAllObjectsToGameRoot(Group root) {
        root.getChildren().add(myStatusBar);
        root.getChildren().add(playerLivesTxt);
        root.getChildren().add(playerPointsTxt);
        root.getChildren().add(myBall);
        root.getChildren().add(myPaddle);
        addBricksAndPowerUpsToRoot(root);
    }

    private void addBricksAndPowerUpsToRoot(Group root) {
        for (Brick myBrick : myBricks) {
            //Add powerUps underneath bricks]
            root.getChildren().add(myBrick.myPowerUp);
            //Add bricks to root
            root.getChildren().add(myBrick);
        }
    }

    private void makePowerUps(){
        Image powerUpImageS =
                new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(POWER_UP_IMAGE_S)));
        Image powerUpImageL =
                new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(POWER_UP_IMAGE_L)));
        Image powerUpImageP = new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(POWER_UP_IMAGE_P)));
        Image powerUpImageB =
                new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(POWER_UP_IMAGE_B)));
        for (Brick myBrick:myBricks) {
            double centerX = myBrick.getX() + myBrick.getBoundsInParent().getWidth()/2;
            double centerY = myBrick.getY() + myBrick.getBoundsInParent().getHeight()/2;
            //creates power up with random type
            PowerUp myPowerUp = createPowerUp(powerUpImageS, powerUpImageL, powerUpImageP, powerUpImageB);
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
            moveImageViewOffScreen(myPowerUp);
        }
    }

    private void moveImageViewOffScreen(ImageView myPowerUp) {
        myPowerUp.setX(1000);
        myPowerUp.setY(1000);
    }

    private void sendBallAndPaddleToCenter() {
        myPaddle.setX(SIZE/2.0 - myPaddle.getBoundsInLocal().getWidth() / 2);
        myPaddle.setY(SIZE + STATUS_BAR_HEIGHT - myPaddle.getBoundsInLocal().getHeight() - 5);
        myBall.setX(SIZE/2.0 - myBall.getBoundsInLocal().getWidth() / 2);
        myBall.setY(SIZE + STATUS_BAR_HEIGHT - myBall.getBoundsInLocal().getHeight() - myPaddle.getBoundsInLocal().getHeight() - 10);
        ((MovingObject)myBall).setXVel(0);
        ((MovingObject)myBall).setYVel(0);
    }

    private PowerUp createPowerUp(Image powerUpImageS, Image powerUpImageL, Image powerUpImageP, Image powerUpImageB) {
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
        return new PowerUp(image, powerUpType);
    }

    private void makeBricksFromFile(String levelLayoutFile) throws FileNotFoundException {
        //Read text file
        File file = new File(levelLayoutFile);
        Scanner sc = new Scanner(file);
        int[][] levelBricks = createArrayOfBrickTypes(sc);
        // make some shapes and set their properties
        double brickHeight = 20.0;
        for (int i = 0; i < MAX_BRICKS_TALL; i++){
            createBricksRow(BRICK_SPACING*(i+1) + (int)brickHeight*i + STATUS_BAR_HEIGHT, levelBricks[i]);
        }
    }

    private int[][] createArrayOfBrickTypes(Scanner sc) {
        int[][] levelBricks = new int[MAX_BRICKS_TALL][MAX_BRICKS_WIDE];
        for (int i = 0; i < MAX_BRICKS_TALL; i++) {
            for (int j = 0; j < MAX_BRICKS_WIDE; j++) {
                levelBricks[i][j] = Integer.parseInt(sc.next());
            }
        }
        return levelBricks;
    }

    private void createBricksRow(int placementHeight, int[] blockTypes){
        ArrayList<Image> brickImages = new ArrayList<>();
        brickImages.add(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(BRICK_IMAGE_0))));
        brickImages.add(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(BRICK_IMAGE_1))));
        brickImages.add(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(BRICK_IMAGE_2))));
        brickImages.add(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(BRICK_IMAGE_3))));
        brickImages.add(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(BRICK_IMAGE_4))));

        double brickWidth = 70;
        for (int i = 0; i < MAX_BRICKS_WIDE; i++){
            createBrick(placementHeight, blockTypes[i], brickImages, BRICK_SPACING * (i + 1) + i * brickWidth);
        }

    }

    private void createBrick(int placementHeight, int blockType, ArrayList<Image> brickImages, double value) {
        Brick myBrick;
        myBrick = createBrickAccordingToType(blockType, brickImages);
        myBrick.setX(value);
        myBrick.setY(placementHeight);
        if (myBrick.myHits != 0){
            numBricksInLevel += 1;
        }
        myBricks.add(myBrick);
    }

    private Brick createBrickAccordingToType(int blockType, ArrayList<Image> brickImages) {
        Brick myBrick;
        if (blockType == 1) {
            myBrick = new Brick(brickImages.get(1),1);
        } else if (blockType == 2){
            myBrick = new Brick(brickImages.get(2),2);
        } else if (blockType == 3) {
            myBrick = new Brick(brickImages.get(3),3);
        } else if (blockType == 4){
            myBrick = new Brick(brickImages.get(4),4);
        } else {
            myBrick = new Brick(brickImages.get(0), 0);
        }
        return myBrick;
    }

    // Change properties of shapes in small ways to animate them over time
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start
    private void step() throws FileNotFoundException {
        // update "actors" attributes
        checkWallCollision((MovingObject) myBall);
        checkPaddleCollision((MovingObject) myBall);
        updatePowerUps();
        makeMovingBricksMove();
        checkBrickCollision();
        checkEndGame();
    }

    private void checkEndGame() {
        if (playerLives <= 0) {
            endGame("You Lose");
        }
    }

    private void checkWallCollision(MovingObject object) {
        double minX = object.getBoundsInParent().getMinX();
        double maxX = object.getBoundsInParent().getMaxX();
        double minY = object.getBoundsInParent().getMinY();
        double maxY = object.getBoundsInParent().getMaxY();
        if (maxX >= SIZE || minX <= 0){
            bounceOffSideWalls(object);
        }
        if (object instanceof Bouncer) {
            if (minY <= STATUS_BAR_HEIGHT ){
                bounceOffHorzSurface(object);
            }
            if (maxY >= WINDOW_HEIGHT){
                dealWithBallHitsBottomScreen(object);
            }
        }
        incrementObjectsPosition(object);
    }

    private void incrementObjectsPosition(MovingObject object) {
        object.setX(object.getX() + MOVING_OBJECT_SPEED *object.xVelocity);
        object.setY(object.getY() + MOVING_OBJECT_SPEED *object.yVelocity);
    }

    private void dealWithBallHitsBottomScreen(MovingObject object) {
        object.setXVel(0);
        object.setYVel(0);
        playerLives -= 1;
        playerLivesTxt.setText("Lives: " + playerLives);
        sendBallAndPaddleToCenter();
        checkEndGame();
    }

    private void bounceOffHorzSurface(MovingObject object) {
        object.setYVel(-1*object.yVelocity);
    }

    private void bounceOffSideWalls(MovingObject object) {
        object.setXVel(-1*object.xVelocity);
    }

    private void updatePowerUps() {
        for (Brick myBrick : myBricks) {
            PowerUp myPowerUp = myBrick.myPowerUp;
            if (myBrick.hasPowerUp && myBrick.myHits == 0) {
                setPowerUpFalling(myBrick, myPowerUp);
            }
        }
    }

    private void setPowerUpFalling(Brick myBrick, PowerUp myPowerUp) {
        myBrick.myPowerUp.setMoving(true);
        checkPaddleCollision(myBrick.myPowerUp);
        myPowerUp.setY(myPowerUp.getY() + MOVING_OBJECT_SPEED * myPowerUp.yVel);
    }

    private void makeMovingBricksMove() {
        for (Brick myBrick : myBricks) {
            if (myBrick.moving) {
                checkWallCollision(myBrick);
            }
        }
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
        switch (((PowerUp) object).myType) {
            case "SLOW":
                //set ball speed to 60%
                ((MovingObject) myBall).setXVel(((MovingObject) myBall).xVelocity * 0.6);
                ((MovingObject) myBall).setYVel(((MovingObject) myBall).yVelocity * 0.6);
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
        moveImageViewOffScreen(object);
        ((PowerUp) object).setMoving(false);
    }

    private void reactToBouncerCollision(MovingObject object, double objMinX, double objMaxX, double paddleMinX, double paddleMaxX, double paddleWidth) {
        boolean intersectsLeft = objMaxX <= paddleMinX + (paddleWidth / 3);
        boolean intersectsRight = objMinX >= paddleMaxX - (paddleWidth / 3);
        if (intersectsLeft) {
            (object).setXVel(-1);
        } else if (intersectsRight) {
            (object).setXVel(1);
        }
        bounceOffHorzSurface(object);
        //give the ball a little boost off of the paddle
        object.setY(object.getY() + 2 * MOVING_OBJECT_SPEED * object.yVelocity);
    }

    private void checkBrickCollision() throws FileNotFoundException {
        for (Brick myBrick : myBricks) {
            boolean doesIntersect = myBall.getBoundsInParent().intersects(myBrick.getBoundsInParent());
            if (doesIntersect) {
                handleBrickCollision(myBrick);
            }
            removeIfBroken(myBrick);
        }
    }

    private void handleBrickCollision(Brick myBrick) throws FileNotFoundException {
        myBrick.myHits -= 1;
        if (myBrick.myHits == 0){
            breakBrick(myBrick);
        }
        if (numBricksBroken == numBricksInLevel){
            nextLevel();
        }
        bounceOffHorzSurface((MovingObject) myBall);
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
        addBricksAndPowerUpsToRoot(levelsRoot);
    }

    private void makeBricksForNextLevel() throws FileNotFoundException {
        if (currentLevel == 0){
            makeBricksFromFile(LEVEL_ONE_BRICKS);
        }else if (currentLevel == 1){
            makeBricksFromFile(LEVEL_TWO_BRICKS);
        } else if (currentLevel == 2){
            makeBricksFromFile(LEVEL_THREE_BRICKS);
        } else if (currentLevel == 3){
            makeBricksFromFile(LEVEL_FOUR_BRICKS);
        }
    }

    private void endGame(String message) {
        clearPreviousLevel();
        displayEndGameMessage(message);
        clearEntireScreen();
    }

    private void clearPreviousLevel() {
        for (Brick myBrick:myBricks){
            moveImageViewOffScreen(myBrick);
            moveImageViewOffScreen(myBrick.myPowerUp);
        }
        myBricks = new ArrayList<>();
    }

    private void displayEndGameMessage(String s) {
        System.out.println(s);
        Text textMessage = new Text(70, 250, s);
        textMessage.setFont(Font.font("Verdana", FontWeight.BOLD, 55));
        levelsRoot.getChildren().add(textMessage);
    }

    private void clearEntireScreen() {
        playerPointsTxt.setX(1000);
        playerLivesTxt.setX(1000);
        myStatusBar.setX(1000);
        moveImageViewOffScreen(myPaddle);
        moveImageViewOffScreen(myBall);
    }

    private void removeIfBroken(Brick myBrick) {
        if (myBrick.myHits <= 0){
            //moves brick off screen
            moveImageViewOffScreen(myBrick);
        }
    }

    // What to do each time a key is pressed
    private void handleKeyInput (KeyCode code) throws FileNotFoundException {
        boolean ballStationary = ((MovingObject)myBall).xVelocity == 0 && ((MovingObject)myBall).yVelocity == 0;
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
                shootBall();
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

    private void shootBall() {
        ((MovingObject)myBall).setYVel(-1);
        //50-50 chance of right or left release
        if (Math.random() < 0.5){
            ((MovingObject)myBall).setXVel(1);
        } else {
            ((MovingObject)myBall).setXVel(-1);
        }
    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}
package breakout;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class GameSetup {
    public static final String BRICK_IMAGE_0 = "brick3.gif";
    public static final String BRICK_IMAGE_1 = "brick8.gif";
    public static final String BRICK_IMAGE_2 = "brick7.gif";
    public static final String BRICK_IMAGE_3 = "brick9.gif";
    public static final String BRICK_IMAGE_4 = "brick6.gif";

    public static final int BRICK_SPACING = 5;
    public static final int MAX_BRICKS_TALL = 8;
    public static final int MAX_BRICKS_WIDE = 6;
    public static final int STATUS_BAR_HEIGHT = 50;
    static int numBricksInLevel = 0;
    static ArrayList<Brick> myBricks = new ArrayList<>();

    public void makeBricksFromFile(String levelLayoutFile) throws FileNotFoundException {
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
}

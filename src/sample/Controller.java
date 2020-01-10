package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.util.Duration;
import sample.Shapes.*;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable{

    public Timer timer;
    public Timeline timeline;
    public KeyFrame keyFrame;
    public @FXML Canvas canvas;
    public @FXML Canvas nextCanvas;
    public CurrentShape currShape;
    public TetrisBoard board;
    public ShapePosition shapePosition;

    public int nextShapeCode;
    public GraphicsContext gc;
    public int goingToBoardCode;
    public boolean gameOver = true;
    public static Controller instance;
    public boolean instantDropExecuted = false;

    public NextShapeField nextShapeField;
    public @FXML Button startButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        gameInit();
    }

    public void gameInit() {
        nextShapeCode = -1;

        gc = nextCanvas.getGraphicsContext2D();
        gameOver = false;

        board = new TetrisBoard(canvas,20.0);
        shapePosition = new ShapePosition();

        board.assignColors();
        board.draw();

        nextShapeField = new NextShapeField(nextCanvas,20);
        nextShapeField.assignColor(board.getColorMapLight(), board.getColorMapMidTone(), board.getColorMapDark());
        nextShapeField.emptyField();

        keyFrame = new KeyFrame(Duration.millis(600), e -> moveShape());
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Check for lines to be removed and brings a new shape to board.
     */
    public void newShape() {
        if (nextShapeCode < 0) {
            nextShapeCode = getRandomInt(7);
        }

        goingToBoardCode = nextShapeCode;
        placeShape(goingToBoardCode);
    }

    public void setNextShape(int shapeCode) {
        Shape shape = shapeFactory(shapeCode);
        nextShapeField.setShape(shape.getShape(),shape.getColorCode());
    }

    public Shape shapeFactory(int shapeCode) {
        Shape shape = null;
        switch (shapeCode) {
            case 0: shape = new BlockShape(); break;
            case 1: shape = new IShape(); break;
            case 2: shape = new TShape(); break;
            case 3: shape = new L1Shape(); break;
            case 4: shape = new L2Shape();break;
            case 5: shape = new S1Shape(); break;
            case 6: shape = new S2Shape(); break;
        }
        return shape;
    }

    /**
     * Method that return a random integer.
     * @param limit
     */
    public int getRandomInt(int limit) {
        Random random = new Random();
        return random.nextInt(limit);
    }

    /**
     * Shapes are added to the tetris board using an integer parameter.
     * @param shapeCode
     */
    public void placeShape(int shapeCode) {
        instantDropExecuted = false;
        Shape shape = shapeFactory(shapeCode);

        currShape = new CurrentShape(
                board.getX(),
                board.getY(),
                shape.getOffset(),
                shape.getRotate(),
                shape.getMidPoint(),
                shape.getPosition(),
                shape
        );

        currShape.addShape(shape);
        currShape.setColor(shape.getColorCode());

        currShape.goOneDown();
        currShape.goOneDown();


        if (gameOver()) {
            stopGameExecution();
        } else {
            nextShapeCode = getRandomInt(7);
            setNextShape(nextShapeCode);

            reAddShape();
            checkIfLanded();
            board.draw();
            timeline.play();
        }
    }

    public void speedUp() {
        timeline.setRate(15.0);
    }

    public void speedDown() {
        timeline.setRate(1.0);
    }

    /**
     * Method is called by startAnimation() and is called every x milliseconds
     */
    public void moveShape() {
        if (currShape.hasLanded()) {
            timeline.stop();
            checkForRemovableLines();
        } else {
            removeShape();
            currShape.goOneDown();
            reAddShape();
            checkIfLanded();
        }
    }

    public boolean gameOver() {
        ArrayList<int[]> cellsEnteringBoard = currShape.get();

        for (int i = 0; i < cellsEnteringBoard.size(); i++) {

            int[] cell = cellsEnteringBoard.get(i);

            if (board.cellIsOn(cell[0],cell[1])) {
                return true;
            }
        }

        return false;
    }

    public void stopGameExecution() {
        timeline.stop();
        gameOver = true;
        currShape.setLanded(true);
    }

    public void checkForRemovableLines() {
        if (removableLines()) {
            board.blinkRemove(getRemovableLines(board.getBoard()));
            newShapeAfterWaiting(45*7);
        } else {
            newShapeWithDelay();
        }
    }

    public void newShapeWithDelay() {
        if (instantDropExecuted) {
            newShape();
        } else {
            newShapeAfterWaiting(300);
        }
    }

    /**
     * Initiates a new placement of a shape by using TimerTask class.
     * @param milliseconds
     */
    public void newShapeAfterWaiting(int milliseconds) {
        timeline.pause();

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                newShape();
            }
        };

        timer.schedule(timerTask, milliseconds);
    }

    /**
     * Checks if the shape has landed.
     */
    public void checkIfLanded() {
        if (currShape.touchingShapeBottom() || currShape.touchingBottomWall()) {
            currShape.setLanded(true);
        } else {
            currShape.setLanded(false);
        }
    }

    public void cancelNewPlacement() {
        if ((timer != null) ) {
            timer.cancel();
        }
    }

    /**
     * Checks if shape can keep on calling moveShape().
     * */
    public void checkIfShapeCanContinue() {
        if (!currShape.touchingShapeBottom() && !currShape.touchingBottomWall()) {

            currShape.setLanded(false);

            cancelNewPlacement();

            timeline.play();
        }
    }

    /**
     * Moves current shape one to the left by removing it, moving and readding it.
     * */
    public void goLeft() {
        if (!currShape.touchingShapeLeft() && currShape.hasEnteredBoard()) {
            removeShape();
            currShape.goOneLeft();
            reAddShape();
        }

        checkIfLanded();
        checkIfShapeCanContinue();
    }

    /**
     * Moves current shape one to the right by removing it, moving and readding it.
     * */
    public void goRight() {
        if (!currShape.touchingShapeRight() && currShape.hasEnteredBoard()) {
            removeShape();
            currShape.goOneRight();
            reAddShape();
        }

        checkIfLanded();
        checkIfShapeCanContinue();
    }

    // Removes shape from board by changing array elements to 0.
    public void removeShape() {
        for (int[] i : currShape.get()) {
            board.cellOff(i[0],i[1],true);
        }
    }

    /**
     * Addes shape to board by changing array elements to shape number.
     */
    public void reAddShape() {
        for (int[] i : currShape.get()) {
            board.cellOn(i[0],i[1],currShape.getColor(),true);
        }
    }

    /**
     * Takes int-array as parameter and looks for full lines that can
     * be removed by using a for-loop. The lines that can be removed
     * are added to an ArrayList, reversed and returned.
     */
    public ArrayList<Integer> getRemovableLines(int[][] board) {
        ArrayList<Integer> lines = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            if (Arrays.stream(board[i]).filter(j -> j == 0).count() == 0) lines.add(i);
        }

        Collections.reverse(lines);
        return lines;
    }

    public boolean removableLines(int[][] board) {
        return getRemovableLines(board).size() > 0;
    }

    public boolean removableLines() {
        return removableLines(board.getBoard());
    }

    /**
     * Moves the shape down using moveShape until it touches another shape or floor.
     * */
    public void instantDrop() {
        timeline.stop();
        removeShape();

        while (!currShape.hasLanded()) {
            instantDropExecuted = true;
            currShape.goOneDown();
            checkIfLanded();
        }

        reAddShape();
        checkForRemovableLines();
    }



    // TODO: Use built-in clone method




    /**
     * Rotates shapes in the directions of the parameter.
     * @param direction
     * */
    public void rotateShape(Direction direction) {
        if (currShape.canRotate()) {

            // Current position is saved
            Position currentPos = currShape.getPosition();

            // Desired position is set
            Position nextPos = direction == Direction.ClockWise ? shapePosition.getNextRight(currentPos) : shapePosition.getNextLeft(currentPos);

            int[][] curr, next;

            if (currShape.getType() instanceof IShape) {
                curr = shapePosition.getIShapeMap(currentPos);
                next = shapePosition.getIShapeMap(nextPos);
            } else {
                curr = shapePosition.getRestShapeMap(currentPos);
                next = shapePosition.getRestShapeMap(nextPos);
            }

            int[][] rotationTable = new int[5][2];

            for (int i = 0; i < curr.length; i++) {
                for (int j = 0; j < curr[0].length; j++) {
                    rotationTable[i][j] = curr[i][j] - next[i][j];
                }
            }

            removeShape();

            ArrayList<int[]> beforeRotation = clone(currShape.get());
            if (direction == Direction.ClockWise) {
                currShape.rotate();
            } else {
                currShape.rotateBack();
            }

            ArrayList<int[]> beforeKick = clone(currShape.get());
            currShape.kick(currShape.get(),rotationTable[0][0],rotationTable[0][1]);

            if (misplacedCells(currShape.get()) > 0) {
                currShape.set(clone(beforeKick));
                for (int i = 1; i < rotationTable.length; i++) {
                    currShape.kick(currShape.get(),rotationTable[i][0],rotationTable[i][1]);
                    if (misplacedCells(currShape.get()) == 0) {
                        break;
                    } else {
                        if (i == rotationTable.length - 1) {
                            currShape.set(clone(beforeRotation));
                            currShape.setRotation(false);
                        } else {
                            currShape.set(clone(beforeKick));
                        }
                    }
                }
            }
            reAddShape();
            currShape.setPosition(nextPos);
        }
        checkIfLanded();
        checkIfShapeCanContinue();
    }

    /**
     * Creates a clone of the ArrayList<int[]> that is sent as parameter and returns it.
     * @param shape
     * */
    public ArrayList<int[]> clone(ArrayList<int[]> shape) {
        ArrayList<int[]> clone = new ArrayList<>();
        for (int[] a : shape) clone.add(a.clone());

        return clone;
    }

    /**
     * Extra method used in rotateShape(Direction direction) to check if
     * the rotation resulted in a wrong position by check for cells
     * outside the board.
     * @param shape
     * */
    public int misplacedCells(ArrayList<int[]> shape) {
        long misplacedCells = shape.stream().filter(i -> !board.isInBoard(i[0],i[1]) || board.cellIsOn(i[0],i[1])).count();
        return (int) misplacedCells;
    }

    /**
     * Executed when P is pressed on the keyboard
     * */
    public void pause() {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
        } else {
            timeline.play();
        }
    }

    /**
     * Executed when N is pressed on the keyboard
     * */
    public void newGame() {
        timeline.stop();
        gameInit();
        newShape();
    }

}
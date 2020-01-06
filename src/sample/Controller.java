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
        board.drawTetrisField();

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

        if (gameOver()) {
            System.out.println("Game Over!");
            stopGameExecution();
        } else                                                                                {
            System.out.println("Game!");
            nextShapeCode = getRandomInt(7);
            setNextShape(nextShapeCode);

            reAddShape();
            checkIfLanded();
            board.drawTetrisField();
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
            removeLines(getRemovableLines(board.getBoard()), board.getBoard());
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
        if (touchingShapeBottom() || touchingBottomWall()) {
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
        if (!touchingShapeBottom() && !touchingBottomWall()) {

            currShape.setLanded(false);

            cancelNewPlacement();

            timeline.play();
        }
    }

    /**
     * Moves current shape one to the left by removing it, moving and readding it.
     * */
    public void goLeft() {
        if (!touchingShapeLeft() && currShape.hasEnteredBoard()) {
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
        if (!touchingShapeRight() && currShape.hasEnteredBoard()) {
            removeShape();
            currShape.goOneRight();
            reAddShape();
        }

        checkIfLanded();
        checkIfShapeCanContinue();
    }

    // Removes shape from board by changing array elements to 0.
    public void removeShape() {

        for (int i = 0; i < currShape.get().size(); i++) {
            int[] cell = currShape.get().get(i);
            board.cellOff(cell[0],cell[1]);
        }
    }

    /**
     * Addes shape to board by chaning array elements to shape number.
     */
    public void reAddShape() {

        for (int i = 0; i < currShape.get().size(); i++) {
            int[] cell = currShape.get().get(i);
            board.cellOn(cell[0],cell[1],currShape.getColor());
        }
    }

    /**
     * Takes int-array as parameter and looks for full lines that can
     * be removed by using a for-loop. The lines that can be removed
     * are added to an ArrayList, reversed and returned.
     */
    public ArrayList<Integer> getRemovableLines(int[][] board) {
        ArrayList<Integer> lines = new ArrayList<>();

        for (int i = 0; i < board[0].length; i++) {
            boolean fullLine = true;
            for (int j = 0; j < board.length; j++) {
                if (board[j][i] == 0) {
                    fullLine = false;
                    continue;
                }
            }
            if (fullLine) {
                lines.add(i);
            }
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
     * Method that takes an ArrayList with removable lines and the tetris
     * board and removes lines. The list is balanced using
     * balanceListForRemoval()
     **/
    public void removeLines(ArrayList<Integer> lines, int[][] board) {
        ArrayList<Integer> colorCodes = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < this.board.getX(); j++) {
                colorCodes.add(this.board.getColorCode(j,lines.get(i)));
            }
        }

        //int blinkSpeed = 35;
        int blinkSpeed = 47;
        Timer blinkTimer = new Timer();

        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Remove
                for (int i = 0; i < lines.size(); i++) {

                    for (int j = 0; j < Controller.this.board.getX(); j++) {

                        Controller.this.board.cellOffNoUpdateBoard(j,lines.get(i));
                    }
                }

                Controller.this.board.drawTetrisField();
            }
        },0);

        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Add
                for (int i = 0; i < lines.size(); i++) {

                    for (int j = 0; j < Controller.this.board.getX(); j++) {

                        Controller.this.board.cellOnNoUpdateBoard(j,lines.get(i),colorCodes.get(i*10 + j));
                    }
                }
                Controller.this.board.drawTetrisField();
            }
        },1*blinkSpeed);

        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Remove
                for (int i = 0; i < lines.size(); i++) {

                    for (int j = 0; j < Controller.this.board.getX(); j++) {

                        Controller.this.board.cellOffNoUpdateBoard(j,lines.get(i));
                    }
                }

                Controller.this.board.drawTetrisField();
            }
        },2*blinkSpeed);

        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Add
                for (int i = 0; i < lines.size(); i++) {

                    for (int j = 0; j < Controller.this.board.getX(); j++) {

                        Controller.this.board.cellOnNoUpdateBoard(j,lines.get(i),colorCodes.get(i*10 + j));
                    }
                }

                Controller.this.board.drawTetrisField();
            }
        },3*blinkSpeed);

        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Remove
                for (int i = 0; i < lines.size(); i++) {

                    for (int j = 0; j < Controller.this.board.getX(); j++) {

                        Controller.this.board.cellOffNoUpdateBoard(j,lines.get(i));
                    }
                }

                Controller.this.board.drawTetrisField();
            }
        },4*blinkSpeed);

        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Add
                for (int i = 0; i < lines.size(); i++) {

                    for (int j = 0; j < Controller.this.board.getX(); j++) {

                        Controller.this.board.cellOnNoUpdateBoard(j,lines.get(i),colorCodes.get(i*10 + j));
                    }
                }

                Controller.this.board.drawTetrisField();
            }
        },5*blinkSpeed);

        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                balanceListForRemoval(lines);

                // Removal of full lines
                for (int k = 0; k < lines.size(); k++) {
                    int destinationLine = lines.get(k);
                    int sourceLine = destinationLine - 1;
                    for (int i = destinationLine; i > 0; i--) {
                        for (int j = 0; j < board.length; j++) {

                            board[j][destinationLine] = board[j][sourceLine];
                        }
                        destinationLine--;
                        sourceLine--;
                    }
                }

            }
        },6*blinkSpeed);

        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Controller.this.board.drawTetrisField();
                newShapeWithDelay();
            }
        },(6*blinkSpeed) + 25);

    }

    /**
     * Method that takes an ArrayList with lines that should be removed
     * and adds 0,1,2... etc to indices 19,18,17... etc to adjust the lines that
     * are awaits a removal.
     *
     * Example: Line 19,18 (last and second to last) will be removed. After
     * line 19 is removed all rows above move one down and the next to be
     * removed is line 18+1=19.
     * */
    public void balanceListForRemoval(ArrayList<Integer> list) {

        Integer addOn = 0;

        for (int i = 0; i < list.size(); i++) {

            Integer curr = list.get(i);

            list.remove(i);

            list.add(i,curr+addOn);

            addOn++;
        }
    }

    /**
     * Checks if the shape has any cells out of the board on the left.
     *
     * @param shape
     * */
    public int cellsOutsideOnLeft(ArrayList<int[]> shape) {

        int outside = 0;

        for (int i = 0; i < shape.size(); i++) {

            int[] currCell = shape.get(i);

            int diff = board.boardLeftDifference(currCell[0]);

            if (diff > outside) {
                outside = diff;
            }
        }

        return outside;
    }

    /**
     * Checks if the shape has any cells out of the board on the right.
     *
     * @param shape
     * */
    public int cellsOutsideOnRight(ArrayList<int[]> shape) {

        int outside = 0;

        for (int i = 0; i < shape.size(); i++) {

            int[] currCell = shape.get(i);

            int diff = board.boardRightDifference(currCell[0]);

            if (diff > outside) {
                outside = diff;
            }
        }

        return outside;
    }

    /**
     * Moves the shape down using moveShape until it touches another shape or floor.
     * */
    public void instantDrop() {
        timeline.stop();
        instantDropExecuted = true;
        removeShape();

        while (!currShape.hasLanded()) {
            currShape.goOneDown();
            checkIfLanded();
        }

        reAddShape();
        checkForRemovableLines();
    }

    /**
     * Rotates shapes in the directions of the parameter.
     * @param direction
     * */
    public void rotateShape(Direction direction) {

        if (currShape.canRotate()) {

            // nåværende posisjon lagres
            Position currentPosition = currShape.getPosition();

            Position nextPosition;

            // ønsket posisjon blir satt
            if (direction == Direction.ClockWise) {
                nextPosition = shapePosition.getNextRight(currentPosition);
            } else {
                nextPosition = shapePosition.getNextLeft(currentPosition);
            }

            int[][] curr, next;

            if (currShape.getType() instanceof IShape) {
                curr = shapePosition.getIShapeMap(currentPosition);
                next = shapePosition.getIShapeMap(nextPosition);
            } else {
                curr = shapePosition.getRestShapeMap(currentPosition);
                next = shapePosition.getRestShapeMap(nextPosition);
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
            kick(currShape.get(),rotationTable[0][0],rotationTable[0][1]);

            if (misplacedCells(currShape.get()) > 0) {
                currShape.set(clone(beforeKick));
                for (int i = 1; i < rotationTable.length; i++) {
                    kick(currShape.get(),rotationTable[i][0],rotationTable[i][1]);
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
            currShape.setPosition(nextPosition);
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
     * Extra method used in rotateShape(Direction direction) to kick shape
     * through SRS kicks.
     * @param shape
     * @param x
     * @param y
     * */
    public void kick(ArrayList<int[]> shape, int x, int y) {

        for (int i = 0; i < shape.size(); i++) {

            int[] tmp = shape.get(i);

            tmp[0]+=x;
            tmp[1]+=-y;

            shape.set(i,tmp);
        }
    }

    /**
     * Extra method used in rotateShape(Direction direction) to check if
     * the rotation resulted in a wrong position by check for cells
     * outside the board.
     * @param shape
     * */
    public int misplacedCells(ArrayList<int[]> shape) {
        return (int) shape.stream().filter(i -> !board.isInBoard(i[0],i[1]) || board.cellIsOn(i[0],i[1])).count();
    }

    /**
     * Checks if the current shape is touching the bottom wall.
     * */
    public boolean touchingBottomWall() {
        for (int[] a : currShape.activeCellsBottom()) {
            if ( a[1] + 1 == board.getY() ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the current shape is touching another shape at the bottom.
     * */
    public boolean touchingShapeBottom() {
        for (int[] a : currShape.activeCellsBottom()) {
            if (board.cellIsOn(a[0],a[1] + 1)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the current shape is touching another shape to the right.
     * */
    public boolean touchingShapeRight() {
        for (int[] a : currShape.activeCellsRight()) {
            if (board.cellIsOn(a[0] + 1,a[1])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the current shape is touching another shape to the left.
     * */
    public boolean touchingShapeLeft() {
        for (int[] a : currShape.activeCellsLeft()) {
            if (board.cellIsOn(a[0] - 1,a[1])) {
                return true;
            }
        }

        return false;
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
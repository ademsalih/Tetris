package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.util.Duration;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable{

    Timer timer;
    Timeline timeline;
    KeyFrame keyFrame;
    @FXML Canvas canvas;
    CurrentShape currShape;
    TetrisBoard tetrisBoard;
    ShapePosition shapePosition;
    Stack<int[]> shapeReverseStack;
    public static Controller instance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        instance = this;

        tetrisBoard = new TetrisBoard(canvas,20.0);
        shapePosition = new ShapePosition();

        tetrisBoard.assignColors();
        tetrisBoard.drawTetrisField();

        newShape();

        startAnimation(1000);
    }

    // Check for lines to be removed and brings a new shape to board
    public void newShape () {

        removeLines(removableLines(tetrisBoard.getBoard()),tetrisBoard.getBoard());
        shapePlacement(getRandomInt(7));
    }

    // Method that return a random integer
    public int getRandomInt(int limit) {

        Random random = new Random();
        return random.nextInt(limit);
    }

    // Random shapes are added to the tetris board PARAMETER: decimal value
    public void shapePlacement(int shapeCode) {

        Shape shape = null;

        switch (shapeCode) {
            case 0: shape = new BlockShape();
            break;
            case 1: shape = new IShape();
            break;
            case 2: shape = new TShape();
            break;
            case 3: shape = new L1Shape();
            break;
            case 4: shape = new L2Shape();
            break;
            case 5: shape = new S1Shape();
            break;
            case 6: shape = new S2Shape();
            break;
        }

        reverseShape(shape);

        currShape = new CurrentShape(tetrisBoard.getX(),tetrisBoard.getY(),
                shape.getOffset(),shape.getRotate(),shape.getMidPoint(),
                shape.getPosition(),shape);

        currShape.addShape(shapeReverseStack);

        currShape.setColor(shape.getColorCode());

        moveShape();
    }

    // Initiates the game animation and starts the timeline by calling moveShape()
    public void startAnimation(int speed) {

        keyFrame = new KeyFrame(Duration.millis(speed), e -> moveShape());

        timeline = new Timeline(keyFrame);

        timeline.setCycleCount(Animation.INDEFINITE);

        timeline.play();
    }

    public void speedUp() {
        timeline.setRate(15.0);
    }

    public void speedDown() {
        timeline.setRate(1.0);
    }

    // Method is called by startAnimation() and is called every x milliseconds
    public void moveShape() {

        if (!currShape.hasLanded()) {
            removeShape();
            currShape.goOneDown();
            reAddShape();

            checkIfLanded();
        } else {
            newPlacementWithDelay(540);
        }
    }

    public void newPlacement() {

        timeline.pause();

        timer = new Timer();

        newShape();
        timeline.play();
        timer.cancel();
    }

    /* Initiates a new placement of a shape by using TimerTask class
     * Takes in parameter for delay
     */
    public void newPlacementWithDelay(int ms) {

        timeline.pause();

        timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                newShape();
                timeline.play();
                timer.cancel();
            }
        };

        timer.schedule(timerTask, ms);
    }

    // Checks if the shape has landed
    public void checkIfLanded() {

        if (touchingAnotherShapeBottom() || touchingBottomWall()) {
            currShape.setLanded(true);
        } else {
            currShape.setLanded(false);
        }
    }

    public void cancelNewPlacement() {

        // dont cancel if shape is stuck
        if ((timer != null) ) {
            timer.cancel();
        }
    }

    // Method that adds a shape to the stack
    public void reverseShape(Shape s) {

       shapeReverseStack = new Stack<>();

       for (int i = 0; i < s.getY(); i++) {
            shapeReverseStack.add(s.getRow());
       }

    }

    public void checkIfShapeCanContinue() {

        if (!touchingAnotherShapeBottom() && !touchingBottomWall()) {

            currShape.setLanded(false);

            cancelNewPlacement();

            timeline.play();
        }
    }

    public void goLeft() {

        if (!touchingAnotherShapeLeft()) {
            removeShape();
            currShape.goOneLeft();
            reAddShape();
        }

        checkIfLanded();

        checkIfShapeCanContinue();
    }

    public void goRight() {

        if (!touchingAnotherShapeRight()) {
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
            tetrisBoard.cellOff(cell[0],cell[1]);
        }
    }

    // Addes shape to board by chaning array elements to shape number.
    public void reAddShape() {

        for (int i = 0; i < currShape.get().size(); i++) {
            int[] cell = currShape.get().get(i);
            tetrisBoard.cellOn(cell[0],cell[1],currShape.getColor());
        }
    }

    /* Takes int-array as parameter and looks for full lines that can
     * be removed by using a for-loop. The lines that can be removed
     * are added to an ArrayList, reversed and returned.
     */
    public ArrayList<Integer> removableLines(int[][] board) {

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

    // Temporary method used to print removable lines
    public void printRemovableLines() {
        ArrayList<Integer> lines = removableLines(tetrisBoard.getBoard());

        System.out.print("Removable Lines:");

        for (int i = 0; i < lines.size(); i++) {
            System.out.print(" " + lines.get(i));
        }

        System.out.println();

        removeLines(removableLines(tetrisBoard.getBoard()),tetrisBoard.getBoard());
    }

    public void removeLines(ArrayList<Integer> lines, int[][] board) {

        balanceListForRemoval(lines);

        for (int k = 0; k < lines.size(); k++) {

            System.out.println(lines.get(k));

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

    // Problem: The list keeps adding in a for loop
    // Solution: Remove item at index then readd it.

    public void balanceListForRemoval(ArrayList<Integer> list) {

        Integer addOn = 0;

        for (int i = 0; i < list.size(); i++) {

            Integer curr = list.get(i);

            list.remove(i);

            list.add(i,curr+addOn);

            addOn++;
        }
    }


    public int cellsOutsideOnLeft(ArrayList<int[]> shape) {

        int outside = 0;

        for (int i = 0; i < shape.size(); i++) {

            int[] currCell = shape.get(i);

            int diff = tetrisBoard.boardLeftDifference(currCell[0]);

            if (diff > outside) {
                outside = diff;
            }
        }

        return outside;
    }

    public int cellsOutsideOnRight(ArrayList<int[]> shape) {

        int outside = 0;

        for (int i = 0; i < shape.size(); i++) {

            int[] currCell = shape.get(i);

            int diff = tetrisBoard.boardRightDifference(currCell[0]);

            if (diff > outside) {
                outside = diff;
            }
        }

        return outside;
    }

    public void instantDrop() {

        removeShape();

        while (!currShape.hasLanded()) {
            currShape.goOneDown();
            checkIfLanded();
        }

        reAddShape();

        newShape();
    }


    //long time = System.currentTimeMillis();

    public void rotateShape(Direction direction) {

        /*time = System.currentTimeMillis() - time;

        System.out.println(time);

        time = System.currentTimeMillis();*/



        //System.out.println("rotateShape");

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

            ArrayList<int[]> beforeRotation = cloneOf(currShape.get());
            if (direction == Direction.ClockWise) {
                currShape.rotate();
            } else {
                currShape.rotateBack();
            }

            ArrayList<int[]> beforeKick = cloneOf(currShape.get());

            kick(currShape.get(),rotationTable[0][0],rotationTable[0][1]);

            if (misplacedCells(currShape.get()) > 0) {

                currShape.set(cloneOf(beforeKick));

                for (int i = 1; i < rotationTable.length; i++) {

                    kick(currShape.get(),rotationTable[i][0],rotationTable[i][1]);

                    if (misplacedCells(currShape.get()) == 0) {
                        break;
                    } else {
                        if (i == rotationTable.length - 1) {
                            currShape.set(cloneOf(beforeRotation));
                            currShape.setRotation(false);
                        } else {
                            currShape.set(cloneOf(beforeKick));
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

    public ArrayList<int[]> cloneOf(ArrayList<int[]> shape) {

        ArrayList<int[]> clone = new ArrayList<>();

        for (int i = 0; i < shape.size(); i++) {

            int[] b = shape.get(i);

            int[] a = new int[b.length];

            a[0] = b[0];
            a[1] = b[1];

            clone.add(a);
        }

        return clone;
    }

    public void kick(ArrayList<int[]> shape, int x, int y) {

        for (int i = 0; i < shape.size(); i++) {

            int[] tmp = shape.get(i);

            tmp[0]+=x;
            tmp[1]+=-y;

            shape.set(i,tmp);
        }
    }

    public int misplacedCells(ArrayList<int[]> shape) {

        int misplacedCells = 0;

        for (int i = 0; i < shape.size(); i++) {

            int[] currentPiece = shape.get(i);

            int x = currentPiece[0], y = currentPiece[1];

            if (!tetrisBoard.isInBoard(x,y) || tetrisBoard.cellIsOn(x,y)) {
                misplacedCells++;
            }
        }

        return misplacedCells;
    }

    public boolean touchingBottomWall() {

        ArrayList<int[]> activeCells = getActiveCellsBottom();

        for (int[] a : activeCells) {

            if ( a[1] + 1 == tetrisBoard.getY() ) {
                return true;
            }
        }

        return false;
    }

    public boolean touchingAnotherShapeBottom() {

        // skal kun sjekke de cellene som er aktive

        ArrayList<int[]> activeCells = getActiveCellsBottom();

        for (int[] a : activeCells) {

            if (tetrisBoard.cellIsOn(a[0],a[1] + 1)) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<int[]> getActiveCellsTop() {

        ArrayList<int[]> active = new ArrayList<>();

        for (int[] a : currShape.get()) {

            int[] check = new int[]{a[0],a[1]-1};

            if (!contains(check)) {

                active.add(a);
            }
        }

        return active;
    }

    public ArrayList<int[]> getActiveCellsBottom() {

        ArrayList<int[]> active = new ArrayList<>();

        for (int[] a : currShape.get()) {

            int[] check = new int[]{a[0],a[1]+1};

            if (!contains(check)) {

                active.add(a);
            }
        }

        return active;
    }

    public boolean contains(int[] a) {

        for (int[] c : currShape.get()) {

            if (c[0] == a[0] && c[1] == a[1]) {
                return true;
            }
        }

        return false;
    }

    public boolean touchingAnotherShapeLeft() {

        ArrayList<int[]> activeCells = getActiveCellsLeft();

        for (int[] a : activeCells) {

            if (tetrisBoard.cellIsOn(a[0] - 1,a[1])) {

                return true;
            }
        }

        return false;
    }

    public ArrayList<int[]> getActiveCellsLeft() {

        ArrayList<int[]> active = new ArrayList<>();

        for (int[] a : currShape.get()) {

            int[] check = new int[]{a[0]-1,a[1]};

            if (!contains(check)) {
                active.add(a);
            }
        }

        return active;
    }

    public boolean touchingAnotherShapeRight() {

        ArrayList<int[]> activeCells = getActiveCellsRight();

        for (int[] a : activeCells) {

            if (tetrisBoard.cellIsOn(a[0] + 1,a[1])) {

                return true;
            }
        }

        return false;
    }

    public ArrayList<int[]> getActiveCellsRight() {

        ArrayList<int[]> active = new ArrayList<>();

        for (int[] a : currShape.get()) {

            int[] check = new int[]{a[0]+1,a[1]};

            if (!contains(check)) {
                active.add(a);
            }
        }

        return active;
    }

}
package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.util.Duration;
import org.omg.PortableInterceptor.DISCARDING;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable{

    @FXML Canvas canvas;
    Timeline timeline;
    KeyFrame keyFrame;
    TetrisBoard tetrisBoard;
    CurrentShape currShape;
    Stack<int[]> shapeReverseStack;
    public static Controller instance;
    private static int iteration;

    Timer timer;

    ShapePosition shapePosition;
    int i = 0;

    /*
    * if not touching bottom wall or other shape at bottom, keep going
    * if touching stop moving and start counting down for next shape
    * if a move is done while waiting, check if shape can continue now
    * if yes
    *
    *
    * */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        iteration = 0;

        instance = this;

        tetrisBoard = new TetrisBoard(canvas,20.0);
        shapePosition = new ShapePosition();

        tetrisBoard.assignColors();
        tetrisBoard.drawTetrisField();


        //test2();


        newShape();

        startAnimation(200);
    }

    public void test1() {
        tetrisBoard.cellOn(9,19);
        tetrisBoard.cellOn(9,18);
        tetrisBoard.cellOn(9,17);
        tetrisBoard.cellOn(9,16);
        tetrisBoard.cellOn(9,15);

        tetrisBoard.cellOn(8,19);
        tetrisBoard.cellOn(8,18);
        tetrisBoard.cellOn(8,17);
        tetrisBoard.cellOn(8,16);
        tetrisBoard.cellOn(8,15);
        tetrisBoard.cellOn(7,15);

        tetrisBoard.cellOn(6,19);
        tetrisBoard.cellOn(6,18);
        tetrisBoard.cellOn(6,17);

        tetrisBoard.cellOn(5,19);
        tetrisBoard.cellOn(5,18);

        tetrisBoard.cellOn(4,19);
        tetrisBoard.cellOn(4,18);
        tetrisBoard.cellOn(4,17);
    }

    public void test2() {
        tetrisBoard.cellOn(9,19);
        tetrisBoard.cellOn(9,18);
        tetrisBoard.cellOn(9,17);
        tetrisBoard.cellOn(9,16);

        tetrisBoard.cellOn(8,19);
        tetrisBoard.cellOn(7,19);

        tetrisBoard.cellOn(2,19);
        tetrisBoard.cellOn(2,18);
        tetrisBoard.cellOn(2,17);

        tetrisBoard.cellOn(3,18);
        tetrisBoard.cellOn(3,17);
        tetrisBoard.cellOn(4,17);

        tetrisBoard.cellOn(3,19);
    }

    public void newShape () {
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

        //Shape shape = new IShape();

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
                shape.getPosition(), shape, 300);

        currShape.addShape(shapeReverseStack);

        currShape.setColor(shape.getColorCode());
    }

    public void startAnimation(int speed) {

        keyFrame = new KeyFrame(Duration.millis(speed), e -> moveShape());

        timeline = new Timeline(keyFrame);

        timeline.setCycleCount(Animation.INDEFINITE);

        timeline.play();
    }

    public void moveShape() {
        //System.out.println("moveShape");

        /*i++;

        if (i == 19) {
            rotateShape(Direction.ClockWise);
        }*/

        if (!currShape.hasLanded()) {
            removeShape();
            currShape.goOneDown();
            reAddShape();

            checkIfLanded();
        } else {

            newPlacementWithDelay(3000);
        }

    }

    public void checkIfLanded() {

        if (touchingAnotherShapeBottom() || touchingBottomWall()) {
            currShape.setLanded(true);
        } else {
            currShape.setLanded(false);
        }
    }

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

    public void cancelNewPlacement() {

        //System.out.println("cancelNewPlacement");

        if (timer != null) {
            timer.cancel();
            timer = null;
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

        //System.out.println("checkIfShapeCanContinue");

        if (!touchingAnotherShapeBottom() && !touchingBottomWall()) {

            currShape.setLanded(false);

            cancelNewPlacement();

            timeline.play();

        }

    }

    public void goLeft() {

        if (!currShape.isFrozen()) {

            if (!touchingAnotherShapeLeft()) {
                removeShape();
                currShape.goOneLeft();
                reAddShape();
            }
        }

        checkIfLanded();

        checkIfShapeCanContinue();
    }

    public void goRight() {

        if (!currShape.isFrozen()) {

            if (!touchingAnotherShapeRight()) {
                removeShape();
                currShape.goOneRight();
                reAddShape();

            }
        }

        checkIfLanded();

        checkIfShapeCanContinue();
    }

    public void removeShape() {

        for (int i = 0; i < currShape.get().size(); i++) {
            int[] cell = currShape.get().get(i);
            tetrisBoard.cellOff(cell[0],cell[1]);
        }
    }

    public void reAddShape() {

        for (int i = 0; i < currShape.get().size(); i++) {
            int[] cell = currShape.get().get(i);
            tetrisBoard.cellOn(cell[0],cell[1],currShape.getColor());
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

    public int cellsOutsideOnBottom() {

        return 0;
    }


    public void rotateShape(Direction direction) {
        i++;

        if (i == 2) {

        }

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

                System.out.println("Miss: " + misplacedCells(currShape.get()));

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
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        iteration = 0;

        instance = this;

        tetrisBoard = new TetrisBoard(canvas,20.0);
        shapePosition = new ShapePosition();

        tetrisBoard.assignColors();
        tetrisBoard.drawTetrisField();


        newShape();

        startAnimation(220);
    }

    public void newShape () {
        shapePlacement(getRandomInt(7));
    }

    // Method that return a random integer
    public int getRandomInt(int limit) {

        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(limit);
    }

    // Random shapes are added to the tetris board PARAMETER: decimal value
    public void shapePlacement(int shapeCode) {

        //Shape shape = null;

        Shape shape = new IShape();

        /*switch (shapeCode) {
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
        }*/

        reverseShape(shape);

        currShape = new CurrentShape(tetrisBoard.getX(),tetrisBoard.getY(),shape.getOffset(),shape.getRotate(),shape.getMidPoint(),shape.getPosition());

        currShape.addShape(shapeReverseStack);

        currShape.setColor(shape.getColorCode());
    }


    public void startAnimation(int speed) {

        keyFrame = new KeyFrame(Duration.millis(speed), actionevent -> moveShape(900));

        timeline = new Timeline(keyFrame);

        timeline.setCycleCount(Animation.INDEFINITE);

        timeline.play();
    }

    public void moveShape(int shapeDelay) {

        // koden som utføres skal oppi her
        if (!currShape.hasLanded()) {

            if (!touchingAnotherShapeBottom()) {
                removeShape();
                currShape.goOneDown();
                reAddShape();
            } else {

                initiateNewPlacementWithDelay(shapeDelay);
            }

        } else {

            initiateNewPlacementWithDelay(shapeDelay);
        }

    }

    public void initiateNewPlacementWithDelay(int milliseconds) {

        timeline.pause();

        timer = new Timer();

        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {

                currShape.freeze();

                newShape();
                timeline.play();
                timer.cancel();
            }

        };

        timer.schedule(timerTask, milliseconds);
    }

    public void cancelNewPlacement() {

        if (timer != null) {
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

    /*public void rotateShape() {

        if (currShape.canRotate()) {

            if (true) {

                removeShape();
                currShape.rotate();
                ///////////////////////////////////// UNDER CONSTRUCTION //////////////////////////////////////////

                // Check if any shape is on the way

                // Check if shape is outside on left
                if (cellsOutsideOnLeft(currShape.get()) > 0) {

                    int outsideOnLeft = cellsOutsideOnLeft(currShape.get()); // get number of cells outside

                    currShape.rotateBack(); // roate back to original state

                    if (!touchingAnotherShapeRight()) {
                        reAddShape(); // add shape back to board

                        for (int j = 0; j < outsideOnLeft; j++) {
                            goRight(); // dont go right if there is something to the right
                        }

                        removeShape();
                        currShape.rotate();
                    }

                }

                // Check if shape is outside on right
                if (cellsOutsideOnRight(currShape.get()) > 0) {

                    int outsideOnRight = cellsOutsideOnRight(currShape.get());

                    currShape.rotateBack();

                    if (!touchingAnotherShapeLeft()) {
                        reAddShape();

                        for (int j = 0; j < outsideOnRight; j++) {
                            goLeft();
                        }

                        removeShape();
                        currShape.rotate();
                    }

                }

                // Check if shape is outside on bottom

                ///////////////////////////////////////////////////////////////////////////////////////////////////

                reAddShape();
            }

        }

        if (currShape.hasLanded()) {

            if (!touchingBottomWall()) {

                currShape.setLanded(false);

            }
        }


        checkIfShapeCanContinue();
    }*/

    public void rotateShape(Direction direction) {

        // nåværende posisjon lagres
        Position currentPosition = currShape.getPosition();

        Position nextPosition;

        // ønsket posisjon blir satt
        if (direction == Direction.ClockWise) {
            nextPosition = shapePosition.getNextRight(currentPosition);
        } else {
            nextPosition = shapePosition.getNextLeft(currentPosition);
        }



        // sett posisjon etter utførelse

        if (currShape.canRotate()) {

            removeShape();
            currShape.rotate();

            if (misplacedCells(currShape.get()) > 0) {
                System.out.println("MISPLACED");

                if (!shapeCanKick(currShape.get())) {
                    System.out.println("Rotating back...");
                    currShape.rotateBack();
                }

            }

            reAddShape();
        }

        checkIfShapeCanContinue();
    }

    public boolean shapeCanKick(ArrayList<int[]> shape) {


        return false;
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

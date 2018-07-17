package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class TetrisBoard {

    private final int x = 10;
    private final int y = 20;
    private double tileSize;
    private double xSpace;
    private double ySpace;
    private GraphicsContext graphicsContext;
    private int[][] board;
    public HashMap<Integer,Color> colorMap = new HashMap<Integer, Color>();

    public TetrisBoard(Canvas canvas, double tileSize) {
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.tileSize = tileSize;
        this.xSpace = 0;
        this.ySpace = 0;
        this.board = new int[x][y];
    }

    // Draw board using the integer board
    public void drawTetrisField() {

        for(int i = 0; i < y; i++) {

            for(int j = 0; j < x; j++) {

                changeColorCode(j,i);

                graphicsContext.fillRect(xSpace,ySpace,tileSize,tileSize);
                graphicsContext.strokeRect(xSpace,ySpace,tileSize,tileSize);

                xSpace+=tileSize;
            }
            xSpace=0;
            ySpace+=tileSize;
        }
        ySpace=0;
    }

    // Set color according to color code at x and y
    public void changeColorCode(int x, int y) {
        int colorCode = board[x][y];
        graphicsContext.setFill(colorMap.get(colorCode));
    }

    // Associate int values to colors by putting entries in HashMap
    public void assignColors() {

        colorMap.put(0,new Color(0.1,0.1,0.1,1.0));  // Default color (off)
        colorMap.put(1,new Color(1.0,0.0,0.0, 1.0));
        colorMap.put(2,new Color(0.0,1.0,0.0, 1.0));
        colorMap.put(3,new Color(0.2,0.4,0.95, 1.0));
        colorMap.put(4,new Color(1.0,0.55,0.0, 1.0));
        colorMap.put(5,new Color(0.3,0.8,0.9, 1.0));
        colorMap.put(6,new Color(1.0,0.9,0.1, 1.0));
        colorMap.put(7,new Color(0.5,0.0,0.8, 1.0));
    }

    // Turn on individual cell at x and y
    public void cellOn(int x, int y) {

        board[x][y] = 1;
        drawTetrisField();
    }

    // Turn on individual cell at x and y and specify color
    public void cellOn(int x, int y, int colorCode) {

        try {
            board[x][y] = colorCode;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            //System.out.println("Cannot turn cell on");
        }

        drawTetrisField();
    }

    // Turn off cell at x and y
    public void cellOff(int x, int y) {

        try {
            board[x][y] = 0;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            //System.out.println("Cannot turn cell off");
        }

        drawTetrisField();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean cellIsOn(int x, int y) {

        if (isInBoard(x,y)) {
            if (board[x][y] > 0) {
                return true;
            }
        }

        return false;
    }

    public boolean isInBoard(int x, int y) {

        if (x > -1 && x < 10 && y > -1 && y < 20) {
            return true;
        }

        return false;
    }

    public int boardLeftDifference(int x) {
        return 0 - x;
    }

    public int boardRightDifference(int x) {
        return x - 9;
    }

    public int boardBottomDifference(int y) {
        return 19 - x;
    }

    public void printTetrisBoard() {

        int counter = 0;
        System.out.println("\n");

        for (int i = 0; i < y; i++) {

            for (int j = 0; j < x; j++) {

                System.out.print("|" + board[j][i]);
                counter++;

                if (counter == 10) {
                    System.out.print("|");
                    System.out.println();
                    counter = 0;
                }

            }

        }

    }

    // Methods for turning on cells with style (i.e. shadow and gloss)

}

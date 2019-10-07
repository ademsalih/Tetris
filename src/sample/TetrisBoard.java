package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

public class TetrisBoard {

    private final int x = 10;
    private final int y = 22;
    private double tileSize;
    private double xSpace;
    private double ySpace;
    private GraphicsContext graphicsContext;
    private int[][] board;
    public HashMap<Integer,Color> colorMapLight = new HashMap<Integer, Color>();
    public HashMap<Integer,Color> colorMapMidTone = new HashMap<Integer, Color>();
    public HashMap<Integer,Color> colorMapDark = new HashMap<Integer, Color>();

    public TetrisBoard(Canvas canvas, double tileSize) {
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.tileSize = tileSize;
        this.xSpace = 0;
        this.ySpace = 0;
        this.board = new int[x][y];
    }

    public int[][] getBoard() {
        return this.board;
    }

    // Draw board using the integer board
    public void drawTetrisField() {



        for(int i = 0; i < y; i++) {

            for(int j = 0; j < x; j++) {

                int colorCode = getColorCode(j,i);

                graphicsContext.setFill(colorMapLight.get(colorCode));
                graphicsContext.fillRect(xSpace,ySpace,tileSize,tileSize);


                if (colorCode != 0) {
                    // dark
                    graphicsContext.setFill(colorMapDark.get(colorCode));

                    double[] x = {xSpace,xSpace+tileSize,xSpace+tileSize};
                    double[] y = {ySpace+tileSize,ySpace,ySpace+tileSize};

                    graphicsContext.fillPolygon(x,y,3);

                    //middle
                    graphicsContext.setFill(colorMapMidTone.get(colorCode));

                    double offset = tileSize*0.2;
                    double smallSquare = tileSize*0.6;

                    graphicsContext.fillRect(offset+xSpace,offset+ySpace,smallSquare,smallSquare);
                }


                graphicsContext.strokeRect(xSpace,ySpace,tileSize,tileSize);
                ///////////////////////////////////////////////////////////////////
                xSpace+=tileSize;
            }
            xSpace=0;
            ySpace+=tileSize;
        }
        ySpace=0;


        graphicsContext.setStroke(new Color(0.25,0.25,0.25,1.0));
        graphicsContext.strokeRect(0,40,200,440);
        graphicsContext.setStroke(Color.BLACK);
    }

    // Set color according to color code at x and y
    public int getColorCode(int x, int y) {
        return board[x][y];
    }

    public Color getColorFromCode(int code) {
        Color color = colorMapMidTone.get(code);
        return color;
    }

    public HashMap<Integer,Color> getColorMapLight() {
        return colorMapLight;
    }

    public HashMap<Integer,Color> getColorMapMidTone() {
        return colorMapMidTone;
    }

    public HashMap<Integer,Color> getColorMapDark() {
        return colorMapDark;
    }

    // Associate int values to colors by putting entries in HashMap
    public void assignColors() {

        // Normal colors
        colorMapLight.put(0,new Color(0.08,0.08,0.08,1.0));  // Default color (off)

        // Light colors
        colorMapLight.put(1,new Color(0.6,0.988,1.0, 1.0));
        colorMapLight.put(2,new Color(0.318,0.467,0.969, 1.0));
        colorMapLight.put(3,new Color(0.988,0.753,0.357, 1.0));
        colorMapLight.put(4,new Color(1.0,0.992,0.22, 1.0));
        colorMapLight.put(5,new Color(0.506,1.0,0.529, 1.0));
        colorMapLight.put(6,new Color(0.902,0.396,0.949, 1.0));
        colorMapLight.put(7,new Color(0.91,0.427,0.427, 1.0));

        // Mid Tone Colors
        colorMapMidTone.put(1,new Color(0.082,0.969,1.0, 1.0));
        colorMapMidTone.put(2,new Color(0.106,0.302,0.957, 1.0));
        colorMapMidTone.put(3,new Color(0.953,0.62,0.055, 1.0));
        colorMapMidTone.put(4,new Color(0.922,0.914,0.082, 1.0));
        colorMapMidTone.put(5,new Color(0.106,0.957,0.145, 1.0));
        colorMapMidTone.put(6,new Color(0.769,0.118,0.831, 1.0));
        colorMapMidTone.put(7,new Color(0.851,0.149,0.149, 1.0));

        // Dark Colors
        colorMapDark.put(1,new Color(0.043,0.78,0.804, 1.0));
        colorMapDark.put(2,new Color(0.02,0.157,0.62, 1.0));
        colorMapDark.put(3,new Color(0.718,0.467,0.039, 1.0));
        colorMapDark.put(4,new Color(0.718,0.71,0.02, 1.0));
        colorMapDark.put(5,new Color(0.055,0.729,0.086, 1.0));
        colorMapDark.put(6,new Color(0.431,0.02,0.471, 1.0));
        colorMapDark.put(7,new Color(0.62,0.09,0.09, 1.0));
    }

    public void clearBoard() {
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                cellOff(j,i);
            }
        }
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

    public void cellOnNoUpdateBoard(int x, int y, int colorCode) {

        try {
            board[x][y] = colorCode;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            //System.out.println("Cannot turn cell on");
        }

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

    public void cellOffNoUpdateBoard(int x, int y) {

        try {
            board[x][y] = 0;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            //System.out.println("Cannot turn cell off");
        }

    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    /////////////////////////////////////////////////////////////
    public boolean cellIsOn(int x, int y) {

        if (isInBoard(x,y)) {
            if (board[x][y] > 0) {
                return true;
            }
        }

        return false;
    }
    //////////////////////////////////////////////////////////////


    public boolean isInBoard(int x, int y) {

        if (x > -1 && x < this.x && y > -1 && y < this.y) {
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
        return 21 - x;
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

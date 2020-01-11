package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TetrisBoard {

    private int x = 10;
    private int y = 20;
    private final int blinkSpeed = 48;
    private final int blinks = 3;
    private double tileSize;
    private double xSpace;
    private double ySpace;
    private GraphicsContext graphicsContext;
    private int[][] board;
    public HashMap<Integer,Color> colorMapLight = new HashMap<Integer, Color>();
    public HashMap<Integer,Color> colorMapMidTone = new HashMap<Integer, Color>();
    public HashMap<Integer,Color> colorMapDark = new HashMap<Integer, Color>();
    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> t;

    public TetrisBoard(Canvas canvas, double tileSize) {
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.tileSize = tileSize;
        this.xSpace = 0;
        this.ySpace = 2;
        this.y = this.y + 2;
        this.board = new int[y][x];
    }

    public int[][] getBoard() {
        return this.board;
    }

    public void draw() {
        for(int i = 2; i < y; i++) {

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
        ySpace=2;


        graphicsContext.setStroke(new Color(0.25,0.25,0.25,1.0));
        graphicsContext.strokeRect(0,0,200,400);
        graphicsContext.setStroke(Color.BLACK);
    }

    // Set color according to color code at x and y
    public int getColorCode(int x, int y) {
        return board[y][x];
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
                cellOff(j,i,true);
            }
        }
    }

    public void cellOn(int x, int y, int colorCode, boolean update) {
        try {
            board[y][x] = colorCode;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            System.out.println("Cannot turn cell on");
        }
        if (update) draw();
    }

    public void cellOff(int x, int y, boolean update) {
        try {
            board[y][x] = 0;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            //System.out.println("Cannot turn cell off");
        }
        if (update) draw();
    }


    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean cellIsOn(int x, int y) {
        return isInBoard(x,y) ? board[y][x] > 0 : false;
    }

    public boolean isInBoard(int x, int y) {
        return -1 < x && x < this.x && -1 < y && y < this.y;
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

    public int blinkDuration() {
        return blinkSpeed * ((blinks * 2)+1) + 35;
    }


    public  void blinkRemove(ArrayList<Integer> rows) {
        ArrayList<int[]> beforeState = new ArrayList<>();
        for (Integer i : rows) beforeState.add(board[i].clone());

        t = ses.scheduleWithFixedDelay(new Runnable() {
            private int i = 0;
            @Override
            public void run() {
                for (int y = 0; y < rows.size(); y++) {
                    for (int x = 0; x < getX(); x++) {
                        if (i % 2 == 0) {
                            cellOff(x,rows.get(y),false);
                        } else {
                            cellOn(x,rows.get(y), beforeState.get(y)[x], false);
                        }
                    }
                }
                draw();
                if (++i > blinks*2) {
                    t.cancel(false);
                    balanceListForRemoval(rows);

                    for (int y = 0; y < rows.size(); y++) {
                        int dLine = rows.get(y);
                        int sLine = dLine - 1;

                        for (int i = dLine; i > 0; i--) {
                            for (int j = 0; j < getX(); j++) {
                                board[dLine][j] = board[sLine][j];
                            }
                            dLine--;sLine--;
                        }
                    }
                    draw();
                }
            }
        }, 0, blinkSpeed, TimeUnit.MILLISECONDS);
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

}

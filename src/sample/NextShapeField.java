package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class NextShapeField {

    private int[][] shape;
    private double height;
    private double width;
    private GraphicsContext gc;

    private HashMap<Integer,Color> colorMapLight = new HashMap<>();
    private HashMap<Integer,Color> colorMapMidTone = new HashMap<>();
    private HashMap<Integer,Color> colorMapDark = new HashMap<>();

    private double tileSize;
    private double xSpace;
    private double ySpace;

    private double startX;
    private double startY;

    public NextShapeField(Canvas canvas, int tileSize) {
        this.gc = canvas.getGraphicsContext2D();
        this.height = canvas.getHeight();
        this.width = canvas.getWidth();
        this.tileSize = tileSize;
    }

    private double calculateXStart() {
        return (width/2) - ((tileSize * shape[0].length)/2);
    }

    private double calculateYStart() {
        return (height/2) - ((tileSize * shape.length)/2);
    }

    public void assignColor(HashMap<Integer,Color> light, HashMap<Integer,Color> midTone, HashMap<Integer,Color> dark) {
        this.colorMapLight = light;
        this.colorMapMidTone = midTone;
        this.colorMapDark = dark;
    }

    public void setShape(int[][] shape, int colorCode) {
        this.shape = shape;
        this.startX = calculateXStart();
        this.startY = calculateYStart();

        xSpace = startX;
        ySpace = startY;

        emptyField();
        drawField(colorCode);
    }

    private void drawField(int colorCode) {

        for(int i = 0; i < shape.length; i++) {

            for(int j = 0; j < shape[0].length; j++) {

                if (shape[i][j] > 0) {
                    // Light
                    gc.setFill(colorMapLight.get(colorCode));
                    gc.fillRect(xSpace,ySpace,tileSize,tileSize);

                    // Dark
                    gc.setFill(colorMapDark.get(colorCode));

                    double[] x = {xSpace,xSpace+tileSize,xSpace+tileSize};
                    double[] y = {ySpace+tileSize,ySpace,ySpace+tileSize};

                    gc.fillPolygon(x,y,3);

                    // Middle
                    gc.setFill(colorMapMidTone.get(colorCode));

                    double offset = tileSize*0.2;
                    double smallSquare = tileSize*0.6;

                    gc.fillRect(offset+xSpace,offset+ySpace,smallSquare,smallSquare);

                    gc.strokeRect(xSpace,ySpace,tileSize,tileSize);
                }

                xSpace += tileSize;
            }
            xSpace = startX;
            ySpace += tileSize;
        }
        ySpace = startY;

        gc.setStroke(new Color(0.25,0.25,0.25,1.0));
        gc.strokeRect(0,0,90,90);
        gc.setStroke(Color.BLACK);
    }

    public void emptyField() {
        gc.setFill(colorMapLight.get(0));
        gc.fillRect(0,0,width,height);

        gc.setStroke(new Color(0.25,0.25,0.25,1.0));
        gc.strokeRect(0,0,90,90);
        gc.setStroke(Color.BLACK);
    }

}

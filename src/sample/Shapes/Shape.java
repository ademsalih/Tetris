package sample.Shapes;

import sample.Position;

public abstract class Shape {

    private int rowCount = 0;
    private Position position;
    private int offset;
    private int colorCode;
    private boolean rotate;
    private int midPoint;
    private int[][] shape;

    public Shape() {
        this.rowCount = 0;
    }

    public int[] getRow() {
        return rowCount < getY() ? shape[rowCount++] : null;
    }

    public int getX() {
        return shape[0].length;
    }

    public int getY() {
        return shape.length;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getColorCode() {
        return colorCode;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }

    public boolean isRotate() {
        return rotate;
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    public boolean getRotate() {
        return this.rotate;
    }

    public int getMidPoint() {
        return midPoint;
    }

    public void setMidPoint(int midPoint) {
        this.midPoint = midPoint;
    }

    public int[][] getShape() {
        return shape;
    }

    public void setShape(int[][] shape) {
        this.shape = shape;
    }
}

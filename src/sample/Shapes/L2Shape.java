package sample.Shapes;

import sample.Position;

public class L2Shape implements Shape {

    private int rowCount;
    private Position position = Position.START;
    private final int offset = 3;
    private final int colorCode = 3;
    private final boolean rotate = true;
    private final int midPoint = 1;

    private final int[][] shape = {
            {0,0,2},
            {2,2,2}
    };

    public L2Shape() {
        this.rowCount = 0;
    }

    @Override
    public int[] getRow() {
        return rowCount < getY() ? shape[rowCount++]:null;
    }

    @Override
    public int getX() {
        return shape[0].length;
    }

    @Override
    public int getY() {
        return shape.length;
    }

    @Override
    public int getOffset() {
        return this.offset;
    }

    @Override
    public int getColorCode() {
        return colorCode;
    }

    @Override
    public boolean getRotate() {
        return this.rotate;
    }

    @Override
    public int getMidPoint() {
        return this.midPoint;
    }

    @Override
    public Position getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(Position pos) {
        this.position = pos;
    }

    @Override
    public int[][] getShape() {
        return shape;
    }

}

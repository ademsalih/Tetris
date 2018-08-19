package sample;

public class BlockShape implements Shape {

    private int rowCount;
    private Position position = Position.START;
    private final int offset = 4;
    private final int colorCode = 4;
    private final boolean rotate = false;
    private final int midPoint = 0;

    private final int[][] shape = {
            {2,2},
            {2,2}
    };

    public BlockShape() {
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

}

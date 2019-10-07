package sample.Shapes;

import sample.Position;

public interface Shape {

    public int[] getRow();

    public int getX();

    public int getY();

    public int getOffset();

    public int getColorCode();

    public boolean getRotate();

    public int getMidPoint();

    public Position getPosition();

    public void setPosition(Position pos);

    public int[][] getShape();

}

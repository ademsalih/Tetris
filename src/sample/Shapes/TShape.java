package sample.Shapes;

import sample.Position;

public class TShape extends Shape {

    private Position position = Position.START;
    private int offset = 3;
    private int colorCode = 6;
    private boolean rotate = true;
    private int midPoint = 2;
    private int[][] shape = {
            {0,2,0},
            {2,2,2}
    };

    public TShape() {
        super.setPosition(position);
        super.setOffset(offset);
        super.setColorCode(colorCode);
        super.setRotate(rotate);
        super.setMidPoint(midPoint);
        super.setShape(shape);
    }

}

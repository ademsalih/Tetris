package sample.Shapes;

import sample.Position;

public class S1Shape extends Shape {

    private Position position = Position.START;
    private int offset = 3;
    private int colorCode = 5;
    private boolean rotate = true;
    private int midPoint = 3;
    private int[][] shape = {
            {0,2,2},
            {2,2,0}
    };

    public S1Shape() {
        super.setPosition(position);
        super.setOffset(offset);
        super.setColorCode(colorCode);
        super.setRotate(rotate);
        super.setMidPoint(midPoint);
        super.setShape(shape);
    }


}

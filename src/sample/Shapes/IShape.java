package sample.Shapes;

import sample.Position;

public class IShape extends Shape {

    private Position position = Position.START;
    private int offset = 3;
    private int colorCode = 1;
    private boolean rotate = true;
    private int midPoint = 1;
    private int[][] shape = {
            {2,2,2,2}
    };

    public IShape() {
        super.setPosition(position);
        super.setOffset(offset);
        super.setColorCode(colorCode);
        super.setRotate(rotate);
        super.setMidPoint(midPoint);
        super.setShape(shape);
    }


}

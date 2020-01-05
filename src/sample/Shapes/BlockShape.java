package sample.Shapes;

import sample.Position;

public class BlockShape extends Shape {

    private Position position = Position.START;
    private int offset = 4;
    private int colorCode = 4;
    private boolean rotate = false;
    private int midPoint = 0;
    private int[][] shape = {
            {2,2},
            {2,2}
    };

    public BlockShape() {
        super.setPosition(position);
        super.setOffset(offset);
        super.setColorCode(colorCode);
        super.setRotate(rotate);
        super.setMidPoint(midPoint);
        super.setShape(shape);
    }

}

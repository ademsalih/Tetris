package sample;

public class ShapePosition {

    public ShapePosition () {

    }

    public Position getNextRight(Position currentPosition) {

        Position nextPosition = null;

        switch (currentPosition) {
            case START: nextPosition = Position.RIGHT;
            break;

            case RIGHT: nextPosition = Position.MID;
            break;

            case MID: nextPosition = Position.LEFT;
            break;

            case LEFT: nextPosition = Position.START;
            break;
        }

        return nextPosition;
    }

    public Position getNextLeft(Position currentPosition) {

        Position nextPosition = null;

        switch (currentPosition) {
            case START: nextPosition = Position.LEFT;
                break;

            case RIGHT: nextPosition = Position.START;
                break;

            case MID: nextPosition = Position.RIGHT;
                break;

            case LEFT: nextPosition = Position.MID;
                break;
        }

        return nextPosition;
    }
}

package sample;

import java.util.HashMap;

public class ShapePosition {

    private HashMap<Position,int[][]> restShapeMap;
    private HashMap<Position,int[][]> iShapeMap;

    public ShapePosition () {
        restShapeMap = new HashMap<>();
        iShapeMap = new HashMap<>();
        initiate();
    }

    public int[][] getIShapeMap(Position position){
        return iShapeMap.get(position);
    }

    public int[][] getRestShapeMap(Position position){
        return restShapeMap.get(position);
    }

    private void initiate() {
        restShapeMap.put(Position.START,new int[][]{{0,0},{0,0},{0,0},{0,0},{0,0}});
        restShapeMap.put(Position.RIGHT,new int[][]{{0,0},{1,0},{1,-1},{0,2},{1,2}});
        restShapeMap.put(Position.MID,new int[][]{{0,0},{0,0},{0,0},{0,0},{0,0}});
        restShapeMap.put(Position.LEFT,new int[][]{{0,0},{-1,0},{-1,-1},{0,2},{-1,2}});

        iShapeMap.put(Position.START,new int[][]{{0,0},{-1,0},{2,0},{-1,0},{2,0}});
        iShapeMap.put(Position.RIGHT,new int[][]{{-1,0},{0,0},{0,0},{0,1},{0,-2}});
        iShapeMap.put(Position.MID,new int[][]{{-1,1},{1,1},{-2,1},{1,0},{-2,0}});
        iShapeMap.put(Position.LEFT,new int[][]{{0,1},{0,1},{0,1},{0,-1},{-0,2}});
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

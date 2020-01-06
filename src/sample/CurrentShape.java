package sample;

import sample.Shapes.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class CurrentShape {

    private int offset;
    private int level;
    private ArrayList<int[]> list;
    private Stack<int[]> shapeStack;
    private int rowsNotAdded;
    private int rowsToAdd;
    private int rows;
    private int columns;
    private boolean landed;
    private boolean frozen;
    private int x;
    private int y;
    private int color;
    private boolean rotation;
    private int midtpointInteger;
    private boolean onBoard;
    private Position position;
    private Shape type;


    public CurrentShape(int x, int y,int offset, boolean rotate, int midtpointInteger, Position position, Shape shapeType) {
        this.x = x - 1;
        this.y = y - 1;
        this.offset = offset;
        this.level = 0;
        this.list = new ArrayList<>();
        this.landed = false;
        this.frozen = false;
        this.rotation = rotate;
        this.midtpointInteger = midtpointInteger;
        this.position = position;
        this.type = shapeType;
        this.onBoard = false;
    }

    /**
     * Find the cells of the shape to check if shape moves upwards.
     * */
    public ArrayList<int[]> activeCellsTop() {
        ArrayList<int[]> active = new ArrayList<>();

        for (int[] a : list) {
            int[] check = new int[]{a[0],a[1]-1};
            if (!contains(check)) {
                active.add(a);
            }
        }

        return active;
    }

    /**
     * Finds the cells of the shape that needs to be checked in
     * touchingShapeBottom().
     * */
    public ArrayList<int[]> activeCellsBottom() {
        ArrayList<int[]> active = new ArrayList<>();
        for (int[] a : list) {
            int[] check = new int[]{a[0],a[1]+1};
            if (!contains(check)) {
                active.add(a);
            }
        }

        return active;
    }

    /**
     * Finds the cells of the shape that needs to be checked in
     * touchingShapeLeft().
     * */
    public ArrayList<int[]> activeCellsLeft() {
        ArrayList<int[]> active = new ArrayList<>();
        for (int[] a : list) {
            int[] check = new int[]{a[0]-1,a[1]};
            if (!contains(check)) {
                active.add(a);
            }
        }

        return active;
    }

    /**
     * Finds the cells of the shape that needs to be checked in
     * touchingShapeRight().
     * */
    public ArrayList<int[]> activeCellsRight() {

        ArrayList<int[]> active = new ArrayList<>();

        for (int[] a : list) {

            int[] check = new int[]{a[0]+1,a[1]};

            if (!contains(check)) {
                active.add(a);
            }
        }

        return active;
    }

    /**
     * Method that checks if ArrayList of currShape contains a pair {x,y}.
     * @param array
     * */
    public boolean contains(int[] a) {
        return list.stream().filter(i -> Arrays.equals(i,a)).findAny().isPresent();
    }

    public void offsetPlusOne() {
        offset+=1;
    }

    public void offsetMinusOne() {
        offset-=1;
    }

    public void addShape(Shape shape) {

        Stack<int[]> tmp = new Stack<>();
        Stack<int[]> tmp2 = new Stack<>();

        for (int i = 0; i < shape.getShape().length; i++) {
            tmp.add(shape.getShape()[i]);
        }

        while (!tmp.empty()) {
            tmp2.push(tmp.pop());
        }

        this.shapeStack = tmp2;
        rowsNotAdded = 0;
        rowsToAdd = rowsNotAdded;
        rows = shapeStack.size();
        columns = shapeStack.get(0).length;
    }

    public void setLanded(boolean status) {
        this.landed = status;
    }

    public void goOneDown() {
        if (!this.onBoard) {
            while (!shapeStack.empty()) {
                if (shapeStack.size() == 1) level = 1;
                int[] row = shapeStack.pop();
                for (int i = 0; i < row.length; i++) {
                    if (row[i] > 0) {
                        int[] cell = {offset+i,level};
                        list.add(cell);
                    }
                }
                level++;
            }
            this.onBoard = true;
        } else {
            for (int[] a : list) {
                if (a[1] + 1 == y) {
                    landed = true;
                }
                a[1] += 1;
            }
        }
    }

    public boolean hasLanded() {
        return landed;
    }

    public boolean hasEnteredBoard() {

        if ((rowsToAdd - rowsNotAdded) > 0) {
            return true;
        }

        return true;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void freeze() {
        frozen = true;
    }

    public void goOneLeft() {

        if (!isTouchingLeftWall()) {
            if (!hasLanded()) {
                offset--;
            }

            for (int[] a : list) {
                a[0] -= 1;
            }
        }
    }

    public boolean isTouchingLeftWall() {

        for (int[] a : list) {

            if (a[0] < 1) {
                return true;
            }
        }

        return false;
    }

    public void goOneRight() {

        if (!isTouchingRightWall()) {

            if (!hasLanded()) {
                offset++;
            }

            for (int[] a : list) {
                a[0] += 1;
            }
        }
    }

    public boolean isTouchingRightWall() {
        for (int[] a : list) {
            if (a[0] + 1 > x) {
                return true;
            }
        }

        return false;
    }

    public void rotate() {

        // TODO Do not allow rotation when close to edge

        if (!(rowsNotAdded > 0)) {

            int[] m = list.get(midtpointInteger); // midpoint set

            for (int i = 0; i < list.size(); i++) {

                int[] curr = list.get(i); // current set

                int[] cmp = {curr[0]-m[0],curr[1]-m[1]}; // comparison set

                if (cmp[0] == -1 && cmp[1] == -1) {

                    curr[0] +=2;

                } else if (cmp[0] == -1 && cmp[1] == 0) {

                    curr[0] +=1;
                    curr[1] -=1;

                } else if (cmp[0] == 0 && cmp[1] == -2) {

                    curr[0] +=2;
                    curr[1] +=2;

                } else if (cmp[0] == 0 && cmp[1] == -1) {

                    curr[0] +=1;
                    curr[1] +=1;

                } else if (cmp[0] == 0 && cmp[1] == 0) {

                    continue;

                } else if (cmp[0] == 0 && cmp[1] == 1) {

                    curr[0] -=1;
                    curr[1] -=1;

                } else if (cmp[0] == 1 && cmp[1] == -1) {

                    curr[1] +=2;

                } else if (cmp[0] == 1 && cmp[1] == 0) {

                    curr[0] -= 1;
                    curr[1] += 1;
                }

                else if (cmp[0] == -2 && cmp[1] == 0) {

                    curr[0] += 2;
                    curr[1] -= 2;

                } else if (cmp[0] == 2 && cmp[1] == 0) {

                    curr[0] -= 2;
                    curr[1] += 2;

                } else if (cmp[0] == -1 && cmp[1] == 1) {

                    curr[1] -= 2;

                } else if (cmp[0] == 1 && cmp[1] == 1) {

                    curr[0] -= 2;

                } else if (cmp[0] == 0 && cmp[1] == 2) {

                    curr[0] -= 2;
                    curr[1] -= 2;
                }

                list.set(i,curr);
            }
        }

    }

    public void rotateBack() {

        // TODO Do not allow rotation when close to edge

        // TODO Do not allow rotation before the whole shapeStack is on the board

        if (!(rowsNotAdded > 0)) {

            int[] m = list.get(midtpointInteger); // midpoint set

            for (int i = 0; i < list.size(); i++) {

                int[] curr = list.get(i); // current set

                int[] cmp = {curr[0]-m[0],curr[1]-m[1]}; // comparison set

                if (cmp[0] == -1 && cmp[1] == -1) {

                    curr[1] +=2;

                } else if (cmp[0] == -1 && cmp[1] == 0) {

                    curr[0] +=1;
                    curr[1] +=1;

                } else if (cmp[0] == 0 && cmp[1] == -2) {

                    curr[0] -=2;
                    curr[1] +=2;

                } else if (cmp[0] == 0 && cmp[1] == -1) {

                    curr[0] -=1;
                    curr[1] +=1;

                } else if (cmp[0] == 0 && cmp[1] == 0) {

                    continue;

                } else if (cmp[0] == 0 && cmp[1] == 1) {

                    curr[0] +=1;
                    curr[1] -=1;

                } else if (cmp[0] == 1 && cmp[1] == -1) {

                    curr[0] -=2;

                } else if (cmp[0] == 1 && cmp[1] == 0) {

                    curr[0] -= 1;
                    curr[1] -= 1;
                }

                else if (cmp[0] == -2 && cmp[1] == 0) {

                    curr[0] += 2;
                    curr[1] += 2;

                } else if (cmp[0] == 2 && cmp[1] == 0) {

                    curr[0] -= 2;
                    curr[1] -= 2;

                } else if (cmp[0] == -1 && cmp[1] == 1) {

                    curr[0] += 2;

                } else if (cmp[0] == 1 && cmp[1] == 1) {

                    curr[1] -= 2;

                } else if (cmp[0] == 0 && cmp[1] == 2) {

                    curr[0] += 2;
                    curr[1] -= 2;
                }

                list.set(i,curr);
            }
        }

    }

    public boolean canRotate() {
        return this.rotation;
    }

    public ArrayList<int[]> get() {
        return list;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Shape getType() {
        return this.type;
    }

    public void set(ArrayList<int[]> shape) {
        this.list = shape;
    }

    public void setRotation(boolean val) {
        this.rotation = val;
    }

}
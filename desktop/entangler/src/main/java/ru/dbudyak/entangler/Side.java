package ru.dbudyak.entangler;

/**
 * Created by dbudyak on 08.06.14.
 */
public class Side {
    private boolean isConnected = false;
    private Direction direction = Direction.NONE;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public static enum Direction {
        INPUT, OUTPUT, NONE
    }
}

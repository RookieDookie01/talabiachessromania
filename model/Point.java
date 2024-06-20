package model;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Point implements Piece {
    private int xCoordinate;
    private int yCoordinate;
    private String color;
    private ImageIcon image;
    private BoardModel model;
    private boolean moveForward;
    private boolean reachedEdge;

    public void loadImage() {
        String imagePath = "/img/" + color.charAt(0) + "Point.png";
        this.image = new ImageIcon(getClass().getResource(imagePath));
    }

    public Point(BoardModel model, int xCoordinate, int yCoordinate, String color) {
        this.model = model;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.moveForward = true;
        this.reachedEdge = false;
        this.color = color.toLowerCase();
        loadImage();
    }

    private boolean isDestinationEmpty(int x, int y) {
        return x >= 0 && x < 7 && y >= 0 && y < 6 && model.getPieceAtPosition(x, y) == null;
    }

    private void updateMovementDirection() {
        boolean atTopOrBottomEdge = getYCoordinate() == 0 || getYCoordinate() == 5;

        if (atTopOrBottomEdge) {
            if (!reachedEdge) {
                moveForward = false;
                reachedEdge = true;
            } else {
                moveForward = !atTopOrBottomEdge;
            }
        } else {
            reachedEdge = false;
        }
    }

    private void moveBackwardToEdge() {
        setYCoordinate(5 - getYCoordinate());
        setXCoordinate(6 - getXCoordinate());
        moveForward = false;
        reachedEdge = true;
    }

    private int getMovementDirection() {
        return getColor().equals("blue") ? (moveForward ? 1 : -1) : (moveForward ? -1 : 1);
    }

    private boolean isAtTopOrBottomEdge() {
        return getYCoordinate() == 0 || getYCoordinate() == 5;
    }

    public void setMoveBackward(boolean moveBackward) {
        this.moveForward = !moveBackward;
        this.reachedEdge = moveBackward;
    }

    @Override
    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    @Override
    public int getXCoordinate() {
        return xCoordinate;
    }

    @Override
    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    @Override
    public int getYCoordinate() {
        return yCoordinate;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public ImageIcon getImage() {
        return image;
    }

    @Override
    public void move(int x, int y) {
        if (isValidMove(x, y, null, 0)) {
            int direction = getMovementDirection();

            if (isAtTopOrBottomEdge() && !reachedEdge && y != getYCoordinate()) {
                int turnedY1 = getYCoordinate() + direction;
                int turnedY2 = getYCoordinate() + 2 * direction;

                if (y == turnedY1 || y == turnedY2) {
                    setYCoordinate(y);
                    loadImage();
                    updateMovementDirection();

                    if (isAtTopOrBottomEdge()) {
                        reachedEdge = true;
                        moveBackwardToEdge();
                        moveForward = false;
                    } else {
                        moveForward = true;
                    }

                    return;
                }
            }

            if (isDestinationEmpty(x, y)) {
                setXCoordinate(x);
                setYCoordinate(y);
                loadImage();
                updateMovementDirection();
                moveForward = !reachedEdge;

                if (isAtTopOrBottomEdge() && !reachedEdge) {
                    reachedEdge = true;
                    moveBackwardToEdge();
                    moveForward = false;
                }
            } else {
                capture();
            }
        }
    }

    @Override
    public void capture() {
        List<int[]> capturePath = getValidMovePositions();

        for (int[] position : capturePath) {
            int x = position[0];
            int y = position[1];

            Piece capturedPiece = model.getPieceAtPosition(x, y);

            if (capturedPiece != null && !capturedPiece.getColor().equals(getColor())) {
                model.removePiece(capturedPiece);

                setXCoordinate(x);
                setYCoordinate(y);
                loadImage();
                break;
            }
        }
    }

    @Override
    public void flipCoordinates(int squareSize, boolean isFlipped) {
        if (isFlipped) {
            setXCoordinate(6 - getXCoordinate());
            setYCoordinate(5 - getYCoordinate());

            moveForward = false;

            if (isAtTopOrBottomEdge()) {
                setYCoordinate(5 - getYCoordinate());
                moveForward = false;

                setXCoordinate(Math.max(0, Math.min(getXCoordinate(), 6)));
                setYCoordinate(Math.max(0, Math.min(getYCoordinate(), 5)));
            }
        } else {
            setXCoordinate(getXCoordinate());
            setYCoordinate(getYCoordinate());

            moveForward = true;
        }

        setXCoordinate(Math.max(0, Math.min(getXCoordinate(), 6)));
        setYCoordinate(Math.max(0, Math.min(getYCoordinate(), 5)));
    }

    @Override
    public List<int[]> getValidMovePositions() {
        List<int[]> validMoves = new ArrayList<>();

        int currentX = getXCoordinate();
        int currentY = getYCoordinate();
        int direction = getMovementDirection();

        int forward1Y = currentY + direction;
        if (isValidMove(currentX, forward1Y, null, 0)) {
            validMoves.add(new int[] { currentX, forward1Y });
        }

        if (!isAtTopOrBottomEdge()) {
            int forward2Y = currentY + 2 * direction;
            if (isValidMove(currentX, forward2Y, null, 0)) {
                validMoves.add(new int[] { currentX, forward2Y });
            }
        } else {
            int backward1Y = currentY - direction;
            if (isValidMove(currentX, backward1Y, null, 0)) {
                validMoves.add(new int[] { currentX, backward1Y });
            }

            int backward2Y = currentY - 2 * direction;
            if (isValidMove(currentX, backward2Y, null, 0)) {
                validMoves.add(new int[] { currentX, backward2Y });
            }
        }

        return validMoves;
    }

    @Override
    public boolean isValidMove(int x, int y, String currentPlayer, int movementCount) {
        if (x != getXCoordinate()) {
            return false;
        }

        int minY = Math.min(y, getYCoordinate());
        int maxY = Math.max(y, getYCoordinate());

        Piece destinationPiece = model.getPieceAtPosition(x, y);

        if (destinationPiece == null) {
            for (int i = minY + 1; i < maxY; i++) {
                if (!isDestinationEmpty(x, i)) {
                    return false;
                }
            }
            return true;
        }

        return !destinationPiece.getColor().equals(getColor());
    }

    @Override
    public boolean isValidCapture(int x, int y) {
        int currentX = getXCoordinate();
        int currentY = getYCoordinate();

        if ((getColor().equals("blue") && y == currentY + 1 && Math.abs(x - currentX) == 1)
                || (getColor().equals("yellow") && y == currentY - 1 && Math.abs(x - currentX) == 1)) {
            return true;
        }

        return false;
    }
}

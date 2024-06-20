// Time.java
package model;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Time implements Piece {
    private int xCoordinate;
    private int yCoordinate;
    private String color;
    private ImageIcon image;
    private BoardModel model;

    public void loadImage() {
        String imagePath = "/img/" + color.charAt(0) + "Time.png";
        this.image = new ImageIcon(getClass().getResource(imagePath));
    }

    public Time(BoardModel model, int xCoordinate, int yCoordinate, String color) {
        this.model = model;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.color = color.toLowerCase();
        loadImage();
    }

    private boolean isDestinationEmpty(int x, int y) {
        boolean withinBoardLimits = x >= 0 && x < 7 && y >= 0 && y < 6;

        if (withinBoardLimits) {
            Piece destinationPiece = model.getPieceAtPosition(x, y);
            return destinationPiece == null;
        }

        return false;
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
            if (isDestinationEmpty(x, y)) {
                model.setPieceAtPosition(null, getXCoordinate(), getYCoordinate());

                setXCoordinate(x);
                setYCoordinate(y);

                model.setPieceAtPosition(this, x, y);
                loadImage();
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
        } else {
            setXCoordinate(getXCoordinate());
            setYCoordinate(getYCoordinate());
        }
    }

    @Override
    public List<int[]> getValidMovePositions() {
        List<int[]> validMoves = new ArrayList<>();

        int currentX = getXCoordinate();
        int currentY = getYCoordinate();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                if (Math.abs(currentX - i) == Math.abs(currentY - j) && isValidMove(i, j, null, 0)) {
                    validMoves.add(new int[] { i, j });
                }
            }
        }

        return validMoves;
    }

    @Override
    public boolean isValidMove(int x, int y, String currentPlayer, int movementCount) {
        boolean withinBoardLimits = x >= 0 && x < 7 && y >= 0 && y < 6;

        if (!withinBoardLimits) {
            return false;
        }

        Piece destinationPiece = model.getPieceAtPosition(x, y);

        if (Math.abs(x - getXCoordinate()) != Math.abs(y - getYCoordinate())) {
            return false;
        }

        int deltaX = Math.abs(x - getXCoordinate());
        int deltaY = Math.abs(y - getYCoordinate());

        if (deltaX != deltaY) {
            return false;
        }

        int minX = Math.min(x, getXCoordinate());
        int minY = Math.min(y, getYCoordinate());
        int maxX = Math.max(x, getXCoordinate());
        int maxY = Math.max(y, getYCoordinate());

        for (int i = 1; i < deltaX; i++) {
            int checkX = minX < maxX ? minX + i : minX - i;
            int checkY = minY < maxY ? minY + i : minY - i;

            if (checkX != getXCoordinate() || checkY != getYCoordinate()) {
                if (!isDestinationEmpty(checkX, checkY)) {
                    return false;
                }
            }
        }

        if (destinationPiece == null || !destinationPiece.getColor().equals(getColor())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidCapture(int x, int y) {
        return isValidMove(x, y, null, 0);
    }
}

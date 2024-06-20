//Sun.piece
package model;

import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;

public class Sun implements Piece {
    private int xCoordinate;
    private int yCoordinate;
    private String color;
    private ImageIcon image;
    private BoardModel model;

    public void loadImage() {
        String imagePath = "/img/" + color.charAt(0) + "Sun.png";
        this.image = new ImageIcon(getClass().getResource(imagePath));
    }

    public Sun(BoardModel model, int xCoordinate, int yCoordinate, String color) {
        this.model = model;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.color = color.toLowerCase();
        loadImage();
    }

    private boolean isDestinationEmpty(int x, int y) {
        Piece destinationPiece = model.getPieceAtPosition(x, y);
        return destinationPiece == null;
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
    public int getYCoordinate() {
        return yCoordinate;
    }

    @Override
    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
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

                if (capturedPiece instanceof Sun) {
                    String winningTeam = (getColor().equals("blue")) ? "yellow" : "blue";
                    System.out.println("Team " + winningTeam + " wins!");
                }
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

        int x = getXCoordinate();
        int y = getYCoordinate();

        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (isValidMove(i, j, null, 0) && (i != x || j != y)) {
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
        int deltaX = Math.abs(x - getXCoordinate());
        int deltaY = Math.abs(y - getYCoordinate());

        if ((deltaX == 1 && deltaY == 0) || (deltaX == 0 && deltaY == 1) || (deltaX == 1 && deltaY == 1)) {
            Piece destinationPiece = model.getPieceAtPosition(x, y);
            return destinationPiece == null || !destinationPiece.getColor().equals(getColor());
        }

        return false;
    }

    @Override
    public boolean isValidCapture(int x, int y) {
        int deltaX = Math.abs(x - getXCoordinate());
        int deltaY = Math.abs(y - getYCoordinate());

        if ((deltaX == 1 && deltaY == 0) || (deltaX == 0 && deltaY == 1) || (deltaX == 1 && deltaY == 1)) {
            Piece destinationPiece = model.getPieceAtPosition(x, y);
            return destinationPiece != null && !destinationPiece.getColor().equals(getColor());
        }

        return false;
    }
}

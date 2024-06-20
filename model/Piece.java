package model;

import javax.swing.ImageIcon;
import java.util.List;

public interface Piece {
    void setXCoordinate(int x); 
    int getXCoordinate();
    void setYCoordinate(int y); 
    int getYCoordinate();
    String getColor();
    ImageIcon getImage();
    void move(int x, int y);
    void capture();
    void flipCoordinates(int squareSize, boolean isFlipped);
    List<int[]> getValidMovePositions();
    boolean isValidMove(int x, int y, String currentPlayer, int movementCount);
    boolean isValidCapture(int x, int y);
}
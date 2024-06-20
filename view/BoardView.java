// BoardView.java
package view;

import model.BoardModel;
import controller.BoardController;
import model.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BoardView extends JPanel {
    private BoardController controller;
    private List<int[]> validMovePositions;
    private BoardModel model;
    private boolean isFlipped = false;

    // Constructor
    public BoardView(BoardModel model) {
        this.model = model;
    }

    // used in main can BoardController
    public void setController(BoardController controller) {
        this.controller = controller;
    }

    // set valid move for BoardController
    public void showValidMoves(List<int[]> validMovePositions) {
        this.validMovePositions = validMovePositions;
    }

    public int getSquareSize() {
        return getWidth() / 7; // Adjust the division factor based on your preference
    }

    // set the board limit
    private boolean isInsideBoard(int x, int y) {
        int boardWidth = getWidth();
        int boardHeight = getHeight();

        return x >= 0 && x < boardWidth && y >= 0 && y < boardHeight;
    }

    private void drawPieceValidMoves(Graphics g, Piece piece, int pieceSize, boolean isFlipped) {
        int x = isFlipped ? (6 - piece.getXCoordinate()) : piece.getXCoordinate();
        int y = isFlipped ? (5 - piece.getYCoordinate()) : piece.getYCoordinate();

        g.drawImage(piece.getImage().getImage(),
                x * pieceSize,
                y * pieceSize,
                pieceSize, pieceSize, this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (isFlipped) {
            drawFlippedBoardAndPieces(g);
            drawFlippedValidMoves(g);
        } else {
            drawBoard(g);
            drawPieces(g);
            drawValidMoves(g);
        }
    }

    public void drawBoard(Graphics g) {
        boolean white = true;
        int squareSize = getSquareSize();

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 6; c++) {
                if (white) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                if (validMovePositions != null) {
                    for (int[] position : validMovePositions) {
                        if (position[0] == r && position[1] == c) {
                            g.setColor(new Color(144, 238, 144));
                            break;
                        }
                    }
                }
                g.fillRect(r * squareSize, c * squareSize, squareSize, squareSize);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(r * squareSize, c * squareSize, squareSize, squareSize);

                white = !white;
            }
            white = !white;
        }
    }

    private void drawPiece(Graphics g, Piece piece, int pieceSize) {
        g.drawImage(piece.getImage().getImage(),
                piece.getXCoordinate() * pieceSize,
                piece.getYCoordinate() * pieceSize,
                pieceSize, pieceSize, this);
    }

    private void drawPieces(Graphics g) {
        for (Piece piece : model.getAllPieces()) {
            drawPiece(g, piece, getSquareSize());
        }
    }

    private void drawValidMoves(Graphics g) {
        if (validMovePositions != null) {
            int squareSize = getSquareSize();
            int lineWidth = 3;

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setStroke(new BasicStroke(lineWidth));

            String currentPlayer = controller.getCurrentPlayer();

            for (int[] position : validMovePositions) {
                int x = position[0] * squareSize;
                int y = position[1] * squareSize;

                int adjustedX = isFlipped ? (6 - position[0]) * squareSize : x;
                int adjustedY = isFlipped ? (5 - position[1]) * squareSize : y;

                if (isInsideBoard(x, y)) {
                    boolean isOccupiedByOpponent = controller.isOpponentPiece(position[0], position[1], currentPlayer);

                    boolean isWhiteSquare = (x / squareSize + y / squareSize) % 2 == 0;

                    if (isOccupiedByOpponent) {
                        g2d.setColor(new Color(255, 0, 0, 150)); // red outline
                        g2d.drawRect(adjustedX, adjustedY, squareSize, squareSize);

                        if (isWhiteSquare) {
                            g2d.setColor(Color.WHITE);
                        } else {
                            g2d.setColor(Color.LIGHT_GRAY);
                        }

                        g2d.fillRect(adjustedX, adjustedY, squareSize, squareSize);

                        Piece opponentPiece = model.getPieceAtPosition(position[0], position[1]);
                        if (opponentPiece != null) {
                            drawPieceValidMoves(g2d, opponentPiece, squareSize, isFlipped);
                        }
                    } else {
                        g2d.setColor(new Color(0, 255, 0, 150)); // green outline
                        g2d.drawRect(adjustedX, adjustedY, squareSize, squareSize);

                        if (isWhiteSquare) {
                            g2d.setColor(Color.WHITE);
                        } else {
                            g2d.setColor(Color.LIGHT_GRAY);
                        }

                        g2d.fillRect(adjustedX, adjustedY, squareSize, squareSize);
                    }
                }
            }

            g2d.dispose();
        }
    }

    private void drawFlippedPiece(Graphics g, Piece piece, int x, int y, int pieceSize, boolean isFlipped) {
        int adjustedX = isFlipped ? (getWidth() - x - pieceSize) : x;
        int adjustedY = isFlipped ? (getHeight() - y - pieceSize) : y;
        g.drawImage(piece.getImage().getImage(), adjustedX, adjustedY, pieceSize, pieceSize, this);
    }

    private void drawFlippedPieces(Graphics g) {
        int squareSize = getSquareSize();

        for (Piece piece : model.getAllPieces()) {
            drawFlippedPiece(g, piece, piece.getXCoordinate() * squareSize, piece.getYCoordinate() * squareSize,
                    squareSize,
                    isFlipped);
        }
    }

    private void drawFlippedBoardAndPieces(Graphics g) {
        boolean white = true;
        int squareSize = getSquareSize();

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 6; c++) {
                if (white) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                int x = (6 - r) * squareSize;
                int y = (5 - c) * squareSize;
                g.fillRect(x, y, squareSize, squareSize);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, squareSize, squareSize);
                white = !white;
            }
            white = !white;
        }
        drawFlippedPieces(g);
    }

    public void updateModelAfterFlip() {
        List<Piece> allPieces = controller.getModel().getAllPieces();

        for (Piece piece : allPieces) {
            int x = piece.getXCoordinate();
            int y = piece.getYCoordinate();

            if (isFlipped) {
                x = 6 - x;
                y = 5 - y;
            }

            piece.setXCoordinate(x);
            piece.setYCoordinate(y);
        }
    }

    private void drawFlippedValidMoves(Graphics g) {
        if (validMovePositions != null) {
            int squareSize = getSquareSize();
            int lineWidth = 3;

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setStroke(new BasicStroke(lineWidth));
            String currentPlayer = controller.getCurrentPlayer();

            for (int[] position : validMovePositions) {
                int x = position[0] * squareSize;
                int y = position[1] * squareSize;
                if (isFlipped) {
                    x = (6 - position[0]) * squareSize;
                    y = (5 - position[1]) * squareSize;
                }
                if (isInsideBoard(x, y)) {
                    boolean isOccupiedByOpponent = controller.isOpponentPiece(position[0], position[1], currentPlayer);
                    boolean isWhiteSquare = (x / squareSize + y / squareSize) % 2 == 0;
                    if (isFlipped) {
                        isWhiteSquare = !isWhiteSquare;
                    }

                    if (isOccupiedByOpponent) {
                        g2d.setColor(new Color(255, 0, 0, 150));
                        g2d.drawRect(x, y, squareSize, squareSize);
                        g2d.setColor(isWhiteSquare ? Color.WHITE : Color.LIGHT_GRAY);
                        g2d.fillRect(x, y, squareSize, squareSize);
                        Piece opponentPiece = model.getPieceAtPosition(position[0], position[1]);
                        if (opponentPiece != null) {
                            drawPieceValidMoves(g2d, opponentPiece, squareSize, isFlipped);
                        }
                    } else {
                        g2d.setColor(new Color(0, 255, 0, 150));
                        g2d.drawRect(x, y, squareSize, squareSize);
                        g2d.setColor(isWhiteSquare ? Color.WHITE : Color.LIGHT_GRAY);
                        g2d.fillRect(x, y, squareSize, squareSize);
                    }
                }
            }
            g2d.dispose();
        }
    }

    // use in BoardController.java
    public void flipBoardAndPieces(boolean flip) {
        isFlipped = flip;
        updateModelAfterFlip();
        revalidate();
        repaint();
    }

    // use in BoardController.java
    public void repaintPieces() {
        revalidate();
        repaint();
    }
}

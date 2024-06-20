// BoardModel.java
package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BoardModel {
    private List<Piece> pieces;
    private Piece[][] board;
    private view.GameEndListener gameEndListener;
    private boolean isBlueTurn;

    // Constructor
    public BoardModel() {
        initializePieces();
        initializeBoard(); 
        isBlueTurn = false; 
    }

    // transform piece in BoardController.java
    public void setTransformPieces(List<Piece> pieces) {
        this.pieces = pieces;
        initializeBoard();
    }

    // set game end in BoardController.java
    public void setGameEndListener(view.GameEndListener gameEndListener) {
        this.gameEndListener = gameEndListener;
    }

    // update piece position for Hourglass,Plus, Sun and Time
    public void setPieceAtPosition(Piece piece, int x, int y) {
        board[x][y] = piece;
    }

    // get piece position from pieces
    public Piece getPieceAtPosition(int x, int y) {
        for (Piece piece : pieces) {
            if (piece.getXCoordinate() == x && piece.getYCoordinate() == y) {
                return piece;
            }
        }
        return null;
    }
    
    // used in BoardView and BoardController
    public List<Piece> getAllPieces() {
        return pieces;
    }

    // capturing opponent piece
    public void removePiece(Piece piece) {
        pieces.remove(piece);
    }

    private void initializeBoard() {
        board = new Piece[7][6];
        for (Piece piece : pieces) {
            int x = piece.getXCoordinate();
            int y = piece.getYCoordinate();
            board[x][y] = piece;
        }
    }

    private void initializePieces() {
        pieces = new ArrayList<>();
        pieces.add(PieceCreate.createSun(this, 3, 0, "blue"));
        pieces.add(PieceCreate.createSun(this, 3, 5, "yellow"));

        pieces.add(PieceCreate.createPlus(this, 0, 0, "blue"));
        pieces.add(PieceCreate.createPlus(this, 6, 0, "blue"));
        pieces.add(PieceCreate.createPlus(this, 0, 5, "yellow"));
        pieces.add(PieceCreate.createPlus(this, 6, 5, "yellow"));

        pieces.add(PieceCreate.createHourGlass(this, 1, 0, "blue"));
        pieces.add(PieceCreate.createHourGlass(this, 5, 0, "blue"));
        pieces.add(PieceCreate.createHourGlass(this, 1, 5, "yellow"));
        pieces.add(PieceCreate.createHourGlass(this, 5, 5, "yellow"));

        pieces.add(PieceCreate.createTime(this, 2, 0, "blue"));
        pieces.add(PieceCreate.createTime(this, 4, 0, "blue"));
        pieces.add(PieceCreate.createTime(this, 2, 5, "yellow"));
        pieces.add(PieceCreate.createTime(this, 4, 5, "yellow"));

        pieces.add(PieceCreate.createPoint(this, 0, 1, "blue"));
        pieces.add(PieceCreate.createPoint(this, 1, 1, "blue"));
        pieces.add(PieceCreate.createPoint(this, 2, 1, "blue"));
        pieces.add(PieceCreate.createPoint(this, 3, 1, "blue"));
        pieces.add(PieceCreate.createPoint(this, 4, 1, "blue"));
        pieces.add(PieceCreate.createPoint(this, 5, 1, "blue"));
        pieces.add(PieceCreate.createPoint(this, 6, 1, "blue"));
        pieces.add(PieceCreate.createPoint(this, 0, 4, "yellow"));
        pieces.add(PieceCreate.createPoint(this, 1, 4, "yellow"));
        pieces.add(PieceCreate.createPoint(this, 2, 4, "yellow"));
        pieces.add(PieceCreate.createPoint(this, 3, 4, "yellow"));
        pieces.add(PieceCreate.createPoint(this, 4, 4, "yellow"));
        pieces.add(PieceCreate.createPoint(this, 5, 4, "yellow"));
        pieces.add(PieceCreate.createPoint(this, 6, 4, "yellow"));
    }

    // paring plus and time to swtich place
    private boolean isPair(Piece plusPiece, Piece timePiece) {
        return plusPiece.getColor().equals(timePiece.getColor()) &&
                Math.abs(plusPiece.getXCoordinate() - timePiece.getXCoordinate()) == 2 &&
                plusPiece.getYCoordinate() == timePiece.getYCoordinate();
    }

    public void swapPlusAndTimePieces() {
        List<Piece> plusPieces = new ArrayList<>();
        List<Piece> timePieces = new ArrayList<>();

        for (Piece piece : pieces) {
            if (piece instanceof Plus) {
                plusPieces.add(piece);
            } else if (piece instanceof Time) {
                timePieces.add(piece);
            }
        }

        for (int i = 0; i < Math.min(plusPieces.size(), timePieces.size()); i++) {
            Piece plusPiece = plusPieces.get(i);
            Piece timePiece = timePieces.get(i);

            if (isPair(plusPiece, timePiece)) {
                int plusX = plusPiece.getXCoordinate();
                int plusY = plusPiece.getYCoordinate();
                int timeX = timePiece.getXCoordinate();
                int timeY = timePiece.getYCoordinate();

                pieces.remove(plusPiece);
                pieces.remove(timePiece);

                pieces.add(new Time(this, plusX, plusY, plusPiece.getColor()));
                pieces.add(new Plus(this, timeX, timeY, timePiece.getColor()));
            }
        }
    }

    private boolean isSunCapturedByYellow() {
        int yellowSunCount = 0;

        for (Piece piece : pieces) {
            if (piece instanceof Sun && "yellow".equals(piece.getColor())) {
                yellowSunCount++;
            }
        }

        return yellowSunCount == 0;
    }

    private boolean isSunCapturedByBlue() {
        int blueSunCount = 0;

        for (Piece piece : pieces) {
            if (piece instanceof Sun && "blue".equals(piece.getColor())) {
                blueSunCount++;
            }
        }

        return blueSunCount == 0;
    }

    public void checkForSunCapture() {
        boolean sunCapturedByBlue = isSunCapturedByBlue();

        if (sunCapturedByBlue) {
            if (gameEndListener != null) {
                gameEndListener.onGameEnd("yellow");
            }
        } else {
            boolean sunCapturedByYellow = isSunCapturedByYellow();

            if (sunCapturedByYellow) {
                if (gameEndListener != null) {
                    gameEndListener.onGameEnd("blue");
                }
            }
        }
    }

    public void saveGame(String saveGame) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveGame, false))) {
            for (Piece piece : pieces) {
                writer.println(piece.getClass().getSimpleName() + " " +
                        piece.getXCoordinate() + " " +
                        piece.getYCoordinate() + " " +
                        piece.getColor());
            }

            writer.println(isBlueTurn ? "blue" : "yellow");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGame(String saveGame) {
        try (Scanner scanner = new Scanner(new File(saveGame))) {
            pieces.clear();

            while (scanner.hasNext()) {
                String pieceType = scanner.next();

                if (!scanner.hasNextInt()) {
                    return;
                }
                int x = scanner.nextInt();

                if (!scanner.hasNextInt()) {
                    return;
                }
                int y = scanner.nextInt();

                if (!scanner.hasNext()) {
                    return;
                }
                String color = scanner.next();

                Piece piece = createPiece(pieceType, x, y, color);
                pieces.add(piece);
            }

            if (scanner.hasNext()) {
                String currentPlayer = scanner.next();
                isBlueTurn = currentPlayer.equals("blue");
            }

            initializeBoard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Piece createPiece(String pieceType, int x, int y, String color) {
        switch (pieceType) {
            case "Sun":
                return PieceCreate.createSun(this, x, y, color);
            case "Plus":
                return PieceCreate.createPlus(this, x, y, color);
            case "HourGlass":
                return PieceCreate.createHourGlass(this, x, y, color);
            case "Time":
                return PieceCreate.createTime(this, x, y, color);
            case "Point":
                return PieceCreate.createPoint(this, x, y, color);
            default:
                throw new IllegalArgumentException("Invalid piece type: " + pieceType);
        }
    }

    public void resetGame() {
        initializePieces();
        initializeBoard();
        isBlueTurn = false;
    }
}

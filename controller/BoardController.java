// BoardController.java
package controller;

import model.Piece;
import model.BoardModel;
import view.BoardView;
import model.Plus;
import model.Time;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class BoardController implements MouseListener, view.GameEndListener {
    private BoardModel model;
    private BoardView view;
    private BoardController controller;
    private Piece selectedPiece;
    private String currentPlayer;
    private int movementCount;
    private static final int transformCount = 4;
    private boolean isBoardFlipped = false;

    public BoardController(BoardModel model, BoardView view) {
        this.model = model;
        this.view = view;
        this.controller = this;

        this.view.setController(this.controller);
        this.view.addMouseListener(this);
        this.model.setGameEndListener(this);
        currentPlayer = "yellow";
        movementCount = 0;
    }

    public BoardModel getModel() {
        return model;
    }

    public BoardView getView() {
        return view;
    }

    public BoardController getController() {
        return controller;
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    // use to determine opponent in BoardView
    public boolean isOpponentPiece(int x, int y, String currentPlayer) {
        Piece piece = model.getPieceAtPosition(x, y);
        boolean withinBoardLimits = x >= 0 && x < 7 && y >= 0 && y < 6;

        return withinBoardLimits && piece != null && !piece.getColor().equals(currentPlayer);
    }

    public void switchPlayer() {
        currentPlayer = currentPlayer.equals("yellow") ? "blue" : "yellow";
        isBoardFlipped = currentPlayer.equals("blue");
        view.flipBoardAndPieces(isBoardFlipped);
    }

    private void incrementMovementCount() {
        movementCount = (movementCount + 1) % 4;
    }

    // pairing plus and time pieces
    private boolean isPair(Piece plusPiece, Piece timePiece) {
        return plusPiece.getColor().equals(timePiece.getColor()) &&
                Math.abs(plusPiece.getXCoordinate() - timePiece.getXCoordinate()) == 2 &&
                plusPiece.getYCoordinate() == timePiece.getYCoordinate();
    }

    private void transformPieces() {
        List<Piece> allPieces = model.getAllPieces();
        List<Piece> transformedPieces = new ArrayList<>();
        List<Piece> plusPieces = new ArrayList<>();
        List<Piece> timePieces = new ArrayList<>();
        List<Piece> otherPieces = new ArrayList<>();

        for (Piece piece : allPieces) {
            if (piece instanceof Plus) {
                plusPieces.add(piece);
            } else if (piece instanceof Time) {
                timePieces.add(piece);
            } else {
                otherPieces.add(piece);
            }
        }

        for (int i = 0; i < Math.min(plusPieces.size(), timePieces.size()); i++) {
            Piece plusPiece = plusPieces.get(i);
            Piece timePiece = timePieces.get(i);

            if (isPair(plusPiece, timePiece)) {
                int plusX = isBoardFlipped ? 6 - plusPiece.getXCoordinate() : plusPiece.getXCoordinate();
                int plusY = isBoardFlipped ? 5 - plusPiece.getYCoordinate() : plusPiece.getYCoordinate();
                int timeX = isBoardFlipped ? 6 - timePiece.getXCoordinate() : timePiece.getXCoordinate();
                int timeY = isBoardFlipped ? 5 - timePiece.getYCoordinate() : timePiece.getYCoordinate();

                Piece newTimePiece = new Time(model, plusX, plusY, plusPiece.getColor());
                Piece newPlusPiece = new Plus(model, timeX, timeY, timePiece.getColor());

                transformedPieces.add(newTimePiece);
                transformedPieces.add(newPlusPiece);
            } else {
                transformedPieces.add(plusPiece);
                transformedPieces.add(timePiece);
            }
        }
        transformedPieces.addAll(otherPieces);
        model.setTransformPieces(transformedPieces);
        view.repaintPieces();
    }

    private void checkForTransformation() {
        if (movementCount % transformCount == 0) {
            transformPieces();
        }
    }

    public void flipBoardAndPieces() {
        isBoardFlipped = !isBoardFlipped;
        view.flipBoardAndPieces(isBoardFlipped);
        view.repaint();
    }

    public void flipBoardAndPieces(boolean flip) {
        isBoardFlipped = flip;
        view.flipBoardAndPieces(isBoardFlipped);
        view.repaint();
    }

    private void startNewGame() {
        model.resetGame();
        view.setController(this);
        view.repaintPieces();
        currentPlayer = "yellow";
        movementCount = 0;
        isBoardFlipped = false;
    }

    @Override
    public void onGameEnd(String winningColor) {
        String message = "Congratulations! The winner is " + winningColor + ".";
        int option = JOptionPane.showOptionDialog(view, message, "Game Over", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, new Object[] { "Start New Game", "Quit Game" }, null);

        if (option == 0) {
            startNewGame();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX() / view.getSquareSize();
        int mouseY = e.getY() / view.getSquareSize();

        int boardMouseX = isBoardFlipped ? 6 - mouseX : mouseX;
        int boardMouseY = isBoardFlipped ? 5 - mouseY : mouseY;

        Piece clickedPiece = model.getPieceAtPosition(boardMouseX, boardMouseY);

        if (clickedPiece != null && clickedPiece.getColor().equals(currentPlayer)) {
            selectedPiece = clickedPiece;
            view.showValidMoves(selectedPiece.getValidMovePositions());
        } else if (selectedPiece != null) {
            List<int[]> validMovePositions = selectedPiece.getValidMovePositions();

            boolean isValidMove = validMovePositions.stream()
                    .anyMatch(position -> position[0] == boardMouseX && position[1] == boardMouseY);

            if (isValidMove) {
                selectedPiece.move(boardMouseX, boardMouseY);
                switchPlayer();
                incrementMovementCount();
                checkForTransformation();
                model.checkForSunCapture();
                isBoardFlipped = "blue".equals(currentPlayer);

                if (isBoardFlipped) {
                    view.flipBoardAndPieces(isBoardFlipped);

                    view.repaint();
                }
            }

            selectedPiece = null;
            view.showValidMoves(null);
        }

        view.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}

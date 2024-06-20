//TalabiaChess.java
import model.BoardModel;
import view.BoardView;
import controller.BoardController;
import view.MenuView;

import javax.swing.*;

public class TalabiaChess extends JFrame {

    private BoardView boardView;
    private MenuView menuView;
    private BoardModel model;

    public TalabiaChess() {
        model = new BoardModel();
        boardView = new BoardView(model);
        BoardController controller = new BoardController(model, boardView);

        menuView = new MenuView();
        setJMenuBar(menuView);
        menuView.setupMenuActions(model, boardView);

        boardView.setController(controller);
        add(boardView);

        setTitle("Talabia Chess");
        setSize(723, 669);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        TalabiaChess talabiaChess = new TalabiaChess();
        talabiaChess.setVisible(true);
    }
}


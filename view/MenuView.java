//MenuView.java
package view;

import model.BoardModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuView extends JMenuBar {
    private JMenuItem loadGameMenuItem;
    private JMenuItem saveGameMenuItem;
    private JMenuItem newGameMenuItem;
    private JMenuItem quitMenuItem;

    private MenuListener menuListener;

    public MenuView() {
        initializeMenu();
        setupActions();
    }

    public void setMenuListener(MenuListener menuListener) {
        this.menuListener = menuListener;
    }

    public interface MenuListener {
        void onLoadGame();

        void onSaveGame();

        void onNewGame();

        void onQuit();
    }

    private void initializeMenu() {
        JMenu fileMenu = new JMenu("Menu");

        loadGameMenuItem = new JMenuItem("Load Game");
        saveGameMenuItem = new JMenuItem("Save Game");
        newGameMenuItem = new JMenuItem("New Game");
        quitMenuItem = new JMenuItem("Quit");

        fileMenu.add(loadGameMenuItem);
        fileMenu.add(saveGameMenuItem);
        fileMenu.add(newGameMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);

        this.add(fileMenu);
    }

    private void setupActions() {
        loadGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuListener != null) {
                    int result = showConfirmationDialog("Load Game",
                            "Game Load! Current progress will be lost. Continue?");
                    if (result == JOptionPane.YES_OPTION) {
                        menuListener.onLoadGame();
                        showInformationDialog("Game Load", "Game Loaded!");
                    }
                }
            }
        });

        saveGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuListener != null) {
                    menuListener.onSaveGame();
                    showInformationDialog("Game Save", "Game Saved!");
                }
            }
        });

        newGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuListener != null) {
                    int result = showConfirmationDialog("New Game", "Current progress will be lost. Continue?");
                    if (result == JOptionPane.YES_OPTION) {
                        menuListener.onNewGame();
                        showInformationDialog("New Game", "New Game Started!");
                    }
                }
            }
        });

        quitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuListener != null) {
                    int result = showConfirmationDialog("Quit", "Are you sure you want to quit?");
                    if (result == JOptionPane.YES_OPTION) {
                        menuListener.onQuit();
                    }
                }
            }
        });
    }

    private int showConfirmationDialog(String title, String message) {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
    }

    private void showInformationDialog(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void setupMenuActions(BoardModel model, BoardView boardView) {
        setMenuListener(new MenuListener() {
            @Override
            public void onLoadGame() {
                String fileName = "SaveGame.txt";
                model.loadGame(fileName);
                boardView.repaint();
            }

            @Override
            public void onSaveGame() {
                String fileName = "SaveGame.txt";
                model.saveGame(fileName);
            }

            @Override
            public void onNewGame() {
                model.resetGame();
                boardView.repaint();
            }

            @Override
            public void onQuit() {
                System.exit(0);
            }
        });
    }
}

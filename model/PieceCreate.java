// PieceCreate.java
package model;

public class PieceCreate {
    public static Piece createSun(BoardModel board, int x, int y, String color) {
        return new Sun(board, x, y, color);
    }

    public static Piece createPlus(BoardModel board, int x, int y, String color) {
        return new Plus(board, x, y, color);
    }

    public static HourGlass createHourGlass(BoardModel board, int x, int y, String color) {
        return new HourGlass(board, x, y, color);
    }

    public static Time createTime(BoardModel board, int x, int y, String color) {
        return new Time(board, x, y, color);
    }

    public static Point createPoint(BoardModel board, int x, int y, String color) {
        return new Point(board, x, y, color);
    }
}

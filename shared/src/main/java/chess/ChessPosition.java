package chess;

import java.util.Objects;

public class ChessPosition {

    private final int row;
    private final int col;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that=(ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return col;
    }

    @Override
    public String toString() {
        // Format position in a "(row, column)" format
        return "(" + row + ", " + col + ")";
    }
}

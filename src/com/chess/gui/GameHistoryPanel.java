package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.chess.gui.Table.*;

public class GameHistoryPanel extends JPanel {

    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANE_DIMENSION = new Dimension(100, 40);

    public GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        table.setVisible(true);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANE_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public void redo(final Board board, final MoveLog moveHistory) {
        int currentRow = 0;
        this.model.clear();
        for (final Move move : moveHistory.getMoves()) {
            final String moveText = move.toString();
            if (move.getPiece().getPieceAlliance().isWhite()) {
                this.model.setValueAt(moveText, currentRow, 0);
            }
            else if (move.getPiece().getPieceAlliance().isBlack()) {
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }

        if(moveHistory.getMoves().size() > 0) {
            final Move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);
            final String moveText = lastMove.toString();

            if (lastMove.getPiece().getPieceAlliance().isWhite()) {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
            }
            else if (lastMove.getPiece().getPieceAlliance().isBlack()) {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
        vertical.validate();
    }

    private String calculateCheckAndCheckMateHash(Board board) {
        if (board.getCurrentPlayer().isInCheckMate()) return "#";
        else if (board.getCurrentPlayer().isInCheck()) return "+";
        else return "";
    }

    private static class DataModel extends DefaultTableModel {
        private final List<Row> values;
        private static final String[] NAMES = new String[] {"White", "Black"};

        public DataModel() {
            this.values = new ArrayList<>();
        }

        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if (this.values == null) return 0;
            return values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            Row currRow = values.get(row);
            if (col == 0) return currRow.getWhiteMove();
            else if (col == 1) return currRow.getBlackMove();
            return null;
        }

        @Override
        public void setValueAt(Object val, int row, int col) {
            Row currRow;
            if (values.size() <= row) {
                currRow = new Row();
                values.add(currRow);
            } else {
                currRow = this.values.get(row);
            }

            if (col == 0) {
                currRow.setWhiteMove((String)val);
                fireTableRowsInserted(row, row);
            } else if (col == 1) {
                currRow.setBlackMove((String) val);
                fireTableCellUpdated(row, col);
            }
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return Move.class;
        }

        @Override
        public String getColumnName(int col) {
            return NAMES[col];
        }
    }

    private static class Row {
        private String whiteMove, blackMove;

        public Row() {

        }

        public String getWhiteMove() {
            return this.whiteMove;
        }

        public String getBlackMove() {
            return this.blackMove;
        }

        public void setWhiteMove(String move) {
            this.whiteMove = move;
        }

        public void setBlackMove(String move) {
            this.blackMove = move;
        }
    }

}

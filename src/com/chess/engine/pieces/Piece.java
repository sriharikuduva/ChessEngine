package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;
import java.util.Objects;

public abstract class Piece {

    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    protected final PieceType pieceType;

    public Piece(final int piecePosition, final Alliance pieceAlliance, final PieceType pieceType, final boolean isFirstMove) {
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        this.isFirstMove = isFirstMove;
        this.pieceType = pieceType;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public int getPiecePosition() {
        return piecePosition;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public Alliance getPieceAlliance() {
        return pieceAlliance;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public abstract Piece movePiece(Move move);

    public int getPieceValue() {
        return this.pieceType.getValue();
    }

    public enum PieceType {

        PAWN("P", 100),
        KNIGHT("N", 300),
        BISHOP("B", 300),
        QUEEN("Q", 900),
        KING("K", 10000),
        ROOK("R", 500);

        private String pieceName;
        private int val;

        PieceType(final String pieceName, final int pieceVal) {
            this.pieceName = pieceName;
            this.val = pieceVal;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public int getValue() {
            return this.val;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return piecePosition == piece.piecePosition &&
                isFirstMove == piece.isFirstMove &&
                pieceAlliance == piece.pieceAlliance &&
                pieceType == piece.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piecePosition, pieceAlliance, isFirstMove, pieceType);
    }
}

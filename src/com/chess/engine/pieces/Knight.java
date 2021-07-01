package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Knight extends Piece {

    private static final int[] MOVES = new int[] {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.KNIGHT, true);
    }

    public Knight(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.KNIGHT, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();


        for (int offset : MOVES) {
            final int candidateCoord = this.piecePosition + offset;
            if (BoardUtils.isValidTileCoordinate(candidateCoord)) {
                if (isFirstColumnExclusion(this.piecePosition, offset)) continue;
                if (isSecondColumnExclusion(this.piecePosition, offset)) continue;
                if (isSeventhColumnExclusion(this.piecePosition, offset)) continue;
                if (isEighthColumnExclusion(this.piecePosition, offset)) continue;


                final Tile candidateTile = board.getTile(candidateCoord);
                if (!candidateTile.isTileOccupied())
                    legalMoves.add(new MajorMove(board, this, candidateCoord));
                else {
                    final Piece destPiece = candidateTile.getPiece();
                    if (this.pieceAlliance != destPiece.getPieceAlliance()) {
                        legalMoves.add(new MajorAttackMove(board, this, candidateCoord, destPiece));
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

    private boolean isFirstColumnExclusion(final int currPiecePos, final int offset) {
        return BoardUtils.FIRST_COL[currPiecePos] &&
                ((offset == -17 || offset == -10 || offset == 6 || offset == 15));
    }

    private boolean isSecondColumnExclusion(final int currPiecePos, final int offset) {
        return BoardUtils.SECOND_COL[currPiecePos] && (offset == 6 || offset == -10);
    }

    private boolean isSeventhColumnExclusion(final int currPiecePos, final int offset) {
        return BoardUtils.SEVENTH_COL[currPiecePos] && (offset == -6 || offset == 10);
    }

    private boolean isEighthColumnExclusion(final int currPiecePos, final int offset) {
        return BoardUtils.EIGHTH_COL[currPiecePos] && (offset == -15 || offset == -6 || offset == 17 || offset == 10);
    }

    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }

    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getDestination(), move.getPiece().getPieceAlliance());
    }
}

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

public class Bishop extends Piece {

    private static final int[] MOVES = new int[] {-9, -7, 7, 9};

    public Bishop(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.BISHOP, true);
    }

    public Bishop(int piecePosition, Alliance pieceAlliance, boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.BISHOP, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (int offset : MOVES) {
            int candidatePos = this.piecePosition;

            while (BoardUtils.isValidTileCoordinate(candidatePos)) {

                if (isFirstColumnExclusion(candidatePos, offset)) break;
                if (isEighthColumnExclusion(candidatePos, offset)) break;

                candidatePos += offset;

                if (BoardUtils.isValidTileCoordinate(candidatePos)) {
                    final Tile candidateTile = board.getTile(candidatePos);
                    if (!candidateTile.isTileOccupied())
                        legalMoves.add(new MajorMove(board, this, candidatePos));
                    else {
                        final Piece destPiece = candidateTile.getPiece();
                        if (this.pieceAlliance != destPiece.getPieceAlliance()) {
                            legalMoves.add(new MajorAttackMove(board, this, candidatePos, destPiece));
                        }
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getDestination(), move.getPiece().getPieceAlliance());
    }

    private boolean isFirstColumnExclusion(final int currPiecePos, final int offset) {
        return BoardUtils.FIRST_COL[currPiecePos] && ((offset == 7 || offset == -9 ));
    }

    private boolean isEighthColumnExclusion(final int currPiecePos, final int offset) {
        return BoardUtils.EIGHTH_COL[currPiecePos] && (offset == -7 ||  offset == 9);
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }
}

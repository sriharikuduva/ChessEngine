package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Pawn extends Piece {

    private static final int[] MOVES = new int[] {8, 16, 7, 9};


    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.PAWN, true);
    }

    public Pawn(final int piecePosition, final Alliance pieceAlliance, boolean isFirstMove) {
        super(piecePosition, pieceAlliance, PieceType.PAWN, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int offset : MOVES) {
            int candidatePos = this.piecePosition + (offset * this.pieceAlliance.getDirection());
            if (!BoardUtils.isValidTileCoordinate(candidatePos)) continue;

            if (offset == 8 && !board.getTile(candidatePos).isTileOccupied()) {

                if (this.pieceAlliance.isPawnPromotionSquare(candidatePos)) {
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidatePos)));
                } else {
                    legalMoves.add(new PawnMove(board, this, candidatePos));
                }
            } else if (offset == 16 && this.isFirstMove()
                    && ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack())
                    || (BoardUtils.SECOND_RANK[piecePosition] && this.pieceAlliance.isWhite()))) {

                final int behindDest = this.piecePosition + (8 * this.pieceAlliance.getDirection());
                if (!board.getTile(behindDest).isTileOccupied() && !board.getTile(candidatePos).isTileOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidatePos));
                }

            } else if (offset == 7 &&
                    !((BoardUtils.EIGHTH_COL[this.piecePosition] && this.pieceAlliance.isWhite() ||
                    (BoardUtils.FIRST_COL[this.piecePosition] && this.pieceAlliance.isBlack())))) {

                if (board.getTile(candidatePos).isTileOccupied()) {
                    Piece otherPiece = board.getTile(candidatePos).getPiece();
                    if (otherPiece.pieceAlliance != this.pieceAlliance) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidatePos)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidatePos, otherPiece)));
                        } else {
                            legalMoves.add(new PawnAttackMove(board, this, candidatePos, otherPiece));
                        }
                    }
                } else if (board.getEnPassantPawn() != null) {
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
                        if (this.pieceAlliance != board.getEnPassantPawn().pieceAlliance) {
                            legalMoves.add(new PawnEnPassantAttackMove(board, this, candidatePos, board.getEnPassantPawn()));
                        }
                    }
                }

            } else if (offset == 9 &&
                    !((BoardUtils.FIRST_COL[this.piecePosition] && this.pieceAlliance.isWhite() ||
                      (BoardUtils.EIGHTH_COL[this.piecePosition] && this.pieceAlliance.isBlack())))) {

                if (board.getTile(candidatePos).isTileOccupied()) {
                    Piece otherPiece = board.getTile(candidatePos).getPiece();
                    if (otherPiece.pieceAlliance != this.pieceAlliance) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidatePos)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidatePos, otherPiece)));
                        } else {
                            legalMoves.add(new PawnAttackMove(board, this, candidatePos, otherPiece));
                        }
                    }
                } else if (board.getEnPassantPawn() != null) {
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
                        if (this.pieceAlliance != board.getEnPassantPawn().pieceAlliance) {
                            legalMoves.add(new PawnEnPassantAttackMove(board, this, candidatePos, board.getEnPassantPawn()));
                        }
                    }
                }

            }


        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestination(), move.getPiece().getPieceAlliance());
    }

    public Piece getPromotionPiece() {
        return new Queen(this.piecePosition, this.pieceAlliance,  false);
    }
}

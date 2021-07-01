package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class BlackPlayer extends Player{


    public BlackPlayer(Board board, Collection<Move> whiteLegalMoves, Collection<Move> blackLegalMoves) {
        super(board, blackLegalMoves, whiteLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return board.getWhitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals) {
        List<Move> list = new ArrayList<>();

        if (this.king.isFirstMove() && !this.isInCheck()) {
            if (!this.board.getTile(5).isTileOccupied() && ! this.board.getTile(6).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(7);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if (Player.calculateAttackOnTile(5, opponentLegals).isEmpty() &&
                            Player.calculateAttackOnTile(6, opponentLegals).isEmpty()
                            && rookTile.getPiece().getPieceType() == Piece.PieceType.ROOK) {
                        list.add(new KingSideCastleMove(board, this.king, 6, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 5));
                    }
                }
            }


            if (!this.board.getTile(1).isTileOccupied() && !this.board.getTile(2).isTileOccupied()
                    && !this.board.getTile(3).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(0);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if (calculateAttackOnTile(2, opponentLegals).isEmpty() &&
                        calculateAttackOnTile(3, opponentLegals).isEmpty() &&
                        rookTile.getPiece().getPieceType() == Piece.PieceType.ROOK) {

                        list.add(new QueenSideCastleMove(board, this.king, 2, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 3));
                    }
                }
            }
        }

        return ImmutableList.copyOf(list);
    }

}

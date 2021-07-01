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

public class WhitePlayer extends Player{

    public WhitePlayer(Board board, Collection<Move> whiteLegalMoves, Collection<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return board.getBlackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals) {
        List<Move> list = new ArrayList<>();

        if (this.king.isFirstMove() && !this.isInCheck()) {
            if (!this.board.getTile(61).isTileOccupied() && ! this.board.getTile(62).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(63);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {

                    if (Player.calculateAttackOnTile(61, opponentLegals).isEmpty() &&
                        Player.calculateAttackOnTile(62, opponentLegals).isEmpty()
                            && rookTile.getPiece().getPieceType() == Piece.PieceType.ROOK) {
                            list.add(new KingSideCastleMove(board, this.king, 62, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 61));
                    }
                }
            }


            if (!this.board.getTile(59).isTileOccupied() && !this.board.getTile(58).isTileOccupied()
                    && !this.board.getTile(57).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(56);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {

                    if (Player.calculateAttackOnTile(58, opponentLegals).isEmpty() &&
                        Player.calculateAttackOnTile(59, opponentLegals).isEmpty() &&
                        rookTile.getPiece().getPieceType() == Piece.PieceType.ROOK) {
                            list.add(new QueenSideCastleMove(board, this.king, 58, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 59));
                    }
                }
            }

        }

        return ImmutableList.copyOf(list);
    }
}

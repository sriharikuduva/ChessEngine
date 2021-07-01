package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;

import java.util.*;

public class Board {

    private final List<Tile> gameBoard;

    private final Collection<Piece> whitePieces, blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;

    private Board(Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = getActivePieces(gameBoard, Alliance.WHITE);
        this.blackPieces = getActivePieces(gameBoard, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move>
                whiteLegalMoves = calculateLegalMoves(this.whitePieces),
                blackLegalMoves = calculateLegalMoves(this.blackPieces);
        this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteLegalMoves, blackLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces) {
        List<Move> legalMoves = new ArrayList<>();
        for (Piece p : pieces) {
            legalMoves.addAll(p.calculateLegalMoves(this));
        }

        return ImmutableList.copyOf(legalMoves);
    }

    private static Collection<Piece> getActivePieces(final List<Tile> gameBoard, final Alliance alliance) {
        List<Piece> pieces = new ArrayList<>();
        for (Tile t : gameBoard) {
            if (t.isTileOccupied() && t.getPiece().getPieceAlliance() == alliance)
                pieces.add(t.getPiece());
        }
        return ImmutableList.copyOf(pieces);
    }

    public WhitePlayer getWhitePlayer() {
        return whitePlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public BlackPlayer getBlackPlayer() {
        return blackPlayer;
    }

    private List<Tile> createGameBoard(final Builder builder) {
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        for (int i = 0; i < tiles.length; i++)
            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
        return ImmutableList.copyOf(tiles);
    }

    public static Board createStandardBoard() {
        Builder builder = new Builder();
        // black pieces
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        builder.setPiece(new Queen(3, Alliance.BLACK));
        builder.setPiece(new King(4, Alliance.BLACK));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        builder.setPiece(new Pawn(8, Alliance.BLACK));
        builder.setPiece(new Pawn(9, Alliance.BLACK));
        builder.setPiece(new Pawn(10, Alliance.BLACK));
        builder.setPiece(new Pawn(11, Alliance.BLACK));
        builder.setPiece(new Pawn(12, Alliance.BLACK));
        builder.setPiece(new Pawn(13, Alliance.BLACK));
        builder.setPiece(new Pawn(14, Alliance.BLACK));
        builder.setPiece(new Pawn(15, Alliance.BLACK));

        builder.setPiece(new Pawn(48, Alliance.WHITE));
        builder.setPiece(new Pawn(49, Alliance.WHITE));
        builder.setPiece(new Pawn(50, Alliance.WHITE));
        builder.setPiece(new Pawn(51, Alliance.WHITE));
        builder.setPiece(new Pawn(52, Alliance.WHITE));
        builder.setPiece(new Pawn(53, Alliance.WHITE));
        builder.setPiece(new Pawn(54, Alliance.WHITE));
        builder.setPiece(new Pawn(55, Alliance.WHITE));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Queen(59, Alliance.WHITE));
        builder.setPiece(new King(60, Alliance.WHITE));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Rook(63, Alliance.WHITE));

        builder.setNextMoveMaker(Alliance.WHITE);
        return builder.build();
    }

    public Tile getTile(final int coord) {
        return gameBoard.get(coord);
    }

    public Collection<Piece> getWhitePieces() {
        return whitePieces;
    }

    public Collection<Piece> getBlackPieces() {
        return blackPieces;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            String tileText = gameBoard.get(i).toString();
            sb.append(String.format("%3s", tileText));
            if ((i + 1) % BoardUtils.NUM_TILES_ROW == 0)
                sb.append("\n");
        }
        return sb.toString();
    }

    public Collection<Move> getAllLegalMoves() {
        List<Move> moves = new ArrayList<>();
        moves.addAll(this.whitePlayer.getLegalMoves());
        moves.addAll(this.blackPlayer.getLegalMoves());
        return ImmutableList.copyOf(moves);
    }


    public static class Builder {

        Map<Integer, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;

        public Builder() {
            this.boardConfig = new HashMap<>();
        }

        public Builder setPiece(final Piece p) {
            this.boardConfig.put(p.getPiecePosition(), p);
            return this;
        }

        public Builder setNextMoveMaker(final Alliance nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn movedPawn) {
            this.enPassantPawn = movedPawn;
        }
    }
}

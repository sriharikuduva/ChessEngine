package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile {
    protected final int tileCoordinate;

    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllEmptyTiles();

    private static Map<Integer, EmptyTile> createAllEmptyTiles() {
        final Map<Integer, EmptyTile> res = new HashMap<>();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++)
            res.put(i, new EmptyTile(i));
        return ImmutableMap.copyOf(res);
    }


    public static Tile createTile(int tileCoordinate, Piece piece) {
        return piece == null ? EMPTY_TILES_CACHE.get(tileCoordinate) : new OccupiedTile(tileCoordinate, piece);
    }

    private Tile(final int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public int getTileCoordinate() {
        return tileCoordinate;
    }


    public static final class EmptyTile extends Tile {

        private EmptyTile(final int tileCoord) {
            super(tileCoord);
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }
    }

    public static final class OccupiedTile extends Tile {

        private final Piece piece;

        private OccupiedTile(final int tileCoordinate, final Piece piece) {
            super(tileCoordinate);
            this.piece = piece;
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return piece;
        }

        @Override
        public String toString() {
            return piece.getPieceAlliance() == Alliance.BLACK ? piece.toString().toLowerCase() : piece.toString();
        }
    }

}

package com.chess.engine.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BoardUtils {

    public static final boolean FIRST_COL[] = initColumns(0);
    public static final boolean SECOND_COL[] = initColumns(1);
    public static final boolean SEVENTH_COL[] = initColumns(6);
    public static final boolean EIGHTH_COL[] = initColumns(7);

    public static final boolean EIGHTH_RANK[] = initRow(0);
    public static final boolean SEVENTH_RANK[] = initRow(8);
    public static final boolean SIXTH_RANK[] = initRow(16);
    public static final boolean FIFTH_RANK[] = initRow(24);
    public static final boolean FOURTH_RANK[] = initRow(32);
    public static final boolean THIRD_RANK[] = initRow(40);
    public static final boolean SECOND_RANK[] = initRow(48);
    public static final boolean FIRST_RANK[] = initRow(56);

    public static final String[] ALGEBREIC_NOTATION = initializeAlgebraicNotation();
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();
    public static final int START_TILE_INDEX = 0;


    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_ROW = 8;

    private BoardUtils() {
        throw new RuntimeException();
    }

    private static String[] initializeAlgebraicNotation() {
        return new String[] {
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1" };
    }

    private static Map<String, Integer> initializePositionToCoordinateMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = START_TILE_INDEX; i < NUM_TILES; i++) {
            positionToCoordinate.put(ALGEBREIC_NOTATION[i], i);
        }
        return Collections.unmodifiableMap(positionToCoordinate);
    }

    private static boolean[] initColumns(int pos) {
        final boolean[] res = new boolean[NUM_TILES];
        do {
            res[pos] = true;
            pos += NUM_TILES_ROW;
        } while (pos < NUM_TILES);
        return res;
    }

    private static boolean[] initRow(int pos) {
        final boolean[] res = new boolean[NUM_TILES];
        for (int i = pos; i < 8 + pos; i++)
            res[i] = true;
        return res;
    }

    public static boolean isValidTileCoordinate(final int candidateCoord) {
        return candidateCoord >= 0 && candidateCoord <= 63;
    }

    public static int getCoordinateAtPosition(String position) {
        return POSITION_TO_COORDINATE.get(position);
    }

    public static String getPositionAtCoordinate(int coordinate) {
        return ALGEBREIC_NOTATION[coordinate];
    }

}

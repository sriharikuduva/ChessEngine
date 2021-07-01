package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

public class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;



    public StandardBoardEvaluator() { }

    @Override
    public int evaluate(Board board, int depth) {
        return scorePlayer(board, board.getWhitePlayer(), depth) - scorePlayer(board, board.getBlackPlayer(), depth);
    }

    private int scorePlayer(Board board, Player player, int depth) {
        return pieceValue(player) + mobility(player) + check(player) + checkMate(player, depth) + castled(player);
    }

    private int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private int checkMate(Player player, int depth) {
        return player.getOpponent().isInCheckMate() ? CHECKMATE_BONUS * depthBonus(depth): 0;
    }

    private int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private int check(Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private int mobility(Player player) {
        return player.getLegalMoves().size();
    }

    private int pieceValue(Player player) {
        int sum = 0;
        for (Piece p : player.getActivePieces())
            sum += p.getPieceValue();
        return sum;
    }
}

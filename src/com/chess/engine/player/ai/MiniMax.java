package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.Player;

public class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private int searchDepth;

    public MiniMax(int searchDepth) {
        boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    @Override
    public Move execute(Board board) {

        final long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int highestSeenVal = Integer.MIN_VALUE;
        int lowestSeenVal = Integer.MAX_VALUE;
        int currentVal;

        System.out.println(board.getCurrentPlayer() + " THINKING with depth = " + searchDepth);
        int numMoves = board.getCurrentPlayer().getLegalMoves().size();

        for (Move m : board.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition transition = board.getCurrentPlayer().makeMove(m);
            if (transition.getMoveStatus().isDone()) {

                currentVal = board.getCurrentPlayer().getAlliance().isWhite() ?
                        min(transition.getTransitionBoard(), searchDepth - 1) :
                        max(transition.getTransitionBoard(), searchDepth - 1);

                if (board.getCurrentPlayer().getAlliance().isWhite() && currentVal > highestSeenVal) {
                    highestSeenVal = currentVal;
                    bestMove = m;
                } else if (board.getCurrentPlayer().getAlliance().isBlack() && currentVal < lowestSeenVal) {
                    lowestSeenVal = currentVal;
                    bestMove = m;
                }
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("Execution Time: " + executionTime);

        return bestMove;
    }

    private boolean isEndGameScenario(Player p) {
        return p.isInCheckMate() || p.isInStaleMate();
    }

    public int min(Board board, int depth) {
        if (depth == 0 || isEndGameScenario(board.getCurrentPlayer())) {
            return boardEvaluator.evaluate(board, depth);
        }

        int low = Integer.MAX_VALUE;
        for (Move m : board.getCurrentPlayer().getLegalMoves()) {
            MoveTransition transition = board.getCurrentPlayer().makeMove(m);
            if (transition.getMoveStatus().isDone()) {
                low = Math.min(low, max(transition.getTransitionBoard(), depth - 1));
            }
        }
        return low;
    }

    public int max(Board board, int depth) {
        if (depth == 0 || isEndGameScenario(board.getCurrentPlayer())) {
            return boardEvaluator.evaluate(board, depth);
        }

        int high = Integer.MIN_VALUE;
        for (Move m : board.getCurrentPlayer().getLegalMoves()) {
            MoveTransition transition = board.getCurrentPlayer().makeMove(m);
            if (transition.getMoveStatus().isDone()) {
                high = Math.max(high, min(transition.getTransitionBoard(), depth - 1));
            }
        }
        return high;
    }
}

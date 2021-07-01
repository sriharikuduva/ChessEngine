package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.Collection;
import java.util.Objects;

import static com.chess.engine.board.Board.Builder;

public abstract class Move {
    final Board board;
    final Piece piece;
    final int destination;
    final boolean isFirstMove;

    private static final Move NULL_MOVE = new NullMove();

    private Move(final Board board, final Piece piece, final int destination) {
        this.board = board;
        this.piece = piece;
        this.isFirstMove = piece.isFirstMove();
        this.destination = destination;
    }

    private Move(final Board board, final int destination) {
        this.board = board;
        this.piece = null;
        this.isFirstMove = false;
        this.destination = destination;
    }

    public int getDestination() {
        return this.destination;
    }

    public Board getBoard() {
        return this.board;
    }

    public Piece getPiece() {
        return this.piece;
    }

    public Board execute() {
        Builder builder = new Builder();
        for (Piece p : board.getCurrentPlayer().getActivePieces()) {
            if (this.piece.equals(p)) continue;
            builder.setPiece(p);
        }

        for (Piece p : board.getCurrentPlayer().getOpponent().getActivePieces())
            builder.setPiece(p);

        builder.setPiece(piece.movePiece(this));
        builder.setNextMoveMaker(board.getCurrentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public int getCurrentCoordinate() {
        return this.piece.getPiecePosition();
    }

    public boolean isAttack() {return false;}

    public boolean isCastlingMove() {return false;}

    public Piece getAttackedPiece() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return destination == move.destination &&
                Objects.equals(piece, move.piece) && isFirstMove == move.isFirstMove;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, piece, destination, isFirstMove);
    }

    public static final class MajorAttackMove extends AttackMove {

        public MajorAttackMove(Board board, Piece piece, int destination, Piece attackedPiece) {
            super(board, piece, destination, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return this.piece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destination);
        }

    }

    public static final class MajorMove extends Move {
        public MajorMove(final Board board, final Piece piece, final int destination) {
            super(board, piece, destination);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString() {
            return this.piece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destination);
        }

    }

    public static class AttackMove extends Move {

        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece piece, final int destination, final Piece attackedPiece) {
            super(board, piece, destination);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            AttackMove that = (AttackMove) o;
            return super.equals(that) && attackedPiece.equals(that.attackedPiece);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), attackedPiece);
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
    }


    public static final class PawnMove extends Move {
        public PawnMove(final Board board, final Piece piece, final int destination) {
            super(board, piece, destination);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destination);
        }
    }


    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(Board board, Piece piece, int destination, Piece attackedPiece) {
            super(board, piece, destination, attackedPiece);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.piece.getPiecePosition()) + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destination);
        }
    }

    public static final class PawnPromotion extends Move {

        final Move decoratedMove;
        final Pawn promotedPawn;

        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getPiece(), decoratedMove.getDestination());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getPiece();
        }

        @Override
        public Board execute() {
            final Board pawnMoveBoard = this.decoratedMove.execute();
            Builder builder = new Builder();
            for (final Piece p : pawnMoveBoard.getCurrentPlayer().getActivePieces()) {
                if (!this.promotedPawn.equals(p)) builder.setPiece(p);
            }

            for (final Piece p : pawnMoveBoard.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(p);
            }

            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setNextMoveMaker(pawnMoveBoard.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString() {
            return "";
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof PawnPromotion && super.equals(other);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), decoratedMove, promotedPawn);
        }
    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove {
        public PawnEnPassantAttackMove(Board board, Piece piece, int destination, Piece attackedPiece) {
            super(board, piece, destination, attackedPiece);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (Piece p : this.board.getCurrentPlayer().getActivePieces()) {
                if (!this.piece.equals(p)) builder.setPiece(p);
            }

            for (Piece p : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                if (!this.piece.equals(this.attackedPiece)) builder.setPiece(p);
            }

            builder.setPiece(this.piece.movePiece(this));
            builder.setNextMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(destination);
        }
    }

    public static final class PawnJump extends Move {
        public PawnJump(final Board board, final Piece piece, final int destination) {
            super(board, piece, destination);
        }

        @Override
        public Board execute() {
            Builder builder = new Builder();
            Collection<Piece> currPlayerActive = board.getCurrentPlayer().getActivePieces();
            Collection<Piece> opponentActive = board.getCurrentPlayer().getOpponent().getActivePieces();

            for (Piece p : currPlayerActive) {
                if (!p.equals(piece)) builder.setPiece(p);
            }
            for (Piece p :opponentActive)
                builder.setPiece(p);

            Pawn movedPawn = (Pawn) piece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setNextMoveMaker(board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(destination);
        }
    }

    static abstract class CastleMove extends Move {

        protected final Rook rook;
        protected final int rookStart;
        protected final int castleRookDest;


        public CastleMove(final Board board, final Piece piece, final int destination, final Rook rook, final int rookStart, final int castleRookDest) {
            super(board, piece, destination);
            this.rook = rook;
            this.castleRookDest = castleRookDest;
            this.rookStart = rookStart;
        }

        public Rook getCastledRook() {
            return this.rook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            Builder builder = new Builder();
            for (Piece p : this.board.getCurrentPlayer().getActivePieces()) {
                if (!p.equals(piece) && !p.equals(rook)) builder.setPiece(p);
            }
            for (Piece p : this.board.getCurrentPlayer().getOpponent().getActivePieces())
                builder.setPiece(p);

            builder.setPiece(this.piece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDest, this.rook.getPieceAlliance()));
            builder.setNextMoveMaker(board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            CastleMove that = (CastleMove) o;
            return rookStart == that.rookStart &&
                    castleRookDest == that.castleRookDest &&
                    Objects.equals(rook, that.rook);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), rook, rookStart, castleRookDest);
        }
    }

    public static final class KingSideCastleMove extends CastleMove {
        public KingSideCastleMove(Board board, Piece piece, int destination, Rook rook, int rookStart, int castleRookDest) {
            super(board, piece, destination, rook, rookStart, castleRookDest);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
        }

        @Override
        public String toString() {
            return "0-0";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(Board board, Piece piece, int destination, Rook rook, int rookStart, int castleRookDest) {
            super(board, piece, destination, rook, rookStart, castleRookDest);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }

        @Override
        public String toString() {
            return "0-0-0";
        }
    }

    public static final class NullMove extends Move {
        public NullMove() {
            super(null,-1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute null move!");
        }

        @Override
        public int getCurrentCoordinate(){
            return -1;
        }
    }

    public static class MoveFactory {
        private MoveFactory() {
            throw new RuntimeException("Not Instantiable!");
        }

        public static Move createMove(final Board board, final int currCoord, final int destCoord) {
            for (Move m : board.getAllLegalMoves()) {
                if (m.getPiece().getPiecePosition() == currCoord && m.destination == destCoord)
                    return m;
            }
            return NULL_MOVE;
        }
    }

    @Override
    public String toString() {
        return piece.toString() + " " +  this.getPiece().getPiecePosition() + " " + this.getDestination();
    }
}

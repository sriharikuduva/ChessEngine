package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveStatus;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Table extends Observable {
    private final JFrame jFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private Board board;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    private Move computerMove;

    private Tile sourceTile, destTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private boolean highlightLegalMoves;

    private static final String defaultStringImagePath = "art/simple/";
    private static final Dimension DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    private final BoardPanel boardPanel;

    private Color lightTileColor = Color.decode("#FFFACD");
    private Color darkTileColor = Color.decode("#593E1A");

    private static final Table INSTANCE = new Table();

    private Table () {
        this.moveLog = new MoveLog();
        this.jFrame = new JFrame("JChess");
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.board = Board.createStandardBoard();
        this.jFrame.setLayout(new BorderLayout());
        this.jFrame.setJMenuBar(createTableMenuBar());
        this.jFrame.setSize(DIMENSION);
        this.gameSetup = new GameSetup(this.jFrame, true);
        this.boardDirection = BoardDirection.NORMAL;
        this.boardPanel = new BoardPanel();
        this.highlightLegalMoves = false;
        this.jFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.jFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.jFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.jFrame.setVisible(true);
        this.addObserver(new TableGameAIWatcher());
    }

    public static Table get() {
        return INSTANCE;
    }

    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(createFileMenu());
        jMenuBar.add(createPreferencesMenu());
        jMenuBar.add(createOptionsMenu());
        return jMenuBar;
    }

    private JMenu createFileMenu() {
        JMenu menu = new JMenu("File");
        JMenuItem item1 = new JMenuItem("Load PGN File");
        item1.addActionListener(e -> {
            System.out.println("open pgn file");
        });

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> {
            System.exit(0);
        });

        menu.add(item1);
        menu.add(exit);

        return menu;
    }

    private JMenu createPreferencesMenu() {
        JMenu menu = new JMenu("Preferences");
        JMenuItem item1 = new JMenuItem("Flip Board");
        item1.addActionListener(e -> {
            boardDirection = boardDirection.opposite();
            boardPanel.drawBoard(board);
        });

        menu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighter = new JCheckBoxMenuItem("Highlight Legal Moves", false);
        legalMoveHighlighter.addActionListener(e -> highlightLegalMoves = legalMoveHighlighter.isSelected());

        menu.add(item1);
        menu.add(legalMoveHighlighter);
        return menu;
    }

    private JMenu createOptionsMenu() {
        JMenu optionsMenu = new JMenu("Options");
        JMenuItem setUpGameMenuItem = new JMenuItem("Set Up Game");
        setUpGameMenuItem.addActionListener(e -> {
            Table.get().getGameSetup().promptUser();
            Table.get().setupUpdate(Table.get().getGameSetup());
        });

        optionsMenu.add(setUpGameMenuItem);
        return optionsMenu;
    }


    private void setupUpdate(GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    public static void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(Table.get().getBoard(), Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getBoard());
    }

    private static class TableGameAIWatcher implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            if (Table.get().getGameSetup().isAIPlayer(Table.get().getBoard().getCurrentPlayer()) &&
                !Table.get().getBoard().getCurrentPlayer().isInCheckMate() &&
                !Table.get().getBoard().getCurrentPlayer().isInStaleMate()) {

                    final AIThinkTank thinkTank = new AIThinkTank();
                    thinkTank.execute();

            }

            if (Table.get().getBoard().getCurrentPlayer().isInCheckMate()) {
                System.out.println("" + Table.get().getBoard().getCurrentPlayer() + " is in checkmate!");
            }

            if (Table.get().getBoard().getCurrentPlayer().isInStaleMate()) {
                System.out.println("" + Table.get().getBoard().getCurrentPlayer() + " is in stalemate!");
            }
        }
    }

    private void updateGameBoard(Board board) {
        this.board = board;
    }

    private void updateComputerMove(Move move) {
        this.computerMove = move;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private void moveMadeUpdate(PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    private static class AIThinkTank extends SwingWorker<Move, String> {
        private AIThinkTank() { }

        @Override
        protected Move doInBackground() throws Exception {
            final MoveStrategy strategy = new MiniMax(Table.get().getGameSetup().getSearchDepth());
            final Move bestMove = strategy.execute(Table.get().getBoard());
            return bestMove;
        }

        @Override
        public void done() {
            try {
                final Move bestMove = get();

                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getBoard().getCurrentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }

    private Board getBoard() {
        return this.board;
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> tilePanelList;

        public BoardPanel() {
            super(new GridLayout(8,8));
            this.tilePanelList = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.tilePanelList.add(tilePanel);
                add(tilePanel);
            }

            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for (final TilePanel panel : boardDirection.traverse(tilePanelList)) {
                panel.drawTile(board);
                add(panel);
            }
            validate();
            repaint();
        }
    }

    public enum PlayerType {
        HUMAN, COMPUTER;
    }

    private class TilePanel extends JPanel {
        private final int tileId;
        public TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            assignTilePieceIcon(board);
            setPreferredSize(TILE_PANEL_DIMENSION);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        sourceTile = null;
                        destTile = null;
                        humanMovedPiece = null;
                    } else if (SwingUtilities.isLeftMouseButton(e)) {

                        if (sourceTile == null) {
                            // first click
                            sourceTile = board.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            destTile = board.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(board, sourceTile.getTileCoordinate(), destTile.getTileCoordinate());
                            final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                board = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destTile = null;
                            humanMovedPiece = null;
                        }

                        SwingUtilities.invokeLater(() -> {
                            gameHistoryPanel.redo(board, moveLog);
                            takenPiecesPanel.redo(moveLog);

                            if (gameSetup.isAIPlayer(board.getCurrentPlayer())) {
                                Table.get().moveMadeUpdate(PlayerType.HUMAN);
                            }

                            boardPanel.drawBoard(board);
                        });
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });


            assignTileColor();
            validate();
        }

        private void highlightLegalMoves() {
            if (highlightLegalMoves) {
                for (final Move move : pieceLegalMoves(board)) {

                    MoveTransition transition = board.getCurrentPlayer().makeMove(move);
                    if (transition.getMoveStatus() != MoveStatus.DONE) continue;

                    if (move.getDestination() == this.tileId) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.getCurrentPlayer().getAlliance())
                return humanMovedPiece.calculateLegalMoves(board);
            return Collections.emptyList();
        }

        private void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegalMoves();
            validate();
            repaint();
        }

        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTile(this.tileId).isTileOccupied()) {

                try {
                    String fileName = defaultStringImagePath
                            + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0, 1)
                            + board.getTile(this.tileId).getPiece().toString()
                            + ".gif";
                    final BufferedImage img = ImageIO.read(new File(fileName));
                    this.add(new JLabel(new ImageIcon(img)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            if (BoardUtils.EIGHTH_RANK[this.tileId] ||
                    BoardUtils.SIXTH_RANK[this.tileId] ||
                    BoardUtils.FOURTH_RANK[this.tileId] ||
                    BoardUtils.SECOND_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if(BoardUtils.SEVENTH_RANK[this.tileId] ||
                    BoardUtils.FIFTH_RANK[this.tileId] ||
                    BoardUtils.THIRD_RANK[this.tileId]  ||
                    BoardUtils.FIRST_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }

    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return BoardDirection.FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return BoardDirection.NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    public static class MoveLog{
        private final List<Move> moves;
        public MoveLog() {
            this.moves = new ArrayList<>();
        }

        public void addMove(Move m) {
            this.moves.add(m);
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int idx) {
            return this.moves.remove(idx);
        }

        public boolean removeMove(Move m) {
            return this.moves.remove(m);
        }
    }
}

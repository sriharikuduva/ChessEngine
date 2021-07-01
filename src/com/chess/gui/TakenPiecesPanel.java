package com.chess.gui;

import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static com.chess.gui.Table.*;

public class TakenPiecesPanel extends JPanel {

    private final JPanel northPanel, southPanel;
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOR = Color.decode("0xFDFE6");
    private static final String defaultStringImagePath = "art/simple/";
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);


    public TakenPiecesPanel() {
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 2));

        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);

        add(northPanel, BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);

        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void redo(final MoveLog log) {
        southPanel.removeAll();
        northPanel.removeAll();

        final List<Piece> whiteTaken = new ArrayList<>(), blackTaken = new ArrayList<>();

        for (Move m : log.getMoves()) {
            if (m.isAttack()) {
                Piece takenPiece = m.getAttackedPiece();
                if (takenPiece.getPieceAlliance().isWhite()) {
                    whiteTaken.add(takenPiece);
                } else {
                    blackTaken.add(takenPiece);
                }
            }
        }

        Collections.sort(whiteTaken, (o1, o2) -> Ints.compare(o1.getPieceValue(), o2.getPieceValue()));
        Collections.sort(blackTaken, (o1, o2) -> Ints.compare(o1.getPieceValue(), o2.getPieceValue()));

        populate(whiteTaken, southPanel);
        populate(blackTaken, northPanel);
    }

    private void populate(Collection<Piece> taken, JPanel panel) {
        for (final Piece takenPiece : taken) {
            try {
                String filePath = defaultStringImagePath
                        + takenPiece.getPieceAlliance().toString().substring(0, 1)
                        + takenPiece.toString()
                        + ".gif";
                final BufferedImage img = ImageIO.read(new File(filePath));
                final ImageIcon icon = new ImageIcon(img);
                final JLabel imageLabel = new JLabel(icon);
                panel.add(imageLabel);
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        validate();
    }


}

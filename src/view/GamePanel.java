package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import model.Block;
import model.BoardInterface;
import model.MovableTetrisPiece;
import model.Point;

/**
 * Creates the game portion of the Tetris GUI.
 * @author braggs03
 * @author daltonmilti
 * @author KhobitheCode
 * @author chriseetwo
 * @version Autumn 2023
 */
public final class GamePanel extends JPanel implements PropertyChangeListener {

    /**
     * A set size for the width and height of a block.
     */
    public static final int SQUARE_SIZE = 40;

    /**
     * A different color red to distinguish the S block from the background.
     */
    public static final Color OTHER_RED = new Color(80, 0, 0);

    /**
     * The stroke width in pixels.
     */
    private static final int STROKE_WIDTH = 2;

    /**
     * The width for the rectangle.
     */
    private static final int RECTANGLE_WIDTH = 50;

    /**
     * The height for the rectangle.
     */
    private static final int RECTANGLE_HEIGHT = 50;

    /**
     * The current piece.
     */
    private MovableTetrisPiece myCurrentPiece;

    /**
     * The ghost piece.
     */
    private MovableTetrisPiece myGhostPiece;

    /**
    * The frozen blocks on the board.
     */
    private List<Block[]> myFrozenBlocks;

    /**
     * Whether or not the game is paused.
     */
    private boolean myGamePaused;

    /**
     * Creates the Tetris game panel for the TetrisGUI.
     */
    public GamePanel() {
        super();
        this.setPreferredSize(new Dimension(TetrisGUI.SIZE / 2, TetrisGUI.SIZE));
        this.setBackground(Color.BLACK);

    }

    @Override
    protected void paintComponent(final Graphics theG) {
        super.paintComponent(theG);
        final Graphics2D g2d = (Graphics2D) theG;

        if (myGhostPiece != null) {
            final Point[] p = myGhostPiece.getBoardPoints();
            for (final Point k : p) {
                g2d.setPaint(Color.BLACK);
                g2d.fillRect(k.x() * GamePanel.SQUARE_SIZE,
                        (this.getHeight() - SQUARE_SIZE) - k.y() * GamePanel.SQUARE_SIZE,
                        GamePanel.SQUARE_SIZE + 1, GamePanel.SQUARE_SIZE + 1);
                g2d.setPaint(Color.LIGHT_GRAY);
                g2d.fillRect(k.x() * GamePanel.SQUARE_SIZE + 1,
                        (this.getHeight() - SQUARE_SIZE) - k.y() * GamePanel.SQUARE_SIZE + 1,
                        GamePanel.SQUARE_SIZE - 1, GamePanel.SQUARE_SIZE - 1);
            }
        }

        if (myCurrentPiece != null) {
            final Point[] i = myCurrentPiece.getBoardPoints();
            for (final Point k : i) {
                g2d.setPaint(Color.BLACK);
                g2d.fillRect(k.x() * GamePanel.SQUARE_SIZE,
                        (this.getHeight() - SQUARE_SIZE) - k.y() * GamePanel.SQUARE_SIZE,
                        GamePanel.SQUARE_SIZE + 1, GamePanel.SQUARE_SIZE + 1);
                g2d.setPaint(TetrisPieceColors.getColor(myCurrentPiece));
                g2d.fillRect(k.x() * GamePanel.SQUARE_SIZE + 1,
                        (this.getHeight() - SQUARE_SIZE) - k.y() * GamePanel.SQUARE_SIZE + 1,
                        GamePanel.SQUARE_SIZE - 1, GamePanel.SQUARE_SIZE - 1);
            }
        }
        if (myFrozenBlocks != null) {
            for (int i = 0; i < myFrozenBlocks.size(); i++) {
                for (int k = 0; k < myFrozenBlocks.get(0).length; k++) {
                    paintBlock(myFrozenBlocks.get(i)[k], g2d, i, k);
                }
            }
        }
        if (myGamePaused) {
            g2d.setPaint(Color.BLACK);
            g2d.setFont(new Font("TimesRoman", Font.ROMAN_BASELINE, 70));
            g2d.setPaint(Color.WHITE);
            g2d.drawString("PAUSED", 55, 415);

        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void propertyChange(final PropertyChangeEvent theEvent) {
        if (BoardInterface.CURRENT_PIECE_CHANGING.equals(theEvent.getPropertyName())) {
            myCurrentPiece = (MovableTetrisPiece) theEvent.getNewValue();
            repaint();
        } else if (BoardInterface.FROZEN_CHANGING.equals(theEvent.getPropertyName())) {
            myFrozenBlocks = (LinkedList<Block[]>) theEvent.getNewValue();
            if (myGhostPiece != null) {
                final Point[] p = myGhostPiece.getBoardPoints();
                for (final Point k : p) {
                    if (myFrozenBlocks.get(k.y())[k.x()] == null) {
                        myGhostPiece = null;
                        break;
                    }
                }
            }
            repaint();
        } else if (BoardInterface.GAME_PAUSED.equals(theEvent.getPropertyName())) {
            myGamePaused = (boolean) theEvent.getNewValue();
            repaint();
        } else if (BoardInterface.GHOST_PIECE_CHANGING.equals(theEvent.getPropertyName())) {
            myGhostPiece = (MovableTetrisPiece) theEvent.getNewValue();
            repaint();
        }
    }

    private void paintBlock(final Block theBlock, final Graphics2D theG2d,
                            final int theX, final int theY) {
        if (theBlock != null) {
            theG2d.setPaint(Color.BLACK);
            theG2d.fillRect(theY * GamePanel.SQUARE_SIZE,
                    (this.getHeight() - SQUARE_SIZE) - theX * GamePanel.SQUARE_SIZE,
                    GamePanel.SQUARE_SIZE + 1, GamePanel.SQUARE_SIZE + 1);
            theG2d.setPaint(TetrisPieceColors.getColor(theBlock));
            theG2d.fillRect(theY * GamePanel.SQUARE_SIZE + 1,
                    (this.getHeight() - SQUARE_SIZE) - theX * GamePanel.SQUARE_SIZE + 1,
                    GamePanel.SQUARE_SIZE - 1, GamePanel.SQUARE_SIZE - 1);

        }

    }

}
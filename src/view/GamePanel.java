package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JPanel;

import model.Board;
import model.MovableTetrisPiece;
import model.Point;
import model.TetrisPiece;

import static model.PropertyChangeBoard.*;
import static view.TetrisGUI.BOARD;

/**
 * Creates the game portion of the Tetris GUI.
 * @author braggs03
 * @author daltonmilti
 * @author KhobitheCode
 * @author chriseetwo
 * @version Autumn 2023
 */
public final class GamePanel extends JPanel implements PropertyChangeListener {

    /** A set size for the width and height of a block. */
    public static final int SQUARE_SIZE = 40;

    /** A different color orange. */
    public static final Color ORANGE = new Color(255, 165, 0);

    /**
     * A different color red to distinguish the S block from the background.
     */
    public static final Color OTHER_RED = new Color(80, 0, 0);

    /** Used for debugging */
    private static int cnt;

    /** The stroke width in pixels. */
    private static final int STROKE_WIDTH = 2;

    /** The width for the rectangle. */
    private static final int RECTANGLE_WIDTH = 50;

    /** The height for the rectangle. */
    private static final int RECTANGLE_HEIGHT = 50;

    /** The current piece. */
    private MovableTetrisPiece myCurrentPiece;

    /**
     *
     */
    final TetrisPiece[] myTetrisPieces = TetrisPiece.values();

    /**
     *
     */
    final Color[] myPieceColors = {Color.CYAN, ORANGE, Color.BLUE, Color.YELLOW,
            OTHER_RED, Color.MAGENTA, Color.GREEN};


    public GamePanel() {
        super();
        if (cnt > 0) {
            throw new IllegalStateException();
        }
        cnt++;
        this.setPreferredSize(new Dimension(TetrisGUI.SIZE / 2, TetrisGUI.SIZE));
        this.setBackground(Color.RED);

        addPropertyChangeListener(this);

        //I'm 99% we don't want to do this
        //myCurrentPiece = TetrisPiece.getRandomPiece();
    }

    @Override
    protected void paintComponent(final Graphics theG) {
        super.paintComponent(theG);
        final Graphics2D g2d = (Graphics2D) theG;

        if (myCurrentPiece != null) {
            int index = -1;
            for (int i = 0; i < myTetrisPieces.length; i++) {
                if (myTetrisPieces[i] == myCurrentPiece) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                final Color color = myPieceColors[index];
                for (final Point p : myCurrentPiece.getPoints()) {
                    makeBlock(g2d, p.x(), p.y(), color);
                }
            }
        }

        g2d.setPaint(Color.BLACK);
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 20; col++) {
                g2d.drawRect(row * SQUARE_SIZE, col * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        //        for (int i = 0; i < allTetrisPieces.length; i++) {
//            final Point[] k = allTetrisPieces[i].getPoints();
//            for (final Point p : k) {
//                makeBlock(g2d, p.x(), p.y(), allColors[i], spacing[i]);
//            }
//        }

    }
    private void makeBlock(final Graphics2D theG2D, final int theX, final int theY,
                           final Color theColor) {
        final int x = theX * SQUARE_SIZE;
        final int y = theY * SQUARE_SIZE;
        theG2D.setPaint(Color.BLACK);
        theG2D.fillRect(x, y, SQUARE_SIZE + 1, SQUARE_SIZE + 1);
        theG2D.setPaint(theColor);
        theG2D.fillRect(x + 1, y + 1, SQUARE_SIZE - 1, SQUARE_SIZE - 1);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent theEvent) {
        switch (theEvent.getPropertyName()) {
            case CURRENT_PIECE_CHANGING:
                handlePieceChanging((MovableTetrisPiece) theEvent.getNewValue());
            case ROW_CHANGE:
                handleCompleteRows((ArrayList<Integer>) theEvent.getNewValue());
        }
        repaint();
    }

    private void handleGameStarting(Boolean theNewGameStarting) {
        //do something here
        BOARD.newGame();
    }

    private void handleGameOver(Boolean theGameIsEnding) {
        //do something here
        //BOARD.myGameOver = true;
        //i know its private i just write it like this as a reminder
    }

    private void handlePieceChanging(MovableTetrisPiece theChangingPiece) {
        myCurrentPiece = theChangingPiece;
    }

    private void handleCompleteRows(ArrayList<Integer> theCompleteRows) {
        //do something there in Sprint 3
    }
}

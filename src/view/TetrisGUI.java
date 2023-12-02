package view;

import model.Board;
import model.BoardInterface;
import model.PropertyChangeBoard;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

/**
 * Creates a Tetris GUI.
 *
 * @author braggs03
 * @author daltonmilti
 * @author KhobitheCode
 * @author chriseetwo
 * @version Autumn 2023
 */
public final class TetrisGUI implements PropertyChangeListener {

    /**
     * The main board used for the Tetris project.
     */
    public static final BoardInterface BOARD = new Board();

    /** Used to update step() the board after some time. */
    private static final Timer TIMER = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent theE) {
            BOARD.step();
        }
    });

    /** Constanst used to size screen. */
    public static final int SIZE = 800;

    /**
     * Used for debugging, if value is ever greater than one, an exception is thrown.
     */
    private static int cnt;

    /** The Tetris Frame. */
    private JFrame myWindow;

    /**
     * The Tetris Menu Bar.
     */
    private JMenuBar myMenuBar;

    /**
     * The Tetris Main Panel.
     */
    private JPanel myMainPanel;

    /**
     * The Tetris Right Panel. Used to organize myNextPiecePanel and myInfoPanel.
     */
    private JPanel myRightPanel;

    /**
     * The Tetris Game Panel.
     */
    private JPanel myGamePanel;

    /**
     * The Tetris Next Piece Panel.
     */
    private JPanel myNextPiecePanel;

    /**
     * The Tetris Info Panel.
     */
    private JPanel myInfoPanel;

    private TetrisGUI() {
        super();
        if (cnt > 0) {
            throw new IllegalStateException();
        }
        buildComponents();
        layoutComponents();
    }

    /**
     * Creates a Tetris GUI.
     */
    public static void createAndShowGUI() {
        final TetrisGUI l = new TetrisGUI();
    }
    private void buildComponents() {
        //Frame
        myWindow = new JFrame("Tetris - Group 9");

        //Menu Bar
        myMenuBar = new TetrisMenuBar();

        //MainPanel
        myMainPanel = new JPanel();

        //RightPanel
        myRightPanel = new JPanel();

        //GamePanel
        myGamePanel = new GamePanel();

        //NextPanel
        myNextPiecePanel = new NextPiecePanel();

        //InfoPanel
        myInfoPanel = new InfoPanel();

    }
    private void layoutComponents() {

        //Right Panel
        myRightPanel.setPreferredSize(
                new Dimension(SIZE / 2, SIZE));
        myRightPanel.setLayout(new GridLayout(0, 1, 0, 0));
        myRightPanel.add(myNextPiecePanel);
        myRightPanel.add(myInfoPanel);

        //Main Panel
        myMainPanel.setPreferredSize(new Dimension(SIZE, SIZE));
        myMainPanel.setLayout(new GridLayout(1, 0, 0, 0));
        myMainPanel.add(myGamePanel);
        myMainPanel.add(myRightPanel);
        myWindow.addKeyListener(new MyKeyAdapter());

        //Window
        myWindow.setJMenuBar(myMenuBar);
        myWindow.add(myMainPanel);
        myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myWindow.setContentPane(myMainPanel);
        myWindow.setResizable(true);
        myWindow.pack();
        myWindow.setVisible(true);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent theEvt) {
        if (PropertyChangeBoard.GAME_STARTING.equals(theEvt.getPropertyName())) {
            gameStart();
        }
    }

    private void gameStart() {
        TIMER.start();
        BOARD.newGame();
    }

    private static final class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyReleased(final KeyEvent theE) {
            if (theE.getKeyCode() == KeyEvent.VK_A || theE.getKeyCode() == KeyEvent.VK_LEFT) {
                BOARD.left();
            } else if (theE.getKeyCode() == KeyEvent.VK_D
                       || theE.getKeyCode() == KeyEvent.VK_RIGHT) {
                BOARD.right();
            } else if (theE.getKeyCode() == KeyEvent.VK_S
                       || theE.getKeyCode() == KeyEvent.VK_DOWN) {
                BOARD.down();
            } else if (theE.getKeyCode() == KeyEvent.VK_W
                       || theE.getKeyCode() == KeyEvent.VK_UP) {
                BOARD.rotateCW();
            }
        }
    }
}



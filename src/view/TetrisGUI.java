package view;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import model.Board;
import model.BoardInterface;

/**
 * Creates a Tetris GUI.
 *
 * @author braggs03
 * @author daltonmilti
 * @author KhobitheCode
 * @author chriseetwo
 * @version Autumn 2023
 */

//All fields present are needed.
//There is not a great way to reduce fields
//count while retaining an error free environment.
@SuppressWarnings("ClassWithTooManyFields")
public final class TetrisGUI implements PropertyChangeListener, PropertyChangeMethods {

    /**
     * The main board used for the Tetris project.
     */
    public static final BoardInterface BOARD = new Board();

    /** Constant used to size screen. */
    public static final int SIZE = 800;

    /**
     * Base delay on timer.
     */
    private static final int BASE_SPEED = 1000;

    /**
     * Music volume.
     */
    private static final float MUSIC_VOLUME = -15.00f;

    /**
     * Pause music volume.
     */
    private static final float PAUSED_MUSIC_VOLUME = -30.00f;

    /**
     * Explosion volume.
     */
    private static final float EXPLOSION_VOLUME = -30.00f;

    /** Used to update step() the board after some time. */
    private static final Timer TIMER = new Timer(BASE_SPEED, theE -> BOARD.step());

    /**
     * Represents constant used for Speed Slider
     */
    private static final int SLIDER_CONSTANT = 50;

    /**
     * Represents constant used for Timer Speed
     */
    private static final int SPEED_CONSTANT = 1000;

    /**
     * Represents constant used for One Half Timer Speed
     */
    private static final int ONE_HALF_CONSTSNT = 750;

    /** PropertyChangeSupport for all listeners */
    private final PropertyChangeSupport myPcs;

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

    /**
     * Whether or not the board
     */
    private boolean myGameStarted;

    /**
     * Whether or not the game is paused.
     */
    private boolean myGamePause;

    /**
     * The current Base Speed of Timer
     */
    private int mySpeed;

    /**
     * The Clip used to play music.
     */
    private Clip myClip;

    private TetrisGUI() {
        super();
        myPcs = new PropertyChangeSupport(this);
        myGameStarted = false;
        buildComponents();
        layoutComponents();
        addListenersAndPropertyChangeListeners();
        mySpeed = BASE_SPEED;
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

        //Window
        myWindow.setLayout(new GridLayout(1, 0, 0, 0));
        myWindow.setJMenuBar(myMenuBar);
        myWindow.add(myMainPanel);
        myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myWindow.addFocusListener(new MyFocusListener());
        myWindow.setContentPane(myMainPanel);
        myWindow.setResizable(false);
        myWindow.pack();
        myWindow.setVisible(true);
        myWindow.requestFocus();
        myWindow.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/assets/images/favicon.png"))).getImage());
    }

    private void addListenersAndPropertyChangeListeners() {
        myWindow.addKeyListener(new MyKeyAdapter());
        myMenuBar.addPropertyChangeListener(this);
        BOARD.addPropertyChangeListener(this);
        BOARD.addPropertyChangeListener((PropertyChangeListener) myGamePanel);
        BOARD.addPropertyChangeListener((PropertyChangeListener) myNextPiecePanel);
        BOARD.addPropertyChangeListener((PropertyChangeListener) myInfoPanel);
        myInfoPanel.addPropertyChangeListener(this);
        myMenuBar.addPropertyChangeListener((PropertyChangeListener) myGamePanel);
        this.addPropertyChangeListener((PropertyChangeListener) myGamePanel);
        this.addPropertyChangeListener((PropertyChangeListener) myInfoPanel);

    }

    private void gameStart() {
        TIMER.start();
        BOARD.newGame();
        playMusic();
        myGameStarted = true;
    }

    private void pause() {
        myGamePause = !myGamePause;
        if (myGamePause) {
            TIMER.stop();
        } else {
            TIMER.start();
        }
        pauseMusic();
        myPcs.firePropertyChange(GAME_PAUSED, !myGamePause, myGamePause);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent theEvt) {
        if (GAME_STARTING.equals(theEvt.getPropertyName()) && !myGameStarted) {
            gameStart();
            myPcs.firePropertyChange(INFO_RESET, null, true);
        } else if (GAME_END.equals(theEvt.getPropertyName())
                   && (boolean) theEvt.getNewValue() && myGameStarted) {
            myClip.close();
            myGameStarted = false;
            playGameOver();
        } else if (LEVEL_CHANGING.equals(theEvt.getPropertyName())) {
            TIMER.setDelay((int) (mySpeed / Math.log((int) theEvt.getNewValue() + 1)));
            playLevelUp();
        } else if (SPEED_CHANGING.equals(theEvt.getPropertyName())) {
            changeSpeed((int) theEvt.getNewValue());
        }
    }

    /**
     * game end sounds
     */
    private void playGameOver() {
        final String filePath = "/assets/sound/charles_bad_1.wav";
        try {
            final URL url = Objects.requireNonNull(this.getClass().getResource(filePath));
            final AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            final Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (final UnsupportedAudioFileException | IOException
                       | LineUnavailableException ignored) {
        }
    }

    /**
     * level up sound method
     */
    private void playLevelUp() {
        final Random r = new Random();
        final String filePath;
        final int randomInt = r.nextInt(3);
        final Clip clip;
        if (randomInt == 0) {
            filePath = "/assets/sound/charles_yes_1.wav";
        } else if (randomInt == 1) {
            filePath = "/assets/sound/charles_yes_2.wav";
        } else {
            filePath = "/assets/sound/charles_yes_3.wav";
        }

        try {
            final URL url = Objects.requireNonNull(this.getClass().getResource(filePath));
            final AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (final UnsupportedAudioFileException | IOException
                       | LineUnavailableException ignored) {
        }
    }

    /**
     * for playing music
     */
    private void playMusic() {
        try {
            final URL url = Objects.requireNonNull
                            (this.getClass().getResource("/assets/sound/tetris.wav"));
            final AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            myClip = AudioSystem.getClip();
            myClip.open(audioIn);
            final FloatControl gainControl =
                    (FloatControl) myClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(MUSIC_VOLUME);
            myClip.start();
            myClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music continuously.
        } catch (final UnsupportedAudioFileException | IOException
                       | LineUnavailableException ignored) {
        }
    }

    /**
     * pause music
     */
    private void pauseMusic() {
        final FloatControl gainControl =
                (FloatControl) myClip.getControl(FloatControl.Type.MASTER_GAIN);
        final float volume;
        if (myGamePause) {
            volume = PAUSED_MUSIC_VOLUME;
        } else {
            volume = MUSIC_VOLUME;
        }
        gainControl.setValue(volume);
    }

    private void drop() {
        BOARD.drop();
        try {
            final URL url = Objects.requireNonNull(
                            this.getClass().getResource("/assets/sound/explosion.wav"));
            final AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            final Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            final FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(EXPLOSION_VOLUME);
            clip.start();
        } catch (final UnsupportedAudioFileException | IOException
                       | LineUnavailableException ignored) {
        }
    }

    private void changeSpeed(final int theSpeed) {
        switch (theSpeed) {
            case SLIDER_CONSTANT:
                mySpeed = SPEED_CONSTANT * 2;
                TIMER.setDelay(mySpeed);
                break;
            case SLIDER_CONSTANT * 2:
                mySpeed = SPEED_CONSTANT;
                TIMER.setDelay(mySpeed);
                break;
            case SLIDER_CONSTANT + SLIDER_CONSTANT * 2:
                mySpeed = ONE_HALF_CONSTSNT;
                TIMER.setDelay(mySpeed);
                break;
            case SLIDER_CONSTANT * 2 + SLIDER_CONSTANT * 2:
                mySpeed = SPEED_CONSTANT / 2;
                TIMER.setDelay(mySpeed);
                break;
            default:
                mySpeed = BASE_SPEED;
        }
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener theListener) {
        myPcs.addPropertyChangeListener(theListener);
    }

    @Override
    public void addPropertyChangeListener(final String thePropertyName,
                                          final PropertyChangeListener theListener) {
        myPcs.addPropertyChangeListener(thePropertyName, theListener);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener theListener) {
        myPcs.removePropertyChangeListener(theListener);
    }

    @Override
    public void removePropertyChangeListener(final String thePropertyName,
                                             final PropertyChangeListener theListener) {
        myPcs.removePropertyChangeListener(thePropertyName, theListener);
    }

    private final class MyKeyAdapter extends KeyAdapter {

        /** Used to store all Keybinds. */
        @SuppressWarnings("All")
        private final HashMap<Integer, Runnable> myKeys = new HashMap<>();

        //Original suppresion warning was "checkstyle:ExecutableStatementCount"
        //Error didn't go away. Suppression is needed as all keysbinds are a
        //Requirement for the project.
        @SuppressWarnings("All")
        MyKeyAdapter() {
            super();
            myKeys.put(KeyEvent.VK_UP, BOARD::rotateCW);
            myKeys.put(KeyEvent.VK_W, BOARD::rotateCW);
            myKeys.put(KeyEvent.VK_DOWN, BOARD::down);
            myKeys.put(KeyEvent.VK_S, BOARD::down);
            myKeys.put(KeyEvent.VK_LEFT, BOARD::left);
            myKeys.put(KeyEvent.VK_A, BOARD::left);
            myKeys.put(KeyEvent.VK_RIGHT, BOARD::right);
            myKeys.put(KeyEvent.VK_D, BOARD::right);
            myKeys.put(KeyEvent.VK_SPACE, TetrisGUI.this::drop);
            myKeys.put(KeyEvent.VK_P, TetrisGUI.this::pause);
        }

        @Override
        public void keyPressed(final KeyEvent theE) {
            if (TetrisGUI.this.myGameStarted && (!TetrisGUI.this.myGamePause
                || theE.getKeyCode() == KeyEvent.VK_P)
                && myKeys.containsKey(theE.getKeyCode())) {
                myKeys.get(theE.getKeyCode()).run();
            }
        }
    }
    private final class MyFocusListener implements FocusListener {
        @Override
        public void focusGained(final FocusEvent theE) {
            if (myGameStarted && myGamePause) {
                pause();
            }
        }

        @Override
        public void focusLost(final FocusEvent theE) {
            if (myGameStarted && !myGamePause) {
                pause();
            }
        }
    }
}



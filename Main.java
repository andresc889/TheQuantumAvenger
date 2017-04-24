
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class Main extends JFrame implements KeyListener, ScreenListener {

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    private IntroductionPanel introductionPanel;
    private GamePanel gamePanel;
    private ResultScreen losingScreen;
    private ResultScreen winningScreen;
    private static GameData gameData;
    private Animator animator;

    public Main() {
        // Center on screen: http://stackoverflow.com/questions/2442599/how-to-set-jframe-to-appear-centered-regardless-of-the-monitor-resolution
        Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenDimensions.width - WINDOW_WIDTH) / 2, (screenDimensions.height - WINDOW_HEIGHT) / 2);
        
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);

        Container c = getContentPane();

        introductionPanel = new IntroductionPanel();
        introductionPanel.setHandler(this);
        introductionPanel.setFocusable(false);

        c.add(introductionPanel, "Center");
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_UP:
                gameData.setKeyUpOn(true);
                break;
            case KeyEvent.VK_LEFT:
                gameData.setKeyLeftOn(true);
                break;
            case KeyEvent.VK_RIGHT:
                gameData.setKeyRightOn(true);
                break;
            case KeyEvent.VK_SPACE:
                gameData.setShooterOn(true);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_UP:
                gameData.setKeyUpOn(false);
                break;
            case KeyEvent.VK_LEFT:
                gameData.setKeyLeftOn(false);
                break;
            case KeyEvent.VK_RIGHT:
                gameData.setKeyRightOn(false);
                break;
            case KeyEvent.VK_SPACE:
                gameData.setShooterOn(false);
                break;
        }
    }

    public static void main(String[] args) {
        JFrame game = new Main();
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setTitle("The Quantum Avenger");
        game.setVisible(true);
    }

    @Override
    public void processScreenEvent(ScreenEvent event) {
        getContentPane().remove(event.getSource());
        
        if (event.getType() == ScreenEvent.START_EASY_GAME) {
            startGame(GameData.DIFFICULTY_EASY);
        } else if (event.getType() == ScreenEvent.START_NORMAL_GAME) {
            startGame(GameData.DIFFICULTY_NORMAL);
        } else if (event.getType() == ScreenEvent.START_HARD_GAME) {
            startGame(GameData.DIFFICULTY_HARD);
        } else if (event.getType() == ScreenEvent.SHOW_WINNING_SCREEN) {
            winningScreen = new WinningScreen(gameData);
            winningScreen.setHandler(this);
            winningScreen.setFocusable(false);
            
            getContentPane().add(winningScreen);
            removeKeyListener(this);
        } else if (event.getType() == ScreenEvent.SHOW_LOSING_SCREEN) {
            losingScreen = new LosingScreen(gameData);
            losingScreen.setHandler(this);
            losingScreen.setFocusable(false);

            getContentPane().add(losingScreen);
            removeKeyListener(this);
        } else if (event.getType() == ScreenEvent.SHOW_MENU) {
            getContentPane().add(introductionPanel);
            removeKeyListener(this);
        } else if (event.getType() == ScreenEvent.EXIT) {
            System.exit(0);
        }
        
        getContentPane().revalidate();
        getContentPane().repaint();
    }
    
    private void startGame(int difficulty) {
        animator = new Animator();
        
        gameData = new GameData();
        gameData.setDifficulty(difficulty);
        
        gamePanel = new GamePanel(animator, gameData);
        gamePanel.setHandler(this);
        gamePanel.setFocusable(false);
        
        animator.setGamePanel(gamePanel);
        animator.setGameData(gameData);
        
        addKeyListener(this);
        getContentPane().add(gamePanel);
    }
}

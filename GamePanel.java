
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GamePanel extends JPanel implements ActionListener, ComponentListener, Screen {
    
    public static int GAME_AREA_WIDTH = Main.WINDOW_WIDTH;
    public static int GAME_AREA_HEIGHT = Main.WINDOW_HEIGHT - 20;
    private Animator animator;
    private Thread animatorThread;
    private GameData gameData;
    private JPanel gameArea;
    JLabel instructionsLabel;
    private JLabel scoreLabel;
    private LifeGauge lifeGauge;
    private FancyButton startPauseButton;
    private FancyButton exitButton;
    // off screen rendering
    private Graphics graphics;
    private Image dbImage;
    private boolean dbImageUpdate;
    private ScreenListener handler;
    
    public GamePanel(Animator animator, GameData gameData) {
        setLayout(new BorderLayout());

        this.animator = animator;
        this.animator.running = false;

        animatorThread = null;

        this.gameData = gameData;

        gameArea = new JPanel();
        gameArea.setLayout(new BorderLayout());
        gameArea.setBackground(Color.BLACK);
        gameArea.addComponentListener(this);
        add(gameArea, BorderLayout.CENTER);

        // Custom font: see http://stackoverflow.com/questions/17539827/ttf-font-with-java-awt-graphics
        Font customFont;
        
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("resources/spaceranger.ttf"));
        } catch (Exception e) {
            customFont = null;
        }
        
        // Multi-line label: http://stackoverflow.com/questions/685521/multiline-text-in-jlabel
        String instructions = "";
        
        instructions += "<html>";
        instructions += "Avenger:<br>";
        instructions += "<br>";
        instructions += "Your mission is to destroy all <u>" + GameData.NUM_OF_FREE_PARTICLES + "</u> spheres that<br>";
        instructions += "threaten the quantum world. Be careful! Their<br>";
        instructions += "motion can be a little... Unpredictable.<br>";
        instructions += "<br>";
        instructions += "Use the <u>up</u> arrow in your keyboard to propel<br>";
        instructions += "your ship. Change its direction using the <u>left</u><br>";
        instructions += "and <u>right</u> arrows. Use the <u>space</u> bar to shoot.<br>";
        instructions += "<br>";
        instructions += "Good luck!<br>";
        instructions += "<br>";
        instructions += "<br>";
        instructions += "The Quantum Chief<br>";
        instructions += "<br>";
        instructions += "<u>P.S.</u> The color of your ship's tip matters.<br>";
        instructions += "</html>";
        
        instructionsLabel = new JLabel(instructions);
        instructionsLabel.setHorizontalAlignment(JLabel.CENTER);
        instructionsLabel.setForeground(new Color(230, 230, 230));
        
        if (customFont != null) {
            Font instructionsCustomFont = customFont.deriveFont(26f);
                    
            // Register font: http://stackoverflow.com/questions/8423007/java-swing-jlabel-html-and-custom-fonts
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(instructionsCustomFont);
            
            instructionsLabel.setFont(instructionsCustomFont);
        }
        
        gameArea.add(instructionsLabel, BorderLayout.CENTER);
        
        JPanel navigationPanel = new JPanel(new GridLayout(1, 4));
        navigationPanel.setBackground(Color.BLACK);
        add(navigationPanel, BorderLayout.SOUTH);

        scoreLabel = new JLabel(gameData.getScoreAsString());
        scoreLabel.setBorder(new EmptyBorder(5 , 5, 5, 5));
        scoreLabel.setForeground(Color.WHITE) ;
        
        if (customFont != null) {
            scoreLabel.setFont(customFont.deriveFont(36f));
        }
        
        navigationPanel.add(scoreLabel);

        lifeGauge = new LifeGauge(1);
        lifeGauge.setForeground(Color.WHITE);
        
        if (customFont != null) {
            lifeGauge.setFont(customFont.deriveFont(36f));
        }
        
        navigationPanel.add(lifeGauge);
        
        startPauseButton = new FancyButton("Start");
        startPauseButton.setFocusable(false);
        startPauseButton.addActionListener(this);
        navigationPanel.add(startPauseButton);

        exitButton = new FancyButton("Exit");
        exitButton.setFocusable(false);
        exitButton.addActionListener(this);
        navigationPanel.add(exitButton);

        dbImage = null;
        dbImageUpdate = true;
        handler = null;
    }

    public void gameRender() {
        if (dbImage == null || dbImageUpdate) {
            dbImage = createImage(GAME_AREA_WIDTH, GAME_AREA_HEIGHT);

            dbImageUpdate = false;

            if (dbImage == null) {
                System.out.println("dbImage is null");
                return;
            } else {
                graphics = dbImage.getGraphics();
                
                // Anti-alias: http://www.java2s.com/Code/Java/2D-Graphics-GUI/RadialGradient.htm
                ((Graphics2D)graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
        }

        // Check if user has won
        if (gameData.isWin()) {
            if (handler != null) {
                // Stop animator
                animator.running = false;

                try {
                    animatorThread.join(100);
                } catch (Exception exc) {
                    System.exit(1);
                }

                animatorThread = null;
                
                // Fire event
                handler.processScreenEvent(new ScreenEvent(this, ScreenEvent.SHOW_WINNING_SCREEN));
            }
        }
        
        // Check if user has lost
        if (gameData.isLoss()) {
            if (handler != null) {
                // Stop animator
                animator.running = false;

                try {
                    animatorThread.join(100);
                } catch (Exception exc) {
                    System.exit(1);
                }

                animatorThread = null;
                
                // Fire event
                handler.processScreenEvent(new ScreenEvent(this, ScreenEvent.SHOW_LOSING_SCREEN));
            }
        }
        
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, GAME_AREA_WIDTH, GAME_AREA_HEIGHT);

        synchronized (gameData.figures) {
            GameFigure f;
            for (int i = 0; i < gameData.figures.size(); i++) {
                f = (GameFigure) gameData.figures.get(i);
                f.render(graphics);
            }
        }
        
        lifeGauge.setRatio(gameData.getLifeRemainingRatio());
        scoreLabel.setText(gameData.getScoreAsString());
    }

    public void printScreen() { // use active rendering to put the buffered image on-screen
        Graphics g;
        try {
            g = gameArea.getGraphics();
            if ((g != null) && (dbImage != null)) {
                g.drawImage(dbImage, 0, 0, null);
            }
            Toolkit.getDefaultToolkit().sync();  // sync the display on some systems
            g.dispose();
        } catch (Exception e) {
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startPauseButton) {
            if (animatorThread == null) {
                animatorThread = new Thread(animator);
                animatorThread.start();

                startPauseButton.setText("Pause");
                animator.running = true;
            } else {
                animator.running = false;

                try {
                    animatorThread.join();
                } catch (Exception exc) {
                    System.exit(1);
                }

                animatorThread = null;
                startPauseButton.setText("Start");
            }
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        // Update the game area dimensions
        GAME_AREA_WIDTH = gameArea.getWidth();
        GAME_AREA_HEIGHT = gameArea.getHeight();

        dbImageUpdate = true;
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void setHandler(ScreenListener handler) {
        this.handler = handler;
    }
}
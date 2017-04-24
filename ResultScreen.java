
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public abstract class ResultScreen extends JPanel implements ActionListener, Screen {
    private ScreenListener handler;
    private BufferedImage backgroundImage;
    private FancyButton returnToMenuButton;
    
    public ResultScreen(String backgroundImageFileName, GameData gameData) {
        handler = null;
                
        // Load background image: http://docs.oracle.com/javase/tutorial/2d/images/loadimage.html
        backgroundImage = null;
        
        try {
            // Load image: http://docs.oracle.com/javase/tutorial/uiswing/components/icon.html#getresource
            backgroundImage = ImageIO.read(getClass().getResource(backgroundImageFileName));
        } catch (IOException e) {
        }
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Custom font: see http://stackoverflow.com/questions/17539827/ttf-font-with-java-awt-graphics
        Font customFont;
        
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("resources/spaceranger.ttf"));
        } catch (Exception e) {
            customFont = null;
        }
        
        JLabel scoreLabel = new JLabel("Score: " + gameData.getScoreAsString());
        scoreLabel.setForeground(new Color(255, 255, 255));
        scoreLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        scoreLabel.setBorder(new EmptyBorder(400, 0, 0, 0));
        
        if (customFont != null) {
            scoreLabel.setFont(customFont.deriveFont(25f));
        }
        
        add(scoreLabel);
        
        JLabel accuracyLabel = new JLabel("Accuracy: " + gameData.getAccuracyPercentAsString());
        accuracyLabel.setForeground(new Color(255, 255, 255));
        accuracyLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        accuracyLabel.setBorder(new EmptyBorder(0, 0, 60, 0));
        
        if (customFont != null) {
            accuracyLabel.setFont(customFont.deriveFont(25f));
        }
        
        add(accuracyLabel);
        
        returnToMenuButton = new FancyButton("Return to Menu");
        returnToMenuButton.setMaximumSize(new Dimension(400, 45));
        returnToMenuButton.setAlignmentX(FancyButton.CENTER_ALIGNMENT);
        returnToMenuButton.setFocusable(false);
        returnToMenuButton.addActionListener(this);
        add(returnToMenuButton);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, null);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (handler != null) {
            if (e.getSource() == returnToMenuButton) {
                handler.processScreenEvent(new ScreenEvent(this, ScreenEvent.SHOW_MENU));
            }
        }
    }

    @Override
    public void setHandler(ScreenListener handler) {
        this.handler = handler;
    }
}

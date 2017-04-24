
import java.awt.Color;
import java.awt.Dimension;
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

public class IntroductionPanel extends JPanel implements ActionListener, Screen {
    private ScreenListener handler;
    private BufferedImage backgroundImage;
    private FancyButton startEasyButton;
    private FancyButton startNormalButton;
    private FancyButton startHardButton;
    private FancyButton exitButton;
    
    public IntroductionPanel() {
        handler = null;
                
        // Load background image: http://docs.oracle.com/javase/tutorial/2d/images/loadimage.html
        backgroundImage = null;
        
        try {
            // Load image: http://docs.oracle.com/javase/tutorial/uiswing/components/icon.html#getresource
            backgroundImage = ImageIO.read(getClass().getResource("resources/intro.bmp"));
        } catch (IOException e) {
        }
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JLabel gapLabel = new JLabel();
        gapLabel.setBorder(new EmptyBorder(280, 0, 0, 0));
        add(gapLabel);
        
        startEasyButton = new FancyButton("Start Easy Mission");
        startEasyButton.setMaximumSize(new Dimension(580, 40));
        startEasyButton.setAlignmentX(FancyButton.CENTER_ALIGNMENT);
        startEasyButton.setFocusable(false);
        startEasyButton.addActionListener(this);
        add(startEasyButton);
        
        startNormalButton = new FancyButton("Start Normal Mission");
        startNormalButton.setMaximumSize(new Dimension(580, 40));
        startNormalButton.setAlignmentX(FancyButton.CENTER_ALIGNMENT);
        startNormalButton.setFocusable(false);
        startNormalButton.addActionListener(this);
        add(startNormalButton);
        
        startHardButton = new FancyButton("Start Hard Mission");
        startHardButton.setMaximumSize(new Dimension(580, 40));
        startHardButton.setAlignmentX(FancyButton.CENTER_ALIGNMENT);
        startHardButton.setFocusable(false);
        startHardButton.addActionListener(this);
        add(startHardButton);
        
        exitButton = new FancyButton("Exit");
        exitButton.setMaximumSize(new Dimension(580, 40));
        exitButton.setAlignmentX(FancyButton.CENTER_ALIGNMENT);
        exitButton.setFocusable(false);
        exitButton.addActionListener(this);
        add(exitButton);
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
            if (e.getSource() == startEasyButton) {
                handler.processScreenEvent(new ScreenEvent(this, ScreenEvent.START_EASY_GAME));
            } else if (e.getSource() == startNormalButton) {
                handler.processScreenEvent(new ScreenEvent(this, ScreenEvent.START_NORMAL_GAME));
            } else if (e.getSource() == startHardButton) {
                handler.processScreenEvent(new ScreenEvent(this, ScreenEvent.START_HARD_GAME));
            } else if (e.getSource() == exitButton) {
                handler.processScreenEvent(new ScreenEvent(this, ScreenEvent.EXIT));
            }
        }
    }

    @Override
    public void setHandler(ScreenListener handler) {
        this.handler = handler;
    }
}

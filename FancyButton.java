
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.File;
import javax.swing.JButton;

public class FancyButton extends JButton {
    public FancyButton(String caption) {
        super(caption);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorder(null);
        
        // Custom font: see http://stackoverflow.com/questions/17539827/ttf-font-with-java-awt-graphics
        try {
            setFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("resources/spaceranger.ttf")).deriveFont(36f));
        } catch (Exception e) {
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        // Overriding paintComponent: http://stackoverflow.com/questions/14627223/how-to-change-a-jbutton-color-on-mouse-pressed
        // g.setColor(Color.BLACK);
        // g.fillRect(0, 0, getWidth(), getHeight());
        
        // 2-D graphics: http://www.java2s.com/Code/Java/2D-Graphics-GUI/RadialGradient.htm
        Graphics2D g2 = ((Graphics2D)g);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isPressed()) {
            g2.setColor(Color.GREEN);
            setForeground(Color.YELLOW);
        } else if (getModel().isRollover()) {
            g2.setColor(new Color(0, 150, 0));
            setForeground(Color.WHITE);
        } else {
            g2.setColor(Color.ORANGE);
            setForeground(new Color(210, 210, 210));
        }
        
        // Centering vertically: http://stackoverflow.com/questions/1055851/how-do-you-draw-a-string-centered-vertically-in-java
        int baseline=(0 + ((getHeight() + 1 - 0) / 2)) - ((g2.getFontMetrics().getAscent() + g2.getFontMetrics().getDescent()) / 2) + g2.getFontMetrics().getAscent();
        
        g2.drawString("<", 10, baseline);
        g2.drawString(">", getWidth() - g2.getFontMetrics().stringWidth(">") - 10, baseline);
        
        super.paintComponent(g);
    }
}

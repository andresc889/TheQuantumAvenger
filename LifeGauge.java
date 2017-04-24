
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

public class LifeGauge extends JLabel {
    private double ratio;

    public LifeGauge(double ratio) {
        this.ratio = ratio;
    }

    public void setRatio(double ratio) {
        if (ratio < 0) {
            return;
        }
        
        this.ratio = ratio;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        // Overriding paintComponent: http://stackoverflow.com/questions/14627223/how-to-change-a-jbutton-color-on-mouse-pressed
        g.setColor(new Color(75, 75, 75));
        g.fillRect(0, 15, getWidth() - 10, getHeight() - 30);
        
        if (ratio >= 0.50) {
            g.setColor(new Color((int)(220 * (1 - ratio) / 0.5), 220, 0));
        } else {
            g.setColor(new Color(220, (int)(200 * ratio / 0.5), 0));
        }

        g.fillRect(0, 15, (int)(ratio * (getWidth() - 10)), getHeight() - 30);

        // 2-D graphics: http://www.java2s.com/Code/Java/2D-Graphics-GUI/RadialGradient.htm
        Graphics2D g2 = ((Graphics2D)g);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        super.paintComponent(g);
    }
}

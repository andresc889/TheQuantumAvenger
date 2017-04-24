
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class FreeParticle extends Particle implements Disappearable, Destroyable {

    public final static int MIN_RADIUS = 10;
    public final static int MAX_RADIUS = 15;

    private int destroyProgress;
    private int diameter;
    
    private DisappearanceListener disappearanceHandler;
            
    private BufferedImage imageToDraw;
    
    public FreeParticle(int radius, double mass, double charge) {
        super(mass, charge);

        collisionRadius = radius;
        diameter = 2 * radius;
        
        disappearanceHandler = null;
        
        destroyProgress = -1;
        
        // Initialize gradient
        initGradient();
    }

    public FreeParticle(double mass, double charge) {
        super(mass, charge);

        // Generate a random radius
        collisionRadius = MIN_RADIUS + (int) ((MAX_RADIUS - MIN_RADIUS + 1) * Math.random());
        diameter = 2 * collisionRadius;
        
        destroyProgress = -1;
        
        // Initialize gradient
        initGradient();
    }

    private void initGradient() {
        // Change color (gradient) according to charge: http://www.java2s.com/Code/Java/2D-Graphics-GUI/RadialGradient.htm
        Color[] gradientColors = new Color[2];
        
        if (charge > 0) {
            gradientColors[0] = new Color(255, 0, 0);
            gradientColors[1] = new Color(75, 0, 0);
        } else if (charge < 0) {
            gradientColors[0] = new Color(0, 150, 255);
            gradientColors[1] = new Color(0, 50, 125);
        } else {
            gradientColors[0] = new Color(200, 200, 200);
            gradientColors[1] = new Color(75, 75, 75);
        }
        
        float[] distribution = new float[2];
                
        distribution[0] = 0.0f;
        distribution[1] = 1.0f;
        
        // Initialize the image to draw: http://docs.oracle.com/javase/tutorial/2d/images/drawonimage.html
        imageToDraw = new BufferedImage(2 * collisionRadius, 2 * collisionRadius, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imageToDraw.createGraphics();
        
        // Gradient: http://www.java2s.com/Code/Java/2D-Graphics-GUI/RadialGradient.htm
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(collisionRadius, collisionRadius) , collisionRadius, distribution, gradientColors));
        g.fillOval(0, 0, 2 * collisionRadius, 2 * collisionRadius);
    }
    
    @Override
    public void render(Graphics g) {
        // Check if we need to show the destroy animation
        if (destroyProgress >= 0) {
            if (destroyProgress <= 100) {
                // Draw burning particle
                g.setColor(new Color(1, (float) Math.random(), 0, 1 - destroyProgress / 100.0f));
                g.fillOval((int) position.getX() - collisionRadius, (int) position.getY() - collisionRadius, diameter, diameter);
                destroyProgress += 1;
            } else {
                if (disappearanceHandler != null) {
                    disappearanceHandler.processDisappearance(new DisappearanceEvent(this));
                }
            }
        } else {
            g.drawImage(imageToDraw, (int) position.getX() - collisionRadius, (int) position.getY() - collisionRadius, null);
        }
        
        /* // Put charge sign
        Font currentFont = g.getFont();
        g.setFont(currentFont.deriveFont(Font.BOLD, 18));
        String sign;

        if (charge > 0) {
            g.setColor(new Color(255, 255, 255));
            sign = "+";
        } else if (this.charge < 0) {
            g.setColor(new Color(255, 255, 255));
            sign = "-";
        } else {
            g.setColor(new Color(255, 255, 255));
            sign = "#";
        }

        g.drawString(sign, (int) (position.getX() - g.getFontMetrics().stringWidth(sign) / 2.0), (int) (position.getY() + g.getFontMetrics().getHeight() / 4.0)); */
    }

    @Override
    public void update() {
        // Check if a wall was been reached (unless the particle is being destroyed)
        if (destroyProgress < 0 && (position.getX() < collisionRadius || position.getX() > GamePanel.GAME_AREA_WIDTH - collisionRadius)) {
            // Reverse horizontal velocity
            velocity.setX(velocity.getX() * -1);

            // Return object to boundaries
            if (position.getX() < collisionRadius) {
                position.setX(collisionRadius);
            } else {
                position.setX(GamePanel.GAME_AREA_WIDTH - collisionRadius);
            }
        }

        if (destroyProgress < 0 && (position.getY() < collisionRadius || position.getY() > GamePanel.GAME_AREA_HEIGHT - collisionRadius)) {
            // Reverse horizontal velocity
            velocity.setY(velocity.getY() * -1);

            // Return object to boundaries
            if (position.getY() < collisionRadius) {
                position.setY(collisionRadius);
            } else {
                position.setY(GamePanel.GAME_AREA_HEIGHT - collisionRadius);
            }
        }

        super.update();
    }

    @Override
    public void destroy() {
        destroyProgress = 0;
    }
    
    @Override
    public boolean isBeingDestroyed() {
        return destroyProgress >= 0;
    }
    
    @Override
    public void setDisappearanceHandler(DisappearanceListener handler) {
        disappearanceHandler = handler;
    }
}

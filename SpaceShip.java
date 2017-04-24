
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.vecmath.Vector2d;

public class SpaceShip extends Particle {

    private static final int SIDE_LENGTH = 25;
    private static final double ROTATION_INCREMENT = 6 * Math.PI / 180;
    private static final double MAX_SPEED = 7;
    private static final int AURA_RADIUS = 150;
    
    private final List<SpaceShipShape> rotationalPositions;
    private int rotationalPosition;
    
    public SpaceShip(double mass, double charge) {
        super(mass, charge);

        double circumcircleRadius = Math.sqrt(3) * SIDE_LENGTH / 3;
        double incircleRadius = Math.sqrt(3) * SIDE_LENGTH / 6;

        collisionRadius = (int) ((circumcircleRadius + incircleRadius) / 2);

        double tipCircumcircleRadius = Math.sqrt(3) * (SIDE_LENGTH / 3) / 3;
        double currentAngle = 0;
        double trueAngle;

        // Change color (gradient) according to charge: http://www.java2s.com/Code/Java/2D-Graphics-GUI/RadialGradient.htm
        Color[] auraGradientColors = new Color[2];
        
        auraGradientColors[0] = new Color(80, 80, 80);
        auraGradientColors[1] = Color.BLACK;
        
        float[] auraDistribution = new float[2];
                
        auraDistribution[0] = 0.0f;
        auraDistribution[1] = 1.0f;
        
        Color[] mainBodyGradientColors = new Color[3];
        
        mainBodyGradientColors[0] = new Color(0, 140, 0);
        mainBodyGradientColors[1] = new Color(0, 240, 0);
        mainBodyGradientColors[2] = new Color(0, 140, 0);
        
        float[] mainBodyDistribution = new float[3];
                
        mainBodyDistribution[0] = 0.0f;
        mainBodyDistribution[1] = 0.5f;
        mainBodyDistribution[2] = 1.0f;
        
        Color[] tipGradientColors = new Color[3];
        
        if (charge > 0) {
            tipGradientColors[0] = new Color(140, 0, 0);
            tipGradientColors[1] = new Color(240, 0, 0);
            tipGradientColors[2] = new Color(140, 0, 0);
        } else if (charge < 0) {
            tipGradientColors[0] = new Color(0, 70, 175);
            tipGradientColors[1] = new Color(0, 150, 255);
            tipGradientColors[2] = new Color(0, 70, 175);
        } else {
            tipGradientColors[0] = new Color(140, 140, 140);
            tipGradientColors[1] = new Color(220, 220, 220);
            tipGradientColors[2] = new Color(140, 140, 140);
        }
        
        float[] tipDistribution = new float[3];
                
        tipDistribution[0] = 0.0f;
        tipDistribution[1] = 0.5f;
        tipDistribution[2] = 1.0f;
        
        rotationalPositions = Collections.synchronizedList(new ArrayList<SpaceShipShape>());
        
        synchronized (rotationalPositions) {
            while (currentAngle < 2 * Math.PI) {
                trueAngle = currentAngle + 3 * Math.PI / 2;

                SpaceShipShape shape = new SpaceShipShape();

                // Assemble main body
                shape.mainFrontCorner = new Vector2d(Math.cos(trueAngle), Math.sin(trueAngle));
                shape.mainFrontCorner.scale(circumcircleRadius);

                shape.mainBackLeftCorner = new Vector2d(Math.cos(trueAngle - 2 * Math.PI / 3), Math.sin(trueAngle - 2 * Math.PI / 3));
                shape.mainBackLeftCorner.scale(circumcircleRadius);

                shape.mainBackRightCorner = new Vector2d(Math.cos(trueAngle + 2 * Math.PI / 3), Math.sin(trueAngle + 2 * Math.PI / 3));
                shape.mainBackRightCorner.scale(circumcircleRadius);

                shape.normalizedDirection = new Vector2d();
                shape.normalizedDirection.normalize(shape.mainFrontCorner);

                // Assemble tip
                shape.tipFrontCorner = new Vector2d(Math.cos(trueAngle), Math.sin(trueAngle));
                shape.tipFrontCorner.scale(tipCircumcircleRadius);

                Vector2d tipOffset = new Vector2d();
                tipOffset.sub(shape.mainFrontCorner, shape.tipFrontCorner);

                shape.tipFrontCorner.scale(1.5);
                shape.tipFrontCorner.add(tipOffset);

                shape.tipBackLeftCorner = new Vector2d(Math.cos(trueAngle - 2 * Math.PI / 3), Math.sin(trueAngle - 2 * Math.PI / 3));
                shape.tipBackLeftCorner.scale(1.5 * tipCircumcircleRadius);
                shape.tipBackLeftCorner.add(tipOffset);

                shape.tipBackRightCorner = new Vector2d(Math.cos(trueAngle + 2 * Math.PI / 3), Math.sin(trueAngle + 2 * Math.PI / 3));
                shape.tipBackRightCorner.scale(1.5 * tipCircumcircleRadius);
                shape.tipBackRightCorner.add(tipOffset);

                // Initialize the image to draw: http://docs.oracle.com/javase/tutorial/2d/images/drawonimage.html
                shape.imageToDraw = new BufferedImage(2 * AURA_RADIUS, 2 * AURA_RADIUS, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = shape.imageToDraw.createGraphics();
                
                // Gradient: http://www.java2s.com/Code/Java/2D-Graphics-GUI/RadialGradient.htm
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g.setPaint(new RadialGradientPaint(new Point2D.Double(AURA_RADIUS, AURA_RADIUS), AURA_RADIUS, auraDistribution, auraGradientColors));
                g.fillOval(0, 0, 2 * AURA_RADIUS, 2 * AURA_RADIUS);

                // Paint main body
                Vector2d mainFrontCorner = new Vector2d();
                mainFrontCorner.add(shape.mainFrontCorner, new Vector2d(AURA_RADIUS, AURA_RADIUS));

                Vector2d mainBackLeftCorner = new Vector2d();
                mainBackLeftCorner.add(shape.mainBackLeftCorner, new Vector2d(AURA_RADIUS, AURA_RADIUS));

                Vector2d mainBackRightCorner = new Vector2d();
                mainBackRightCorner.add(shape.mainBackRightCorner, new Vector2d(AURA_RADIUS, AURA_RADIUS));

                int mainX[] = {(int) mainFrontCorner.getX(), (int) mainBackLeftCorner.getX(), (int) mainBackRightCorner.getX()};
                int mainY[] = {(int) mainFrontCorner.getY(), (int) mainBackLeftCorner.getY(), (int) mainBackRightCorner.getY()};

                Point2D.Double mainBodyGradientStart = new Point2D.Double(mainBackLeftCorner.getX(), mainBackLeftCorner.getY());
                Point2D.Double mainBodyGradientEnd = new Point2D.Double(mainBackRightCorner.getX(), mainBackRightCorner.getY());
                
                g.setPaint(new LinearGradientPaint(mainBodyGradientStart, mainBodyGradientEnd, mainBodyDistribution, mainBodyGradientColors));
                g.fillPolygon(mainX, mainY, 3);

                // Paint tip
                Vector2d tipFrontCorner = new Vector2d();
                tipFrontCorner.add(shape.tipFrontCorner, new Vector2d(AURA_RADIUS, AURA_RADIUS));

                Vector2d tipBackLeftCorner = new Vector2d();
                tipBackLeftCorner.add(shape.tipBackLeftCorner, new Vector2d(AURA_RADIUS, AURA_RADIUS));

                Vector2d tipBackRightCorner = new Vector2d();
                tipBackRightCorner.add(shape.tipBackRightCorner, new Vector2d(AURA_RADIUS, AURA_RADIUS));

                int tipX[] = {(int) tipFrontCorner.getX(), (int) tipBackLeftCorner.getX(), (int) tipBackRightCorner.getX()};
                int tipY[] = {(int) tipFrontCorner.getY(), (int) tipBackLeftCorner.getY(), (int) tipBackRightCorner.getY()};

                Point2D.Double tipGradientStart = new Point2D.Double(tipBackLeftCorner.getX(), tipBackLeftCorner.getY());
                Point2D.Double tipGradientEnd = new Point2D.Double(tipBackRightCorner.getX(), tipBackRightCorner.getY());
                
                g.setPaint(new LinearGradientPaint(tipGradientStart, tipGradientEnd, tipDistribution, tipGradientColors));
                g.fillPolygon(tipX, tipY, 3);
        
                rotationalPositions.add(shape);

                currentAngle += ROTATION_INCREMENT;
            }
        }

        rotationalPosition = 0;
        position = new Vector2d(GamePanel.GAME_AREA_WIDTH / 2, GamePanel.GAME_AREA_HEIGHT / 2);
    }

    public Vector2d getNormalizedDirection() {
        return new Vector2d(rotationalPositions.get(rotationalPosition).normalizedDirection);
    }

    public Vector2d getFrontPosition() {
        Vector2d frontPosition = new Vector2d(position);
        frontPosition.add(rotationalPositions.get(rotationalPosition).tipFrontCorner);
        
        return new Vector2d(frontPosition);
    }
    
    public void rotateRight() {
        rotationalPosition = (rotationalPosition + 1) % rotationalPositions.size();
    }

    public void rotateLeft() {
        rotationalPosition = rotationalPosition == 0 ? rotationalPositions.size() - 1 : rotationalPosition - 1;
    }

    @Override
    public void render(Graphics g) {
        SpaceShipShape shape = rotationalPositions.get(rotationalPosition);
        g.drawImage(shape.imageToDraw, (int)position.getX() - AURA_RADIUS, (int)position.getY() - AURA_RADIUS, null);
        
        /*// Paint aura: http://www.java2s.com/Code/Java/2D-Graphics-GUI/RadialGradient.htm
        ((Graphics2D)g).setPaint(new RadialGradientPaint(new Point2D.Double(position.getX(), position.getY()), 100, distribution, gradientColors));
        
        g.fillOval((int)position.getX() - 100, (int)position.getY() - 100, 200, 200);
        
        // Paint main body
        Vector2d mainFrontCorner = new Vector2d();
        mainFrontCorner.add(shape.mainFrontCorner, position);

        Vector2d mainBackLeftCorner = new Vector2d();
        mainBackLeftCorner.add(shape.mainBackLeftCorner, position);

        Vector2d mainBackRightCorner = new Vector2d();
        mainBackRightCorner.add(shape.mainBackRightCorner, position);

        int mainX[] = {(int) mainFrontCorner.getX(), (int) mainBackLeftCorner.getX(), (int) mainBackRightCorner.getX()};
        int mainY[] = {(int) mainFrontCorner.getY(), (int) mainBackLeftCorner.getY(), (int) mainBackRightCorner.getY()};

        g.setColor(new Color(0, 255, 0));
        g.fillPolygon(mainX, mainY, 3);

        // Paint tip
        Vector2d tipFrontCorner = new Vector2d();
        tipFrontCorner.add(shape.tipFrontCorner, position);

        Vector2d tipBackLeftCorner = new Vector2d();
        tipBackLeftCorner.add(shape.tipBackLeftCorner, position);

        Vector2d tipBackRightCorner = new Vector2d();
        tipBackRightCorner.add(shape.tipBackRightCorner, position);

        int tipX[] = {(int) tipFrontCorner.getX(), (int) tipBackLeftCorner.getX(), (int) tipBackRightCorner.getX()};
        int tipY[] = {(int) tipFrontCorner.getY(), (int) tipBackLeftCorner.getY(), (int) tipBackRightCorner.getY()};

        if (charge > 0) {
            g.setColor(new Color(255, 0, 0));
        } else if (charge < 0) {
            g.setColor(new Color(150, 150, 255));
        } else {
            g.setColor(new Color(255, 255, 255));
        }

        g.fillPolygon(tipX, tipY, 3);*/
    }

    @Override
    public void update() {
        maxSpeedReached = velocity.length() >= MAX_SPEED;

        // Check if a wall was been reached
        if (position.getX() < collisionRadius || position.getX() > GamePanel.GAME_AREA_WIDTH - collisionRadius) {
            // Reverse horizontal velocity
            velocity.setX(velocity.getX() * -1);

            // Take away 10% of the speed
            velocity.scale(0.9);

            // Return object to boundaries
            if (position.getX() < collisionRadius) {
                position.setX(collisionRadius);
            } else {
                position.setX(GamePanel.GAME_AREA_WIDTH - collisionRadius);
            }
        }

        if (position.getY() < collisionRadius || position.getY() > GamePanel.GAME_AREA_HEIGHT - collisionRadius) {
            // Reverse horizontal velocity
            velocity.setY(velocity.getY() * -1);

            // Take away 10% of the speed
            velocity.scale(0.9);

            // Return object to boundaries
            if (position.getY() < collisionRadius) {
                position.setY(collisionRadius);
            } else {
                position.setY(GamePanel.GAME_AREA_HEIGHT - collisionRadius);
            }
        }

        super.update();
    }
}

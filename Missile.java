
import java.awt.Color;
import java.awt.Graphics;
import javax.vecmath.Vector2d;


public class Missile extends Particle implements Disappearable, Destroyable {

    public static final int MISSILE_RADIUS = 2;
    public static final double MISSILE_SPEED = 18.0;
    
    private boolean beingDestroyed;
    
    private DisappearanceListener disappearanceHandler;
            
    public Missile(Vector2d position, Vector2d velocity) {
        super(1, 0);
        
        this.position = new Vector2d(position);
        this.velocity = new Vector2d(velocity);
        
        beingDestroyed = false;
                
        disappearanceHandler = null;
        
        collisionRadius = MISSILE_RADIUS;
    }

    @Override
    public void render(Graphics g) {
        int diameter = 2 * collisionRadius;
        
        g.setColor(Color.YELLOW);
        g.fillOval((int) position.getX() - collisionRadius, (int) position.getY() - collisionRadius, diameter, diameter);
    }

    @Override
    public void update() {
        super.update();
        
        // Check if missile has disappeared
        if (disappearanceHandler == null) {
            return;
        }
        
        if (position.getX() < -collisionRadius || position.getX() > GamePanel.GAME_AREA_WIDTH + collisionRadius) {
            destroy();
            return;
        }

        if (position.getY() < -collisionRadius || position.getY() > GamePanel.GAME_AREA_HEIGHT + collisionRadius) {
            destroy();
            return;
        }
    }

    @Override
    public void destroy() {
        beingDestroyed = true;
        
        if (disappearanceHandler != null) {
            disappearanceHandler.processDisappearance(new DisappearanceEvent(this));
        }
    }

    @Override
    public boolean isBeingDestroyed() {
        return beingDestroyed;
    }
    
    @Override
    public void setDisappearanceHandler(DisappearanceListener handler) {
        disappearanceHandler = handler;
    }
    
}

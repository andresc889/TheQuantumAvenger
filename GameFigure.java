
import java.awt.Graphics;
import javax.vecmath.Vector2d;

public abstract class GameFigure {
    // Protected access for quick access by children

    protected Vector2d position;
    protected Vector2d velocity;
    protected boolean overridePosition;
    protected boolean maxSpeedReached;
    protected int collisionRadius;

    public GameFigure() {
        this.position = new Vector2d(0, 0);
        this.velocity = new Vector2d(0, 0);
        overridePosition = false;
        maxSpeedReached = false;
        collisionRadius = 0;
    }

    public GameFigure(Vector2d position, Vector2d velocity) {
        this.position = new Vector2d(position);
        this.velocity = new Vector2d(velocity);
        collisionRadius = 0;
    }

    public Vector2d getPosition() {
        return new Vector2d(position);
    }

    public void setPosition(Vector2d position) {
        this.position = new Vector2d(position);
    }

    public Vector2d getVelocity() {
        return new Vector2d(velocity);
    }

    public void setVelocity(Vector2d velocity) {
        this.velocity = new Vector2d(velocity);
    }

    public abstract void render(Graphics g);

    public abstract void update();
}

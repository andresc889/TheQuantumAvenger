
import java.awt.Graphics;
import javax.vecmath.Vector2d;

public abstract class Particle extends GameFigure {

    // Protected access for quick access by children
    protected double mass;
    protected double charge;
    protected Vector2d netForce;

    public Particle(double mass, double charge) {
        this.mass = mass;
        this.charge = charge;
        netForce = new Vector2d(0, 0);
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public Vector2d getNetForce() {
        return new Vector2d(netForce);
    }

    public void setNetForce(Vector2d netForce) {
        this.netForce = new Vector2d(netForce);
    }

    @Override
    public abstract void render(Graphics g);

    @Override
    public void update() {
        // Calculate the acceleration using Newton's second law (F = m * a)
        Vector2d acceleration;

        // Check if we have reached the maximum speed. If so, we only allow
        // decceleration or change in direction
        //
        if (!maxSpeedReached || netForce.dot(velocity) <= 0) {
            acceleration = new Vector2d(netForce);
        } else {
            Vector2d normalizedVelocity = new Vector2d(velocity);
            normalizedVelocity.normalize();

            // Obtain the parallel component of the force
            double scalarProjection = normalizedVelocity.dot(netForce);

            Vector2d parallelForce = new Vector2d(normalizedVelocity);
            parallelForce.scale(scalarProjection);

            // Obtain the perpendicular component of the force
            Vector2d perpendicularForce = new Vector2d(netForce);
            perpendicularForce.sub(parallelForce);

            acceleration = new Vector2d(perpendicularForce);
        }

        acceleration.scale(1 / mass);

        // Update the position (Rf = Ro + Vo * dt + 0.5 * a * dt^2, dt = 1)
        if (!overridePosition) {
            Vector2d accelerationTerm = new Vector2d(acceleration);
            accelerationTerm.scale(0.5);

            position.add(velocity);
            position.add(accelerationTerm);
        } else {
            overridePosition = false;
        }

        // Update the velocity (Vf = Vo + a * dt, where dt = 1)
        velocity.add(acceleration);
    }
}

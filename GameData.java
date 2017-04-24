
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.vecmath.Vector2d;

public class GameData implements DisappearanceListener {

    public final static int NUM_OF_FREE_PARTICLES = 25;
    public final static int MIN_DISTANCE_FREE_PARTICLES_EDGES = 5;
    public final static int CLEARANCE_DISTANCE = 100;
    
    public final static int DIFFICULTY_EASY = 1;
    public final static int DIFFICULTY_NORMAL = 2;
    public final static int DIFFICULTY_HARD = 3;
    
    private final static int INITIAL_LIFE = 100;
    
    private boolean initialized;
    private boolean keyUpOn;
    private boolean keyLeftOn;
    private boolean keyRightOn;
    private boolean shooterOn;
    private boolean shooterReleased;
    final List<GameFigure> figures;
    final List<Disappearable> figuresToRemove;
    SpaceShip spaceShip;
    private int life;
    private int lifeDecreaseSpeed;
    private int score;
    private double maxSpaceShipDistance;
    private int difficulty;
    private int firedShotsCount;
    private int accurateShotsCount;
    
    private boolean win;
    private boolean loss;
    
    public GameData() {
        initialized = false;

        difficulty = DIFFICULTY_EASY;
        firedShotsCount = 0;
        accurateShotsCount = 0;
        win = false;
        loss = false;
        
        keyUpOn = false;
        keyLeftOn = false;
        keyRightOn = false;
        shooterOn = false;
        shooterReleased = true;
        
        figures = Collections.synchronizedList(new ArrayList<GameFigure>());
        figuresToRemove = Collections.synchronizedList(new ArrayList<Disappearable>());
        
        life = INITIAL_LIFE;
        score = 0;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    
    private void initialize() {
        if (initialized) {
            return;
        }

        double charges[] = {-1, 0, 1};
        double chargeMultiplier;
        
        switch (difficulty) {
            case DIFFICULTY_EASY:
                chargeMultiplier = 3;
                lifeDecreaseSpeed = 5;
                break;
            case DIFFICULTY_NORMAL:
                chargeMultiplier = 10;
                lifeDecreaseSpeed = 7;
                break;
            case DIFFICULTY_HARD:
                chargeMultiplier = 20;
                lifeDecreaseSpeed = 10;
                break;
            default:
                chargeMultiplier = 1;
        }
        
        int centerX = GamePanel.GAME_AREA_WIDTH / 2;
        int centerY = GamePanel.GAME_AREA_HEIGHT / 2;
        maxSpaceShipDistance = Math.sqrt(GamePanel.GAME_AREA_WIDTH * GamePanel.GAME_AREA_WIDTH + GamePanel.GAME_AREA_HEIGHT * GamePanel.GAME_AREA_HEIGHT);
       
        // Add the space ship
        spaceShip = new SpaceShip(1, chargeMultiplier * charges[difficulty == DIFFICULTY_EASY ? 1 : (Math.random() > 0.5 ? 0 : 2)]);
        figures.add(spaceShip);

        // Add free particles
        FreeParticle newParticle;
        int minX;
        int minY;
        int maxX;
        int maxY;
        int randomX;
        int randomY;

        for (int i = 0; i < NUM_OF_FREE_PARTICLES; i++) {
            // Generate charge randomly
            newParticle = new FreeParticle(0.5, chargeMultiplier * charges[(int) (3 * Math.random())]);
            newParticle.setDisappearanceHandler(this);
                    
            // Set the position of the particle randomly and away from the center.
            // Also, make sure every charge does not overlap with another
            while (true) {
                minX = minY = 0;
                maxX = GamePanel.GAME_AREA_WIDTH - 2 * newParticle.collisionRadius;
                maxY = GamePanel.GAME_AREA_HEIGHT - 2 * newParticle.collisionRadius;

                randomX = minX + (int) ((maxX - minX + 1) * Math.random());
                randomY = minY + (int) ((maxY - minY + 1) * Math.random());

                Vector2d candidatePosition = new Vector2d(randomX, randomY);
                
                // Make sure ship is cleared
                Vector2d fromShip = new Vector2d(candidatePosition);
                fromShip.sub(spaceShip.position);
                
                if (fromShip.length() - newParticle.collisionRadius < CLEARANCE_DISTANCE) {
                    continue;
                }
                
                // Make sure all existing free particles are cleared
                boolean existingFreeParticlesCleared = true;
                
                for (int j = 0; j < figures.size(); j++) {
                    if (!(figures.get(j) instanceof FreeParticle)) {
                        continue;
                    }
                    
                    Vector2d fromCurrentParticle = new Vector2d(candidatePosition);
                    fromCurrentParticle.sub(figures.get(j).position);
                    
                    if (fromCurrentParticle.length() - newParticle.collisionRadius - figures.get(j).collisionRadius < MIN_DISTANCE_FREE_PARTICLES_EDGES) {
                        existingFreeParticlesCleared = false;
                        break;
                    }
                }
                
                if (existingFreeParticlesCleared) {
                    break;
                }
            }

            newParticle.setPosition(new Vector2d(randomX, randomY));

            // Set the initial velocity of 0-charge particles
            if (newParticle.charge == 0) {
                double randomAngle = 2 * Math.PI * Math.random();

                newParticle.velocity = new Vector2d(Math.cos(randomAngle), Math.sin(randomAngle));
                newParticle.velocity.scale(1 + 2 * Math.random());
            }

            figures.add(newParticle);
        }

        initialized = true;
    }

    public boolean isKeyUpOn() {
        return keyUpOn;
    }

    public void setKeyUpOn(boolean keyUpOn) {
        this.keyUpOn = keyUpOn;
    }

    public boolean isKeyLeftOn() {
        return keyLeftOn;
    }

    public void setKeyLeftOn(boolean keyLeftOn) {
        this.keyLeftOn = keyLeftOn;
    }

    public boolean isKeyRightOn() {
        return keyRightOn;
    }

    public void setKeyRightOn(boolean keyRightOn) {
        this.keyRightOn = keyRightOn;
    }

    public void setShooterOn(boolean shooterOn) {
        if (shooterOn && !shooterReleased) {
            return;
        }
        
        this.shooterOn = shooterOn;
        this.shooterReleased = !shooterOn;
    }

    public void setShooterReleased(boolean shooterReleased) {
        this.shooterReleased = shooterReleased;
    }
    
    public String getScoreAsString() {
        return String.format("%07d", score);
    }
    
    public String getAccuracyPercentAsString() {
        if (firedShotsCount > 0) {
            return String.format("%.02f%%", accurateShotsCount * 100.0 / firedShotsCount);
        } else {
            return "N/A";
        }
    }
    
    public double getLifeRemainingRatio() {
        return (double)life / INITIAL_LIFE;
    }

    public boolean isWin() {
        return win;
    }

    public boolean isLoss() {
        return loss;
    }
    
    public void update() {
        if (!initialized) {
            initialize();
        }

        List<GameFigure> remove = new ArrayList<>();
        GameFigure f;
        Particle particle1;
        Particle particle2;
        double distance;
        Vector2d unitNormal;
        Vector2d unitTangent;
        double normalInitialVelocity1;
        double tangentInitialVelocity1;
        double normalInitialVelocity2;
        double tangentInitialVelocity2;
        Vector2d newNormalVelocity1;
        Vector2d newTangentVelocity1;
        Vector2d newVelocity1;
        Vector2d newNormalVelocity2;
        Vector2d newTangentVelocity2;
        Vector2d newVelocity2;

        synchronized (figures) {
            // Remove figures
            synchronized(figuresToRemove) {
                for (int i = 0; i < figuresToRemove.size(); i++) {
                    figures.remove(figuresToRemove.get(i));
                }
                
                figuresToRemove.clear();
            }
            
            // Check if we have to shoot a missile
            if (shooterOn) {
                Vector2d direction = spaceShip.getNormalizedDirection();
                
                Vector2d startPosition = new Vector2d(direction);
                startPosition.scale(Missile.MISSILE_RADIUS);
                startPosition.add(spaceShip.getFrontPosition());
                
                Vector2d startVelocity = new Vector2d(direction);
                startVelocity.scale(Missile.MISSILE_SPEED);
                
                Missile newMissile = new Missile(startPosition, startVelocity);
                newMissile.setDisappearanceHandler(this);
                
                figures.add(newMissile);
                firedShotsCount++;
                
                shooterOn = false;
            }
        
            for (int i = 0; i < figures.size(); i++) {
                ((Particle)figures.get(i)).setNetForce(new Vector2d(0, 0));
            }
            
            for (int i = 0; i < figures.size() && !loss; i++) {
                f = figures.get(i);

                // Check if we are updating the space ship
                if (f instanceof SpaceShip) {
                    // Update the net force depending on keys
                    SpaceShip ship = (SpaceShip) f;

                    if (keyUpOn) {
                        Vector2d newNetForce = ship.getNormalizedDirection();
                        newNetForce.scale(0.3);
                        ship.setNetForce(newNetForce);
                    } else {
                        ship.setNetForce(new Vector2d(0, 0));
                    }

                    if (keyLeftOn) {
                        ship.rotateLeft();
                    } else if (keyRightOn) {
                        ship.rotateRight();
                    }
                }

                // Calculate the force between this particle and the others
                particle1 = (Particle) f;

                for (int j = i + 1; j < figures.size(); j++) {
                    particle2 = (Particle) figures.get(j);

                    boolean ignoreInteraction = false;
                    
                    if (particle1 instanceof Destroyable) {
                        ignoreInteraction = ((Destroyable)particle1).isBeingDestroyed();
                    }
                    
                    if (!ignoreInteraction && particle2 instanceof Destroyable) {
                        ignoreInteraction = ((Destroyable)particle2).isBeingDestroyed();
                    }

                    if (ignoreInteraction) {
                        continue;
                    }
                    
                    Vector2d newForce = new Vector2d(particle2.position);
                    newForce.sub(particle1.position);

                    distance = newForce.length();

                    newForce.scale(-1 * particle1.charge * particle2.charge / Math.pow(newForce.length(), 3));

                    // Update the net force for both particles involved (except for SpaceShip)
                    if (!(particle1 instanceof SpaceShip)) {
                        particle1.netForce.add(newForce);
                    }

                    newForce.scale(-1);

                    if (!(particle2 instanceof SpaceShip)) {
                        particle2.netForce.add(newForce);
                    }
                    
                    // Check for a collision
                    if (distance <= particle1.collisionRadius + particle2.collisionRadius) {
                        // Check if one of the particles is a missile and the other a free particle
                        if ((particle1 instanceof Missile && particle2 instanceof FreeParticle) || (particle1 instanceof FreeParticle && particle2 instanceof Missile)) {
                            Destroyable destroyable1 = (Destroyable)particle1;
                            Destroyable destroyable2 = (Destroyable)particle2;
                            
                            FreeParticle particle = particle1 instanceof FreeParticle ? (FreeParticle)particle1 : (FreeParticle)particle2;
                            
                            // Destroy both
                            if (!destroyable1.isBeingDestroyed() && !destroyable2.isBeingDestroyed()) {
                                destroyable1.destroy();
                                destroyable2.destroy();
                                
                                // Increase score
                                int chargeMultiplier = particle.charge != 0 ? 2 : 1;
                                int radiusMultiplier = (int)((double)FreeParticle.MAX_RADIUS * 2 / particle.collisionRadius);
                                int speedMultiplier = (int)particle.velocity.length() + 1;
                                
                                if (speedMultiplier > 20) {
                                    speedMultiplier = 20;
                                }
                                
                                Vector2d spaceShipDistance = new Vector2d(particle.position);
                                spaceShipDistance.sub(spaceShip.position);
                                
                                int distanceMultiplier = (int)(spaceShipDistance.length() * 20 / maxSpaceShipDistance) + 1;
                                
                                score += 5 * chargeMultiplier * radiusMultiplier * speedMultiplier * distanceMultiplier;
                                
                                // Increase accurate shots count
                                accurateShotsCount++;
                            }
                            
                            continue;
                        }
                        
                        // Check if ship collided with a free particle
                        if ((particle1 instanceof SpaceShip && particle2 instanceof FreeParticle) || (particle1 instanceof FreeParticle && particle2 instanceof SpaceShip)) {
                            // Decrease life
                            life -= lifeDecreaseSpeed;
                            
                            // Check if user lost
                            if (life <= 0) {
                                loss = true;
                                break;
                            }
                        }
                        
                        // Handle collision according to method in http://www.imada.sdu.dk/~rolf/Edu/DM815/E10/2dcollisions.pdf
                        unitNormal = new Vector2d(particle2.position);
                        unitNormal.sub(particle1.position);
                        unitNormal.normalize();

                        unitTangent = new Vector2d(-1 * unitNormal.getY(), unitNormal.getX());

                        // Find the normal and tangent components of the initial velocities
                        normalInitialVelocity1 = particle1.velocity.dot(unitNormal);
                        tangentInitialVelocity1 = particle1.velocity.dot(unitTangent);

                        normalInitialVelocity2 = particle2.velocity.dot(unitNormal);
                        tangentInitialVelocity2 = particle2.velocity.dot(unitTangent);

                        // Obtain the new velocities
                        newNormalVelocity1 = new Vector2d(unitNormal);
                        newNormalVelocity1.scale((normalInitialVelocity1 * (particle1.mass - particle2.mass) + 2 * particle2.mass * normalInitialVelocity2) / (particle1.mass + particle2.mass));

                        newTangentVelocity1 = new Vector2d(unitTangent);
                        newTangentVelocity1.scale(tangentInitialVelocity1);

                        newNormalVelocity2 = new Vector2d(unitNormal);
                        newNormalVelocity2.scale((normalInitialVelocity2 * (particle2.mass - particle1.mass) + 2 * particle1.mass * normalInitialVelocity1) / (particle1.mass + particle2.mass));

                        newTangentVelocity2 = new Vector2d(unitTangent);
                        newTangentVelocity2.scale(tangentInitialVelocity2);

                        newVelocity1 = new Vector2d(newNormalVelocity1);
                        newVelocity1.add(newTangentVelocity1);

                        particle1.setVelocity(newVelocity1);

                        newVelocity2 = new Vector2d(newNormalVelocity2);
                        newVelocity2.add(newTangentVelocity2);

                        particle2.setVelocity(newVelocity2);

                        // Force them to be apart
                        particle2.overridePosition = true;
                        particle2.position = new Vector2d(unitNormal);
                        particle2.position.scale(particle1.collisionRadius + particle2.collisionRadius + 5);
                        particle2.position.add(particle1.position);
                    }
                }

                f.update();
            }

            figures.removeAll(remove);
        }
    }

    @Override
    public void processDisappearance(DisappearanceEvent event) {
        synchronized (figuresToRemove) {
            figuresToRemove.add(event.getObject());
            
            if (event.getObject() instanceof FreeParticle) {
                if (accurateShotsCount == NUM_OF_FREE_PARTICLES) {
                    win = true;
                }
            }
        }
    }
}

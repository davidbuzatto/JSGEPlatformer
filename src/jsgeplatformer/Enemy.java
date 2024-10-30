package jsgeplatformer;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.math.CollisionUtils;
import br.com.davidbuzatto.jsge.math.Vector2;
import br.com.davidbuzatto.jsge.sound.Sound;
import java.awt.Color;

/**
 * Inimigo.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Enemy {
    
    public static enum State {
        JUMPING,
        FALLING,
        ON_GROUND
    }
    
    public static enum Direction {
        LEFT,
        RIGHT
    }
    
    public static enum CollisionType {
        NONE,
        LEFT,
        RIGHT,
        UP,
        DOWN,
        ALL
    }
    
    public Rectangle rect;
    public Vector2 vel;
    private Vector2 prevPos;
    
    public double walkSpeed;
    
    public Color color;
    public State state;
    public Direction direction;
    public boolean dead;
    
    private Animation walkRightAnimation;
    private Animation walkLeftAnimation;
    
    private Sound sound;
    
    private Rectangle cpLeft;
    private Rectangle cpRight;
    private Rectangle cpUp;
    private Rectangle cpDown;

    public Enemy( Rectangle rect, double walkSpeed, Color color, 
                   Animation walkRightAnimation, Animation walkLeftAnimation, Sound sound ) {
        
        this.rect = rect;
        this.prevPos = new Vector2();
        this.vel = new Vector2();
        this.walkSpeed = walkSpeed;
        this.color = color;
        this.state = State.ON_GROUND;
        this.direction = Direction.LEFT;
        
        this.walkRightAnimation = walkRightAnimation;
        this.walkLeftAnimation = walkLeftAnimation;
        
        this.sound = sound;
        
        cpLeft = new Rectangle( 0, 0, 6, 20 );
        cpRight = new Rectangle( 0, 0, 6, 20 );
        cpUp = new Rectangle( 0, 0, 12, 6 );
        cpDown = new Rectangle( 0, 0, 12, 6 );
        
    }
    
    public void update( double delta, EngineFrame e ) {
        
        if ( direction == Direction.LEFT ) {
            vel.x = -walkSpeed;
            walkRightAnimation.reset();
            walkLeftAnimation.update( delta );
        } else if ( direction == Direction.RIGHT ) {
            vel.x = walkSpeed;
            walkLeftAnimation.reset();
            walkRightAnimation.update( delta );
        }
        
        rect.x += vel.x * delta;
        rect.y += vel.y * delta;
        
        vel.y += Main.GRAVITY;
        
        if ( vel.y >= Main.MAX_FALL_SPEED ) {
            vel.y = Main.MAX_FALL_SPEED;
        }
        
        if ( state != State.ON_GROUND ) {
            if ( prevPos.y < rect.y ) {
                state = State.FALLING;
            }
        }
        
        updateCollisionProbes();
        
        prevPos.x = rect.x;
        prevPos.y = rect.y;
        
    }
    
    public void draw( EngineFrame e ) {
        
        if ( !dead ) {
            
            Image currentImage;

            if ( direction == Direction.LEFT ) {
                currentImage = walkLeftAnimation.getFrameImage();
            } else {
                currentImage = walkRightAnimation.getFrameImage();
            }

            e.drawImage( currentImage, rect.x - rect.width / 2, rect.y - rect.height / 2 );

            /*cpLeft.fill( e, Engine.RED );
            cpRight.fill( e, Engine.GREEN );
            cpUp.fill( e, Engine.VIOLET );
            cpDown.fill( e, Engine.GOLD );*/
            
        }
        
    }
    
    public void updateCollisionProbes() {
        
        cpLeft.x = rect.x - rect.width / 2;
        cpLeft.y = rect.y - cpLeft.height / 2;
        
        cpRight.x = rect.x + rect.width / 2 - cpRight.width;
        cpRight.y = rect.y - cpRight.height / 2;
        
        cpUp.x = rect.x - cpUp.width / 2;
        cpUp.y = rect.y - rect.height / 2;
        
        cpDown.x = rect.x - cpDown.width / 2;
        cpDown.y = rect.y + rect.height / 2 - cpDown.height;
        
    }
    
    public void setOnGround() {
        vel.y = 0.0;
        state = State.ON_GROUND;
    }
    
    public Rectangle getBoundingBox() {
        return rect;
    }
    
    public CollisionType checkCollision( Block block ) {
        
        Rectangle blockBB = block.getBoundingBox();
        
        if ( CollisionUtils.checkCollisionRectangles( cpUp, blockBB ) ) {
            return CollisionType.UP;
        } else if ( CollisionUtils.checkCollisionRectangles( cpDown, blockBB ) ) {
            return CollisionType.DOWN;
        } else if ( CollisionUtils.checkCollisionRectangles( cpLeft, blockBB ) ) {
            return CollisionType.LEFT;
        } else if ( CollisionUtils.checkCollisionRectangles( cpRight, blockBB ) ) {
            return CollisionType.RIGHT;
        }
        
        return CollisionType.NONE;
        
    }
    
    public void turn() {
        if ( direction == Direction.LEFT ) {
            direction = Direction.RIGHT;
        } else {
            direction = Direction.LEFT;
        }
    }
    
    public void kill() {
        sound.play();
        dead = true;
    }
    
}

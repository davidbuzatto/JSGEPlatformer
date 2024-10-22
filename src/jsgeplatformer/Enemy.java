package jsgeplatformer;

import br.com.davidbuzatto.jsge.core.Engine;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.geom.Vector2;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.sound.Sound;
import br.com.davidbuzatto.jsge.utils.CollisionUtils;
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
    
    public Vector2 pos;
    public Vector2 prevPos;
    public Vector2 dim;
    public Vector2 vel;
    public double walkSpeed;
    public Color color;
    public State state;
    public Direction direction;
    
    private Animation walkRightAnimation;
    private Animation walkLeftAnimation;
    
    public boolean dead;
    
    private Sound sound;
    
    private Rectangle cpLeft;
    private Rectangle cpRight;
    private Rectangle cpUp;
    private Rectangle cpDown;
    
    boolean running;

    public Enemy( Vector2 pos, Vector2 dim, double walkSpeed, Color color, 
                   Animation walkRightAnimation, Animation walkLeftAnimation, Sound sound ) {
        
        this.pos = pos;
        this.prevPos = new Vector2();
        this.dim = dim;
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
    
    public void update( double delta, Engine e ) {
        
        if ( direction == Direction.LEFT ) {
            vel.x = -walkSpeed;
            walkRightAnimation.reset();
            walkLeftAnimation.update( delta );
        } else if ( direction == Direction.RIGHT ) {
            vel.x = walkSpeed;
            walkLeftAnimation.reset();
            walkRightAnimation.update( delta );
        }
        
        pos.x += vel.x * delta;
        pos.y += vel.y * delta;
        
        vel.y += Main.GRAVITY;
        
        if ( vel.y >= Main.MAX_FALL_SPEED ) {
            vel.y = Main.MAX_FALL_SPEED;
        }
        
        if ( state != State.ON_GROUND ) {
            if ( prevPos.y < pos.y ) {
                state = State.FALLING;
            }
        }
        
        updateCollisionProbes();
        prevPos.x = pos.x;
        prevPos.y = pos.y;
        
    }
    
    public void draw( Engine e ) {
        
        if ( !dead ) {
            
            Image currentImage;

            if ( direction == Direction.LEFT ) {
                currentImage = walkLeftAnimation.getFrameImage();
            } else {
                currentImage = walkRightAnimation.getFrameImage();
            }

            e.drawImage( currentImage, pos.x - dim.x / 2, pos.y - dim.y / 2 );

            /*cpLeft.fill( e, Engine.RED );
            cpRight.fill( e, Engine.GREEN );
            cpUp.fill( e, Engine.VIOLET );
            cpDown.fill( e, Engine.GOLD );*/
            
        }
        
    }
    
    public void updateCollisionProbes() {
        
        cpLeft.x = pos.x - dim.x / 2;
        cpLeft.y = pos.y - cpLeft.height / 2;
        
        cpRight.x = pos.x + dim.x / 2 - cpRight.width;
        cpRight.y = pos.y - cpRight.height / 2;
        
        cpUp.x = pos.x - cpUp.width / 2;
        cpUp.y = pos.y - dim.y / 2;
        
        cpDown.x = pos.x - cpDown.width / 2;
        cpDown.y = pos.y + dim.y / 2 - cpDown.height;
        
    }
    
    public void setOnGround() {
        vel.y = 0.0;
        state = State.ON_GROUND;
    }
    
    public Rectangle getBoundingBox() {
        return new Rectangle( pos.x - dim.x / 2, pos.y - dim.y / 2, dim.x, dim.y );
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

package jsgeplatformer;

import br.com.davidbuzatto.jsge.core.Engine;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.geom.Vector2;
import br.com.davidbuzatto.jsge.utils.CollisionUtils;
import java.awt.Color;

/**
 * Jogador.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Player {
    
    public static enum State {
        JUMPING,
        FALLING,
        ON_GROUND
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
    public double runSpeed;
    public double jumpSpeed;
    public Color color;
    public State state;
    
    private int jumps;
    private static final int MAX_JUMPS = 2;
    
    private Rectangle cpLeft;
    private Rectangle cpRight;
    private Rectangle cpUp;
    private Rectangle cpDown;

    public Player( Vector2 pos, Vector2 dim, double walkSpeed, double runSpeed, double jumpSpeed, Color color ) {
        
        this.pos = pos;
        this.prevPos = new Vector2();
        this.dim = dim;
        this.vel = new Vector2();
        this.walkSpeed = walkSpeed;
        this.runSpeed = runSpeed;
        this.jumpSpeed = jumpSpeed;
        this.color = color;
        this.state = State.FALLING;
        this.jumps = 0;
        
        cpLeft = new Rectangle( 0, 0, 6, 20 );
        cpRight = new Rectangle( 0, 0, 6, 20 );
        cpUp = new Rectangle( 0, 0, 12, 6 );
        cpDown = new Rectangle( 0, 0, 12, 6 );
        
    }
    
    public void update( double delta, Engine e ) {
        
        double speed;
        
        if ( e.isKeyDown( Engine.KEY_CONTROL ) ) {
            speed = runSpeed;
        } else {
            speed = walkSpeed;
        }
        
        if ( e.isKeyDown( Engine.KEY_LEFT ) ) {
            vel.x = -speed;
        } else if ( e.isKeyDown( Engine.KEY_RIGHT ) ) {
            vel.x = speed;
        } else {
            vel.x = 0;
        }
        
        if ( e.isKeyPressed( Engine.KEY_SPACE ) && jumps < MAX_JUMPS ) {
            vel.y = -jumpSpeed;
            jumps++;
        }
        
        pos.x += vel.x * delta;
        pos.y += vel.y * delta;
        
        vel.y += Main.GRAVITY;
        
        if ( vel.y >= Main.MAX_FALL_SPEED ) {
            vel.y = Main.MAX_FALL_SPEED;
        }
        
        if ( prevPos.y < pos.y ) {
            state = State.FALLING;
        } else if ( prevPos.y > pos.y ) {
            state = State.JUMPING;
        }
        
        updateCollisionProbes();
        prevPos.x = pos.x;
        prevPos.y = pos.y;
        
    }
    
    public void draw( Engine e ) {
        
        e.fillRectangle( pos.x - dim.x / 2, pos.y - dim.y / 2, dim.x, dim.y, color );
        e.drawRectangle( pos.x - dim.x / 2, pos.y - dim.y / 2, dim.x, dim.y, Engine.BLACK );
        
        /*cpLeft.fill( e, Engine.RED );
        cpRight.fill( e, Engine.GREEN );
        cpUp.fill( e, Engine.VIOLET );
        cpDown.fill( e, Engine.GOLD );*/
        
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
        vel.y = 0;
        state = State.ON_GROUND;
        jumps = 0;
    }
    
    public Rectangle getBoundingBox() {
        return new Rectangle( pos.x - dim.x / 2, pos.y - dim.y / 2, dim.x, dim.y );
    }
    
    public CollisionType checkCollision( Block block ) {
        
        Rectangle blockBB = block.getBoundingBox();
        
        if ( CollisionUtils.checkCollisionRectangles( cpLeft, blockBB ) ) {
            return CollisionType.LEFT;
        } else if ( CollisionUtils.checkCollisionRectangles( cpRight, blockBB ) ) {
            return CollisionType.RIGHT;
        } else if ( CollisionUtils.checkCollisionRectangles( cpUp, blockBB ) ) {
            return CollisionType.UP;
        } else if ( CollisionUtils.checkCollisionRectangles( cpDown, blockBB ) ) {
            return CollisionType.DOWN;
        }
        
        return CollisionType.NONE;
        
    }
    
}

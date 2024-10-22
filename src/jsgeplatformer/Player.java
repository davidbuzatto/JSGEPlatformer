package jsgeplatformer;

import br.com.davidbuzatto.jsge.core.Engine;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.geom.Vector2;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.sound.Sound;
import br.com.davidbuzatto.jsge.utils.CollisionUtils;
import br.com.davidbuzatto.jsge.utils.ImageUtils;
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
    public double runSpeed;
    public double jumpSpeed;
    public Color color;
    public State state;
    public Direction direction;
    
    private int jumps;
    private static final int MAX_JUMPS = 2;
    
    private Animation walkRightAnimation;
    private Animation walkLeftAnimation;
    
    private Image jumpRightImage;
    private Image jumpLeftImage;
    private Image jumpRunningRightImage;
    private Image jumpRunningLeftImage;
    private Image fallingRightImage;
    private Image fallingLeftImage;
    
    private Sound jumpSound;
    
    private Rectangle cpLeft;
    private Rectangle cpRight;
    private Rectangle cpUp;
    private Rectangle cpDown;
    
    boolean running;
    boolean idle = true;

    public Player( Vector2 pos, Vector2 dim, double walkSpeed, double runSpeed, 
                   double jumpSpeed, Sound jumpSound, Color color, 
                   Animation walkRightAnimation, Animation walkLeftAnimation,
                   Image jumpRightImage, Image jumpRunningRightImage, Image fallingRightImage ) {
        
        this.pos = pos;
        this.prevPos = new Vector2();
        this.dim = dim;
        this.vel = new Vector2();
        this.walkSpeed = walkSpeed;
        this.runSpeed = runSpeed;
        this.jumpSpeed = jumpSpeed;
        this.color = color;
        this.state = State.FALLING;
        this.direction = Direction.RIGHT;
        this.jumps = 0;
        
        this.jumpSound = jumpSound;
        
        this.walkRightAnimation = walkRightAnimation;
        this.walkLeftAnimation = walkLeftAnimation;
        
        this.jumpRightImage = jumpRightImage;
        this.jumpLeftImage = ImageUtils.imageFlipHorizontal( jumpRightImage );
        this.jumpRunningRightImage = jumpRunningRightImage;
        this.jumpRunningLeftImage = ImageUtils.imageFlipHorizontal( jumpRunningRightImage );
        this.fallingRightImage = fallingRightImage;
        this.fallingLeftImage = ImageUtils.imageFlipHorizontal( fallingRightImage );
        
        cpLeft = new Rectangle( 0, 0, 6, 20 );
        cpRight = new Rectangle( 0, 0, 6, 20 );
        cpUp = new Rectangle( 0, 0, 12, 6 );
        cpDown = new Rectangle( 0, 0, 12, 6 );
        
    }
    
    public void update( double delta, Engine e ) {
        
        double speed;
        
        if ( e.isKeyDown( Engine.KEY_CONTROL ) ) {
            speed = runSpeed;
            running = true;
        } else {
            speed = walkSpeed;
            running = false;
        }
        
        if ( e.isKeyDown( Engine.KEY_LEFT ) ) {
            vel.x = -speed;
            idle = false;
            direction = Direction.LEFT;
            walkRightAnimation.reset();
            if ( running ) {
                walkLeftAnimation.update( delta / 0.62 );
            } else {
                walkLeftAnimation.update( delta );
            }
        } else if ( e.isKeyDown( Engine.KEY_RIGHT ) ) {
            vel.x = speed;
            idle = false;
            direction = Direction.RIGHT;
            walkLeftAnimation.reset();
            if ( running ) {
                walkRightAnimation.update( delta / 0.62 );
            } else {
                walkRightAnimation.update( delta );
            }
        } else {
            vel.x = 0;
            idle = true;
        }
        
        if ( e.isKeyPressed( Engine.KEY_SPACE ) && jumps < MAX_JUMPS ) {
            vel.y = -jumpSpeed;
            jumps++;
            state = State.JUMPING;
            jumpSound.play();
            walkRightAnimation.reset();
            walkLeftAnimation.reset();
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
            } else if ( prevPos.y > pos.y ) {
                state = State.JUMPING;
            }
        }
        
        updateCollisionProbes();
        prevPos.x = pos.x;
        prevPos.y = pos.y;
        
    }
    
    public void draw( Engine e ) {
        
        Image currentImage;
        
        if ( state == State.JUMPING ) {
            if ( running ) {
                if ( direction == Direction.LEFT ) {
                    currentImage = jumpRunningLeftImage;
                } else {
                    currentImage = jumpRunningRightImage;
                }
            } else {
                if ( direction == Direction.LEFT ) {
                    currentImage = jumpLeftImage;
                } else {
                    currentImage = jumpRightImage;
                }
            }
        } else if ( state == State.FALLING ) {
            if ( direction == Direction.LEFT ) {
                currentImage = fallingLeftImage;
            } else {
                currentImage = fallingRightImage;
            }
        } else { // ON_GROUND
            if ( direction == Direction.LEFT ) {
                if ( idle ) {
                    currentImage = walkLeftAnimation.getIdleFrameImage();
                } else {
                    currentImage = walkLeftAnimation.getFrameImage();
                }
            } else {
                if ( idle ) {
                    currentImage = walkRightAnimation.getIdleFrameImage();
                } else {
                    currentImage = walkRightAnimation.getFrameImage();
                }

            }
        }
        
        e.drawImage( currentImage, pos.x - dim.x / 2, pos.y - dim.y / 2 );
        
        /*e.fillRectangle( pos.x - dim.x / 2, pos.y - dim.y / 2, dim.x, dim.y, color );
        e.drawRectangle( pos.x - dim.x / 2, pos.y - dim.y / 2, dim.x, dim.y, Engine.BLACK );*/
        
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
        vel.y = 0.0;
        state = State.ON_GROUND;
        jumps = 0;
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
    
    public boolean checkCollision( Coin coin ) {
        return CollisionUtils.checkCollisionRectangles( getBoundingBox(), coin.rect );
    }
    
}

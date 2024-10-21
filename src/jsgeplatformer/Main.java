package jsgeplatformer;

import br.com.davidbuzatto.jsge.core.Camera2D;
import br.com.davidbuzatto.jsge.core.Engine;
import br.com.davidbuzatto.jsge.geom.Vector2;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.utils.ColorUtils;
import br.com.davidbuzatto.jsge.utils.ImageUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSGE basic game template.
 *
 * @author Prof. Dr. David Buzatto
 */
public class Main extends Engine {

    public Main() {

        super(
            800, // width
            448, // height
            "JSGE Platformer", // title
            60, // target FPS
            false // antialiasing
        );

    }

    public static final double GRAVITY = 20;
    public static final double MAX_FALL_SPEED = 400;
    public static final double SPRITE_WIDTH = 32;

    private Player player;
    private List<Block> blocks;
    
    private Camera2D camera;
    private double worldWidth;
    private double worldHeight;
    
    private Map<Character, Image> tileImages;

    /**
     * Creates the game world.
     *
     * This method runs just one time during engine initialization.
     */
    @Override
    public void create() {

        List<Image> playerWalkRightImages = new ArrayList<>();
        playerWalkRightImages.add( loadImage( "resources/images/SmallMario_0.png" ) );
        playerWalkRightImages.add( loadImage( "resources/images/SmallMario_1.png" ) );
        Animation playerWalkRight = new Animation( 2, 0.15, playerWalkRightImages );
        
        List<Image> playerWalkLeftImages = new ArrayList<>();
        playerWalkLeftImages.add( ImageUtils.imageFlipHorizontal( playerWalkRightImages.get( 0 ) ) );
        playerWalkLeftImages.add( ImageUtils.imageFlipHorizontal( playerWalkRightImages.get( 1 ) ) );
        Animation playerWalkLeft = new Animation( 2, 0.15, playerWalkLeftImages );
        
        player = new Player(
            new Vector2( getScreenWidth() / 2, getScreenHeight() / 2 ),
            new Vector2( 32, 40 ),
            250,
            400,
            400,
            loadSound( "resources/sfx/jump.wav" ),
            BLUE,
            playerWalkRight,
            playerWalkLeft,
            loadImage( "resources/images/SmallMarioJumping_0.png"),
            loadImage( "resources/images/SmallMarioJumpingAndRunning_0.png"),
            loadImage( "resources/images/SmallMarioFalling_0.png")
        );
        
        tileImages = new HashMap<>();
        for ( char c = 'A'; c <= 'I'; c++ ) {
            tileImages.put( c, loadImage( String.format( "resources/images/tile_%c.png", c ) ) );
        }
        
        processMap(
            """
            D                                                              C
            D                                                              C
            D                                                              C
            D                                                              C
            D                                                              C
            DIIIIIIIIIIIIIIIIIIII    IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIC
            D                                                              C
            D                                                              C
            D                    IIIII                                     C
            D                                                              C
            D                                                              C
            D                IIIII                                         C
            D                                                              C
            D                                   EBBBBBBF                   C
            D            IIIII                  CAAAAAAD                   C
            D                          P     EBBGAAAAAAD                   C
            D                                CAAAAAAAAAD                   C
            HBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBGAAAAAAAAAHBBBBBBBBBBBBBBBBBBBG
            AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
            """
        );
        
        camera = new Camera2D();
        camera.offset.x = getScreenWidth() / 2;
        camera.offset.y = getScreenHeight() / 2;
        updateCamera();

    }

    /**
     * Reads user input and update game world.
     *
     * Input methods should be used here. You MUST NOT use any of the engine
     * drawing methods here.
     */
    @Override
    public void update() {

        double delta = getFrameTime();

        player.update( delta, this );
        resolveCollisionPlayerBlocks();
        
        updateCamera();

    }

    /**
     * Draws the game world.
     *
     * All drawing related operations MUST be performed here.
     */
    @Override
    public void draw() {

        clearBackground( WHITE );
        setFontSize( 20 );
        setFontStyle( FONT_BOLD );

        beginMode2D( camera );
        
        for ( Block b : blocks ) {
            b.draw( this );
        }
        player.draw( this );
        
        endMode2D();

        drawFPS( 20, 20 );

    }

    private void resolveCollisionPlayerBlocks() {
        
        for ( Block b : blocks ) {
            
            Player.CollisionType ct = player.checkCollision( b );
            
            switch ( ct ) {
                case LEFT:
                    player.pos.x = b.pos.x + b.dim.x + player.dim.x / 2;
                    break;
                case RIGHT:
                    player.pos.x = b.pos.x - player.dim.x / 2;
                    break;
                case UP:
                    player.pos.y = b.pos.y + b.dim.y + player.dim.y / 2;
                    player.vel.y = 0;
                    break;
                case DOWN:
                    player.pos.y = b.pos.y - player.dim.y / 2;
                    player.setOnGround();
                    break;
            }
            
            player.updateCollisionProbes();
            
        }
        
    }
    
    private void processMap( String map ) {

        blocks = new ArrayList<>();

        int line = 0;
        int column = 0;
        int maxColumn = 0;

        for ( String mapLine : map.split( "\n" ) ) {
            
            for ( char c : mapLine.toCharArray() ) {
                switch ( c ) {
                    case 'A':
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'E':
                    case 'F':
                    case 'G':
                    case 'H':
                    case 'I':
                        blocks.add( new Block(
                            new Vector2( 
                                column * SPRITE_WIDTH,
                                line * SPRITE_WIDTH
                            ),
                            new Vector2(
                                SPRITE_WIDTH,
                                SPRITE_WIDTH
                            ),
                            null,
                            tileImages.get( c )
                        ));
                        break;
                    case 'P':
                        player.pos = new Vector2( 
                            column * SPRITE_WIDTH, 
                            line * SPRITE_WIDTH
                        );
                        break;
                }
                
                column++;
                
                if ( maxColumn < column ) {
                    maxColumn = column;
                }
                
            }
            
            line++;
            column = 0;
            
        }
        
        worldWidth = maxColumn * SPRITE_WIDTH;
        worldHeight = line * SPRITE_WIDTH;

    }
    
    private void updateCamera() {
        
        if ( player.pos.x <= getScreenWidth() / 2 ) {
            camera.target.x = getScreenWidth() / 2;
        } else if ( player.pos.x >= worldWidth - getScreenWidth() / 2 ) {
            camera.target.x = worldWidth - getScreenWidth() / 2 ;
        } else {
            camera.target.x = player.pos.x;
        }
        
        if ( player.pos.y <= getScreenHeight() / 2 ) {
            camera.target.y = getScreenHeight() / 2;
        } else if ( player.pos.y >= worldHeight - getScreenHeight() / 2 ) {
            camera.target.y = worldHeight - getScreenHeight() / 2 ;
        } else {
            camera.target.y = player.pos.y;
        }
        
    }
    
    public static void main( String[] args ) {
        new Main();
    }

}

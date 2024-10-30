package jsgeplatformer;

import br.com.davidbuzatto.jsge.core.Camera2D;
import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.image.ImageUtils;
import br.com.davidbuzatto.jsge.sound.Music;
import br.com.davidbuzatto.jsge.sound.Sound;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSGE basic game template.
 *
 * @author Prof. Dr. David Buzatto
 */
public class Main extends EngineFrame {

    public Main() {
        super( 800, 448, "JSGE Platformer", 60, false );
    }

    public static final double GRAVITY = 20;
    public static final double MAX_FALL_SPEED = 400;
    public static final double SPRITE_WIDTH = 32;

    private Player player;
    private List<Block> blocks;
    
    private List<Coin> coins;
    private Animation baseCoinAnimation;
    private Sound coinSound;
    
    private List<Enemy> enemies;
    private Animation enemyWalkRight;
    private Animation enemyWalkLeft;
    private Sound kickSound;
    
    private Music music;
    
    private Camera2D camera;
    private double worldWidth;
    private double worldHeight;
    
    private Map<Character, Image> tileImages;
    private Image background;
    private int backgroundTimes;
    
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
        
        List<Image> coinImages = new ArrayList<>();
        coinImages.add( loadImage( "resources/images/Coin_0.png" ) );
        coinImages.add( loadImage( "resources/images/Coin_1.png" ) );
        coinImages.add( loadImage( "resources/images/Coin_2.png" ) );
        coinImages.add( loadImage( "resources/images/Coin_3.png" ) );
        baseCoinAnimation = new Animation( 4, 0.1, coinImages );
        
        List<Image> enemyWalkRightImages = new ArrayList<>();
        enemyWalkRightImages.add( loadImage( "resources/images/Goomba_0.png" ) );
        enemyWalkRightImages.add( loadImage( "resources/images/Goomba_1.png" ) );
        enemyWalkRight = new Animation( 2, 0.15, enemyWalkRightImages );
        
        List<Image> enemyWalkLeftImages = new ArrayList<>();
        enemyWalkLeftImages.add( ImageUtils.imageFlipHorizontal( enemyWalkRightImages.get( 0 ) ) );
        enemyWalkLeftImages.add( ImageUtils.imageFlipHorizontal( enemyWalkRightImages.get( 1 ) ) );
        enemyWalkLeft = new Animation( 2, 0.15, enemyWalkLeftImages );
        
        background = loadImage( "resources/images/background1.png" );
        
        coinSound = loadSound( "resources/sfx/coin.wav" );
        kickSound = loadSound( "resources/sfx/kick.wav" );
        music = loadMusic( "resources/musics/music1.mp3" );
        music.play();
        
        player = new Player(
            new Rectangle( 
                getScreenWidth() / 2, getScreenHeight() / 2,
                32, 40
            ),
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
            D    P       IIIII                  CAAAAAAD                   C
            D                                EBBGAAAAAAD                   C
            D      o o o o o    e  e  e      CAAAAAAAAAD                   C
            HBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBGAAAAAAAAAHBBBBBBBBBBBBBBBBBBBG
            AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
            """
        );
        
        backgroundTimes = (int) ( worldWidth / background.getWidth() + 1 );
        
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
        
        for ( Coin c : coins ) {
            c.update( delta );
        }
        
        for ( Enemy e : enemies ) {
            e.update( delta, this );
        }
        
        resolveCollisionPlayerBlocks();
        resolveCollisionPlayerCoins();
        resolveCollisionEnemiesBlocks();
        resolveCollisionPlayerEnemies();
        
        if ( music.isStopped() ) {
            music.play();
        }
        
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

        beginMode2D( camera );
        
        for ( int i = 0; i < backgroundTimes; i++ ) {
            drawImage( background, i * background.getWidth(), worldHeight - background.getHeight(), SKYBLUE );
        }
        
        for ( Block b : blocks ) {
            b.draw( this );
        }
        
        for ( Coin c : coins ) {
            c.draw( this );
        }
        
        for ( Enemy e : enemies ) {
            e.draw( this );
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
                    player.rect.x = b.rect.x + b.rect.width + player.rect.width / 2;
                    break;
                case RIGHT:
                    player.rect.x = b.rect.x - player.rect.width / 2;
                    break;
                case UP:
                    player.rect.y = b.rect.y + b.rect.height + player.rect.height / 2;
                    player.vel.y = 0;
                    break;
                case DOWN:
                    player.rect.y = b.rect.y - player.rect.height / 2;
                    player.setOnGround();
                    break;
            }
            
            player.updateCollisionProbes();
            
        }
        
    }
    
    private void resolveCollisionPlayerEnemies() {
        
        for ( Enemy e : enemies ) {
            
            if ( !e.dead ) {
                
                Player.CollisionType ct = player.checkCollision( e );

                switch ( ct ) {
                    case LEFT:
                        break;
                    case RIGHT:
                        break;
                    case UP:
                        break;
                    case DOWN:
                        player.rect.y = e.rect.y - e.rect.height / 2 - player.rect.height / 2;
                        player.jump( true );
                        player.updateCollisionProbes();
                        e.kill();
                        break;
                }
                
            }
            
        }
        
    }
    
    private void resolveCollisionEnemiesBlocks() {
        
        for ( Enemy e : enemies ) {
            
            for ( Block b : blocks ) {

                Enemy.CollisionType ct = e.checkCollision( b );

                switch ( ct ) {
                    case LEFT:
                        e.rect.x = b.rect.x + b.rect.width + e.rect.width / 2;
                        e.turn();
                        break;
                    case RIGHT:
                        e.rect.x = b.rect.x - e.rect.width / 2;
                        e.turn();
                        break;
                    case UP:
                        e.rect.y = b.rect.y + b.rect.height + e.rect.height / 2;
                        e.vel.y = 0;
                        break;
                    case DOWN:
                        e.rect.y = b.rect.y - e.rect.height / 2;
                        e.setOnGround();
                        break;
                }

                e.updateCollisionProbes();

            }
            
        }
        
    }
    
    private void resolveCollisionPlayerCoins() {
        
        for ( Coin c : coins ) {
            if ( !c.collected ) {
                if ( player.checkCollision( c ) ) {
                    c.collect();
                }
            }
        }
        
    }
    
    private void processMap( String map ) {

        blocks = new ArrayList<>();
        coins = new ArrayList<>();
        enemies = new ArrayList<>();

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
                            new Rectangle( 
                                column * SPRITE_WIDTH,
                                line * SPRITE_WIDTH,
                                SPRITE_WIDTH,
                                SPRITE_WIDTH
                            ),
                            null,
                            tileImages.get( c )
                        ));
                        break;
                    case 'o':
                        coins.add( new Coin( 
                            new Rectangle(
                                column * SPRITE_WIDTH,
                                line * SPRITE_WIDTH,
                                24, 32
                            ),
                            baseCoinAnimation.copy(),
                            coinSound
                        ));
                        break;
                    case 'e':
                        enemies.add( new Enemy( 
                            new Rectangle(
                                column * SPRITE_WIDTH,
                                line * SPRITE_WIDTH,
                                SPRITE_WIDTH,
                                SPRITE_WIDTH
                            ),
                            150, RED,
                            enemyWalkRight.copy(),
                            enemyWalkLeft.copy(),
                            kickSound
                        ));
                        break;
                    case 'P':
                        player.rect.x = column * SPRITE_WIDTH;
                        player.rect.y = line * SPRITE_WIDTH;
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
        
        if ( player.rect.x <= getScreenWidth() / 2 ) {
            camera.target.x = getScreenWidth() / 2;
        } else if ( player.rect.x >= worldWidth - getScreenWidth() / 2 ) {
            camera.target.x = worldWidth - getScreenWidth() / 2 ;
        } else {
            camera.target.x = player.rect.x;
        }
        
        if ( player.rect.y <= getScreenHeight() / 2 ) {
            camera.target.y = getScreenHeight() / 2;
        } else if ( player.rect.y >= worldHeight - getScreenHeight() / 2 ) {
            camera.target.y = worldHeight - getScreenHeight() / 2 ;
        } else {
            camera.target.y = player.rect.y;
        }
        
    }
    
    public static void main( String[] args ) {
        new Main();
    }

}

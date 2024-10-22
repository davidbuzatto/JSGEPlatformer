package jsgeplatformer;

import br.com.davidbuzatto.jsge.core.Engine;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.sound.Sound;

/**
 *
 * @author Prof. Dr. David Buzatto
 */
public class Coin {
    
    public Rectangle rect;
    private Animation animation;
    private Sound sound;
    public boolean collected;

    public Coin( Rectangle rect, Animation animation, Sound sound ) {
        this.rect = rect;
        this.animation = animation;
        this.sound = sound;
    }
    
    public void update( double delta ) {
        animation.update( delta );
    }
    
    public void draw( Engine e ) {
        if ( !collected ) {
            e.drawImage( animation.getFrameImage(), rect.x, rect.y );
        }
    }
    
    public void collect() {
        sound.play();
        collected = true;
    }
    
}

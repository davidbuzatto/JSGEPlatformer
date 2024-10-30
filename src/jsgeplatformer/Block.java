package jsgeplatformer;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.core.utils.ColorUtils;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.image.Image;
import java.awt.Color;

/**
 * Bloco.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Block {
    
    public Rectangle rect;
    public Color color;
    public Image image;

    public Block( Rectangle rect, Color color, Image image ) {
        this.rect = rect;
        this.color = color;
        this.image = image;
    }
    
    public void draw( EngineFrame e ) {
        e.drawImage( image, rect.x, rect.y );
        if ( color != null ) {
            e.fillRectangle( rect, ColorUtils.fade( color, 0.5 ) );
        }
    }
    
    public Rectangle getBoundingBox() {
        return rect;
    }
    
}

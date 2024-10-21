package jsgeplatformer;

import br.com.davidbuzatto.jsge.core.Engine;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.geom.Vector2;
import br.com.davidbuzatto.jsge.image.Image;
import java.awt.Color;

/**
 * Bloco.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Block {
    
    public Vector2 pos;
    public Vector2 dim;
    public Color color;
    public Image image;

    public Block( Vector2 pos, Vector2 dim, Color color, Image image ) {
        this.pos = pos;
        this.dim = dim;
        this.color = color;
        this.image = image;
    }
    
    public void draw( Engine e ) {
        e.drawImage( image, pos.x, pos.y );
    }
    
    public Rectangle getBoundingBox() {
        return new Rectangle( pos.x, pos.y, dim.x, dim.y );
    }
    
}

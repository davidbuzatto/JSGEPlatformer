package jsgeplatformer;

import br.com.davidbuzatto.jsge.image.Image;
import java.util.List;

/**
 * Uma animação.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Animation {
    
    private int totalFrames;
    private int currentFrame;
    private double timeCounter;
    private double timeToNextFrame;
    private List<Image> images;

    public Animation( int totalFrames, double timeToNextFrame, List<Image> images ) {
        this.totalFrames = totalFrames;
        this.timeToNextFrame = timeToNextFrame;
        this.images = images;
    }
    
    public void reset() {
        currentFrame = 0;
        timeCounter = 0;
    }
    
    public Image getFrameImage() {
        return images.get( currentFrame );
    }
    
    public Image getIdleFrameImage() {
        return images.get( 0 );
    }
    
    public void update( double delta ) {
        timeCounter += delta;
        if ( timeCounter >= timeToNextFrame ) {
            timeCounter = 0;
            currentFrame = ( currentFrame + 1 ) % totalFrames;
        }
    }
    
}

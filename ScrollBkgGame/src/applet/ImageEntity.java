/*********************************************************
 * Base game image class for bitmapped game entities
 **********************************************************/
package applet;
import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.net.*;
import java.applet.*;

public class ImageEntity extends BaseGameEntity {
    //variables
    protected Image image;
    protected Applet applet;
    protected AffineTransform at;
    protected Graphics2D g2d;
    private static int imageCount=1;

    //default constructor
    ImageEntity(Applet a) {
        applet = a;
        setImage(null);
        setAlive(true);
    }

    public Image getImage() { return image; }

    public void setImage(Image image) {
        this.image = image;
        double x = applet.getSize().width/2  - width()/2;
        double y = applet.getSize().height/2 - height()/2;
        at = AffineTransform.getTranslateInstance(x, y);
    }

    public int width() {
        if (image != null)
            return image.getWidth(applet);
        else
            return 0;
    }
    public int height() {
        if (image != null)
            return image.getHeight(applet);
        else
            return 0;
    }

    public double getCenterX() {
        return getX() + width() / 2;
    }
    public double getCenterY() {
        return getY() + height() / 2;
    }

    public void setGraphics(Graphics2D g) {
        g2d = g;
    }


    public void load(String filename) 
    {
      URL url = null;
      try {
            String codeBase=applet.getCodeBase().toString();
            //url = new URL(codeBase+"resources/images/"+filename);
            url=this.getClass().getResource("/resources/images/"+filename);
           } catch (Exception e) { e.printStackTrace(); }

      image = applet.getImage(url);
        
      double x = applet.getSize().width/2  - width()/2;
      double y = applet.getSize().height/2 - height()/2;
      at = AffineTransform.getTranslateInstance(x, y);
    }

    public void transform() {
        at.setToIdentity();
        at.translate((int)getX() + width()/2, (int)getY() + height()/2);
        at.rotate(Math.toRadians(getFaceAngle()));
        at.translate(-width()/2, -height()/2);
    }

    public void draw() {
        g2d.drawImage(getImage(), at, applet);
    }

    //bounding rectangle
    public Rectangle getBounds() {
        Rectangle r;
        r = new Rectangle((int)getX(), (int)getY(), width(), height());
        return r;
    }

}

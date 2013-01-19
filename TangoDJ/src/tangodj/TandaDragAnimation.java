package tangodj;

import java.util.ArrayList;
import java.util.Iterator;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TandaDragAnimation extends Group
{
    public int dragStartIndex=0;
    public int dragFinishIndex=0;
    public long startDragTime=0;
    public GridPane gp = new GridPane();
    private Tanda tanda;
    double startPosition=0;
 

  	public TandaDragAnimation(Tanda tanda, double startPosition)
  	{
  	  this.tanda=tanda;
  	  this.startPosition=startPosition;
  	  gp = new GridPane();
	  gp.setPadding(new Insets(10, 10, 10, 10));
	  gp.setVgap(0);
	  gp.setHgap(0);
	  gp.setOpacity(.7);
  	  ArrayList<TrackRow> trs;
	  TrackRow tr=null;
	  TrackRow trt;
	  int row=0;
  	  trs=tanda.getTrackRows();
	  Iterator<TrackRow> itx = trs.iterator();
	  while(itx.hasNext())
	  {
	    trt = itx.next();
	    tr = trt.clone(trt);
	    gp.add(tr.getIndicator(), 0, row);
	    gp.add(tr.getTrackNumberLabel(), 1, row);
	    gp.add(tr.getGroupingLabel(), 2, row);
	    gp.add(tr.getArtistLabel(), 3, row);
	    gp.add(tr.getTrackTitleLabel(), 4, row);
	    row++;
	}
	
	this.getChildren().clear();
	gp.setLayoutX(0);
	gp.setLayoutY(tanda.getPosition());
	
	double tandaHeight=row*22.188;
	double tandaWidth=525;
	Rectangle voidBox = new Rectangle(tandaWidth, tandaHeight);
	voidBox.setX(50);
	voidBox.setY(tanda.getPosition()+8);
	voidBox.setFill(Color.LIGHTGRAY);
	voidBox.setOpacity(.8);
	
	Rectangle destMarker = new Rectangle(75, 10);
	destMarker.setFill(Color.YELLOW);
	destMarker.setStrokeWidth(2);
	destMarker.setStroke(Color.RED);
	destMarker.setX(0);
	destMarker.setY(tanda.getPosition());
	
	this.getChildren().add(voidBox);
	this.getChildren().add(destMarker);
	this.getChildren().add(gp);
  	}
  	
  	public void move(double newPosition)
  	{
  		gp.setLayoutY(tanda.getPosition()-(startPosition-newPosition));	
  	}
}
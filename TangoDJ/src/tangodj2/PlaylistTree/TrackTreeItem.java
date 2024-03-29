package tangodj2.PlaylistTree;

import java.sql.SQLException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import tangodj2.Db;
import tangodj2.SharedValues;
import tangodj2.TrackDb;

public class TrackTreeItem extends BaseTreeItem
{
  private String trackHash;
  private int type = 0;
  private int tandaDbId=-1;
  public static Image greenCheckBallImage;
  public static Image dimBallImage;
  

  private String album;
  private String path;
  private String artist;
  private double duration;
  private String singer;
  private String comment;
  private int delay=0;
  private int dbId=-1;
  
  
  public TrackTreeItem(String trackHash, int position)
  {
  	super();
  	this.trackHash=trackHash;
  	this.setTreeType("tango");
   
  	setGraphic(new ImageView(gray_light));
  	
  	//System.out.println("TrackTreeItem: "+trackHash);
  	TrackDb trackDb=Db.getTrackInfo(trackHash);
  	
  	if (trackDb.cleanup==1) setTreeType("cleanup");
  	album = trackDb.album;
  	path = trackDb.path;
  	this.artist = trackDb.artist;
  	this.duration=trackDb.duration;
  	this.singer=trackDb.singer;
  	this.comment=trackDb.comment;
  	this.delay=trackDb.delay;
  	this.dbId=trackDb.id;
  	this.setValue(trackDb.title);
  	
  	//this.getChildren().add(new DetailTreeItem("Length", formatTime(new Duration(TrackDb.duration))));
    //this.getChildren().add(new DetailTreeItem("Artist", artist));
    //this.getChildren().add(new DetailTreeItem("Album", album));
       
  }
    
    
  public String getTandaAndTrackPosition()
  {
    TandaTreeItem tandaTreeItem = (TandaTreeItem)getParent();
    PlaylistTreeItem playlistTreeItem = (PlaylistTreeItem)tandaTreeItem.getParent();
    int tandaIndex = playlistTreeItem.getTandaPosition(tandaTreeItem);
    int trackIndex = tandaTreeItem.getChildren().indexOf(this);
    return tandaIndex+","+trackIndex;
  }
  
  public int getTrackPosition(TrackTreeItem tti)
  {
	  TandaTreeItem tandaTreeItem = (TandaTreeItem)getParent();
	  int trackIndex = tandaTreeItem.getChildren().indexOf(tti);
	  return trackIndex;
  }
  
  public int getTrackPosition()
  {
    TandaTreeItem tandaTreeItem = (TandaTreeItem)getParent();
    int trackIndex = tandaTreeItem.getChildren().indexOf(this);
    return trackIndex;
  }

  public String getTrackHash() {
	return trackHash;
  }

  public void setTrackHash(String trackHash) {
	this.trackHash = trackHash;
  }

  public int getType() {
	return type;
  }

  public void setType(int type) {
	this.type = type;
  }

    
  private String formatDuration(int sec) {
    
    int seconds = (int)( sec  % 60);
    int minutes = (int) (sec /  60);
    return String.format("%02d:%02d", minutes, seconds);
  }

public int getTandaDbId() {
	return tandaDbId;
}

public TandaTreeItem getTanda()
{
  return (TandaTreeItem)getParent();
}

public void setTandaDbId(int tandaDbId) {
	this.tandaDbId = tandaDbId;
}


public String getAlbum()
{
  return album;
}


public void setAlbum(String album)
{
  this.album = album;
}


public String getPath()
{
  return path;
}


public void setPath(String path)
{
  this.path = path;
}

private static String formatTime(Duration duration) 
{
  if (duration.greaterThan(Duration.ZERO)) 
  {
    int intDuration = (int) Math.floor(duration.toSeconds());
    int durationHours = intDuration / (60 * 60);
    if (durationHours > 0) { intDuration -= durationHours * 60 * 60; }
    int durationMinutes = intDuration / 60;
    int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
    if (durationHours > 0) 
    {
      return String.format("%d:%02d:%02d", 
                    durationHours, durationMinutes, durationSeconds);
     } 
     else 
     {
       return String.format("%02d:%02d",
                   durationMinutes,
                    durationSeconds);
     }
   }
  else return "00:00";
  
   
 }






public String getArtist() {
	return artist;
}






public double getDuration()
{
  return duration;
}






public String getSinger()
{
  return singer;
}






public String getComment()
{
  return comment;
}


public int getDelay()
{
  return delay;
}


public void setDelay(int delay)
{
  this.delay = delay;
}

public boolean isDelay()
{
  if (delay>0) return true;
  return false;
}


public int getDbId()
{
  return dbId;
}






}

package tangodj;

import java.util.concurrent.TimeUnit;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class TrackRow 
{
	private String artistName;
	private String path; 
	private String groupingName;
	private String trackTitle;
	private int trackNumber=0;
	
	private Label trackTitleLabel; 
	private Label artistLabel; 
	private Label groupingLabel; 
	private Label  timeLabel;
	private Label trackLabel;
	
	
	private static int pIndex=0;
	private ImageView nowPlaying = new ImageView();
	private ImageView selected = new ImageView();
	private Image greenLightImage;
	private Image noLightImage;
	private Image greyLightImage;
	private Image selectedArrowImage;
	private int fontSize=18;
	private static String lastGrouping="";
	private static String lastArtist="";
	private boolean groupingVisible=true;
	private boolean nowPlayingIndicated=false;
	private boolean selectedIndicated=false;
	private StackPane indicator = new StackPane();
	private int tandaNumber=0;
	private int tandaTrackNumber=0;
	
	
	public int getTandaNumber() {
		return tandaNumber;
	}

	public void setTandaNumber(int tandaNumber) {
		this.tandaNumber = tandaNumber;
	}

	public TrackRow(String trackTitle, String artistName, String ipath, String grouping, int  time, int trackNumber)
	{
	  this.trackTitle = trackTitle;
	  this.artistName=artistName;
		greenLightImage = new Image(TangoDJ.class.getResourceAsStream("/resources/images/green_light.png"));
		noLightImage = new Image(TangoDJ.class.getResourceAsStream("/resources/images/no_light.png"));
		greyLightImage = new Image(TangoDJ.class.getResourceAsStream("/resources/images/gray_light.png"));
		selectedArrowImage = new Image(TangoDJ.class.getResourceAsStream("/resources/images/selected_arrow.png"));
		
		String cssBkgColor = "tangoBkg";
		
		indicator.getChildren().add(selected);
		indicator.getChildren().add(nowPlaying);
		
		EventHandler <MouseEvent>bHandler = new EventHandler<MouseEvent>() {
	          public void handle(MouseEvent event)  { setIndex(); }};
	          
			          
		if (grouping!=null)
		{
		  if (grouping.toLowerCase().equals("vals")) cssBkgColor = "valsBkg";
		  else if (grouping.toLowerCase().equals("milonga")) cssBkgColor = "milongaBkg";
		  else if (grouping.toLowerCase().equals("cortina")) cssBkgColor = "cortinaBkg";
		  else if (grouping.toLowerCase().equals("padding")) cssBkgColor = "paddingBkg";
		  
		  groupingName=grouping;
		  
		  if (!lastGrouping.equals(grouping))
			{
			  groupingLabel=textLabel(grouping, 100, cssBkgColor);
			  lastGrouping=grouping;
			}
			else groupingLabel=textLabel("", 100, cssBkgColor);
			
			if (grouping.toLowerCase().equals("padding")) 
			{	
				//System.out.println("padding");
			  groupingLabel.setText("");
			}
			groupingLabel.setOnMouseClicked(bHandler);
		}
		else 
		{	
		   groupingLabel=textLabel("", 100, cssBkgColor);
		   groupingName=lastGrouping;
		}	
		
		if (groupingName==null) groupingName="General";
		
		this.trackTitleLabel=textLabel(trackTitle, 200, cssBkgColor);
		
		
		  // holds the name even though it might be surpressed in the table view.
		if (!lastArtist.equals(artistName))
		{
		  this.artistLabel=textLabel(artistName, 200, cssBkgColor);
		  lastArtist=artistName;
		}
		else this.artistLabel=textLabel("", 200, cssBkgColor);
		
		//this.artist=textLabel(iartist, 200, cssBkgColor);
		this.path=ipath;
		
		timeLabel=textLabel(getTime(time), 100, cssBkgColor);
		this.trackNumber=trackNumber;
		this.trackLabel=textLabel(""+(trackNumber+1)+") ", 50, cssBkgColor);
		trackLabel.setAlignment(Pos.CENTER_RIGHT);
		
		this.trackLabel.setOnMouseClicked(bHandler);
		this.trackTitleLabel.setOnMouseClicked(bHandler);
		this.artistLabel.setOnMouseClicked(bHandler);
		
		timeLabel.setOnMouseClicked(bHandler);
		nowPlaying.setOnMouseClicked(bHandler);
		selected.setOnMouseClicked(bHandler);
		nowPlaying.setImage(noLightImage);
		
	}
	
	public void setNowPlayingIndicatorBall()
	{
		nowPlaying.setImage(greenLightImage);
		nowPlayingIndicated=true;
	}
	
	public void setNotPlayingIndicatorBall()
	{
	  nowPlaying.setImage(noLightImage);
	  nowPlayingIndicated=false;
	}
		
	public void setSelectedIndicatorBall()
	{
	   selected.setImage(selectedArrowImage);
	   selectedIndicated=true;
	}
	
	public void setNotSelectedIndicatorBall()
	{
	  selected.setImage(noLightImage);
	  selectedIndicated=false;
	}
	
	private void setIndex()
	{
		//System.out.println("TrackRow: "+index);
		pIndex=trackNumber;
	}
	
	public static int getPindex()
	{
	  return pIndex;	
	}
	
	private Label textLabel(String text, int width, String cssBkgColor)
	{
	   Label label = new Label(" "+text);
	   label.setPrefWidth(width);
	   label.getStyleClass().add(cssBkgColor);
	   label.setFont(new Font("Cambria", fontSize));
	  // label.setStyle("tangoBkg");
	   return label;
	}
	
	public String getTime(int time) {
    	return String.format("%d:%d", 
    		    TimeUnit.MILLISECONDS.toMinutes(time),
    		    TimeUnit.MILLISECONDS.toSeconds(time) - 
    		    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
    		);
        //return time.get();
    }

	public String getArtistName() {
		return artistName;
	}
	
	public void setTandaInfo(int tandaNumber, int tandaTrackNumber, int trackNumber)
	{
		this.tandaNumber=tandaNumber;
		this.tandaTrackNumber=tandaTrackNumber;
		this.trackNumber=trackNumber;
		this.trackLabel.setText(""+(trackNumber+1)+") ");
		trackLabel.setAlignment(Pos.CENTER_RIGHT);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String trackTitle) {
		this.trackTitle = trackTitle;
	}

	public StackPane getIndicator() {
		return indicator;
	}

	public void setIndicator(StackPane indicator) {
		this.indicator = indicator;
	}

	public Label getTrackTitleLabel() {
		return trackTitleLabel;
	}

	public void setTrackTitleLabel(Label trackTitleLabel) {
		this.trackTitleLabel = trackTitleLabel;
	}

	public Label getGroupingLabel() {
		return groupingLabel;
	}

	public void setGroupingLabel(Label groupingLabel) {
		this.groupingLabel = groupingLabel;
	}
}


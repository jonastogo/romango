

package tangodj2;

import java.io.File;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Player 
{
    private MediaView mediaView = new MediaView();
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private HBox mediaBar;
    private MediaPlayer mediaPlayer;
    final Button playButton;
    final Button stopButton;
    final Button skipButton;
    final Button previousButton;
    private boolean playing=false;
    private Equalizer eq;
    public final static int ALL_TRACKS=1;
    public final static int PLAYLIST=2;
    public final static int EVENT_PLAYLIST=3;
    Playlist playlist;
    int nextPlaylistTrack=0;
    Tab equalizerTab;
    VBox vbox = new VBox();
    int mode = ALL_TRACKS;
    GridPane gridPane;
    Label startPositionValue=null;
    Label endPositionValue=null;
    Label playPositionValue=null;
    Label cortinaLength=null;
    final int col[] = {0,1,2,3,4,5,6};
    final int row[] = {0,1,2,3,4,5,6};
    Duration currentTrackDuration = new Duration(0);
    Slider startPositionSlider=null;
    Slider endPositionSlider=null;
    Slider playPositionSlider=null;
    
    
   // SimpleStringProperty source = new SimpleStringProperty();
   

    public Player(Playlist playlist, Tab equalizerTab) 
    {
      this.playlist=playlist;
      this.equalizerTab=equalizerTab;
      
      
      setupListeners();
      mediaBar = new HBox();
      mediaBar.setSpacing(5);
      mediaBar.setStyle("-fx-background-color: #bfc2c7;");
      mediaBar.setAlignment(Pos.CENTER);
      mediaBar.setPadding(new Insets(5, 10, 5, 10));
      BorderPane.setAlignment(mediaBar, Pos.CENTER);

      previousButton = new Button("<<");
      previousButton.setOnAction(new EventHandler<ActionEvent>() 
      {
        public void handle(ActionEvent e) 
        {
          stopPlaying();
          playPreviousTrack();
        }
      });
      
      skipButton = new Button(">>");
      skipButton.setOnAction(new EventHandler<ActionEvent>() 
      {
        public void handle(ActionEvent e) 
        {
          stopPlaying();
          playTrack();
        }
      });
      
      playButton = new Button(">");
      stopButton = new Button("Stop");
      stopButton.setOnAction(new EventHandler<ActionEvent>() 
      {
        public void handle(ActionEvent e) 
        {
          stopPlaying();
        }
      });
      
      stopButton.setDisable(true);

      playButton.setOnAction(new EventHandler<ActionEvent>() 
      {
        public void handle(ActionEvent e) 
        {
          
          if (mediaPlayer == null) playTrack();
          else
          {  
            Status status = mediaPlayer.getStatus();
            System.out.println("Player, status: "+status.toString());
           
            if (status == Status.PLAYING) pauseTrack();
            else if (status == Status.PAUSED) resumeTrack();
            else playTrack();
          }
        }
      });
      
      mediaBar.getChildren().add(previousButton);
      mediaBar.getChildren().add(stopButton);
      mediaBar.getChildren().add(playButton);
      mediaBar.getChildren().add(skipButton);
      Label spacer = new Label("   ");
      mediaBar.getChildren().add(spacer);

      Label timeLabel = new Label("Time: ");
      mediaBar.getChildren().add(timeLabel);
 
      timeSlider = new Slider();
      HBox.setHgrow(timeSlider, Priority.ALWAYS);
      timeSlider.setMinWidth(50);
      timeSlider.setMaxWidth(Double.MAX_VALUE);
      
       mediaBar.getChildren().add(timeSlider);

       playTime = new Label();
       playTime.setPrefWidth(130);
       playTime.setMinWidth(50);
       mediaBar.getChildren().add(playTime);

       Label volumeLabel = new Label("Vol: ");
       mediaBar.getChildren().add(volumeLabel);

       volumeSlider = new Slider(0, 100, 50);
       volumeSlider.setPrefWidth(70);
       volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
       volumeSlider.setMinWidth(30);
       volumeSlider.setValue(50);
       
       
       volumeSlider.valueProperty().addListener(new InvalidationListener() 
       {
         public void invalidated(Observable ov) 
         {
           if (volumeSlider.isValueChanging()) 
           {
            // System.out.println("Playes, volumeSlider invalidation");
              mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
           }
         }
       });
      
       mediaBar.getChildren().add(volumeSlider);
       
       
       timeSlider.valueProperty().addListener(new InvalidationListener() 
       {
         public void invalidated(Observable ov) 
         {
           if (timeSlider.isValueChanging()) 
           {
             // multiply duration by percentage calculated by slider position
              mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
           }
         }
       });
       
       vbox.getChildren().add(mediaBar);
       
       
       
    }
    
    public void setDefaultTrack()
    {
      TrackMeta trackMeta=null;
      if (SharedValues.allTracksData.size()>0)
      {
        Track firstTrack = SharedValues.allTracksData.get(0);
        SharedValues.selectedAllTracksPathHash.set(firstTrack.getPathHash());
        trackMeta = Db.getTrackInfo(SharedValues.selectedAllTracksPathHash.get());
        File file = new File(trackMeta.path);
        int totalTrackTime=trackMeta.duration;
        currentTrackDuration = new Duration(totalTrackTime*1000);
        System.out.println("Default Track Duration: "+formatTime(currentTrackDuration));
      }
      
      
    }

    public void showAdvancedControls(boolean show)
    {
      
      if (show)
      {
        setupCortinaControls();
        vbox.getChildren().add(gridPane);
      }
      else vbox.getChildren().remove(gridPane);
    }
    
    
    
    GridPane setupCortinaControls()
    {
      gridPane = new GridPane();
      gridPane.setPadding(new Insets(10, 10, 10, 10));
      gridPane.setStyle("-fx-background-color: DAE6F3; -fx-border-color: BLACK; -fx-border-style: SOLID; -fx-border-width: 3px;"); // border doesn't work
      gridPane.setHgap(15);
      
      startPositionSlider = new Slider(0, 1, 0);
     endPositionSlider = new Slider(0, 1, 1);
       playPositionSlider = new Slider(0, 1, 0);
      
      CheckBox fadeIn = new CheckBox("Fade In");
      CheckBox fadeOut = new CheckBox("Fade Out");
      CheckBox delay = new CheckBox("3 Sec delay at end");
      
      startPositionValue = new Label("00:00");
      System.out.println("Default Track Duration2: "+formatTime(currentTrackDuration));
      endPositionValue = new Label(formatTime(currentTrackDuration));
      playPositionValue = new Label(Double.toString(playPositionSlider.getValue()));
      cortinaLength=new Label("0:00");
    
      
      gridPane.add(new Text("Start Position"), col[0], row[0]);
      gridPane.add(new Text("End Position"),   col[0], row[1]);
      gridPane.add(new Text("Play Position"),  col[0], row[2]);
      
      
      gridPane.add(startPositionValue,         col[1], row[0]);
      gridPane.add(endPositionValue,           col[1], row[1]);
      gridPane.add(playPositionValue,          col[1], row[2]);
      
      gridPane.add(startPositionSlider,        col[2], row[0]);
      gridPane.add(endPositionSlider,          col[2], row[1]);
      gridPane.add(playPositionSlider,         col[2], row[2]);
      
      gridPane.add(fadeIn,                     col[3], row[0]);
      gridPane.add(fadeOut,                    col[3], row[1]);
      gridPane.add(delay,                      col[3], row[2]);
      
      gridPane.add(new Text("Cortna Length: "),col[4], row[0]);
      gridPane.add(cortinaLength              ,col[5], row[0]);
      
      
      
      
      startPositionSlider.valueProperty().addListener(new ChangeListener<Number>() 
      {
          public void changed(ObservableValue<? extends Number> arg0,
                  Number arg1, Number arg2) 
          {
            float sliderPercent  = arg2.floatValue();
            startPositionValue.setText(formatTime(currentTrackDuration.multiply(sliderPercent))); //new value
          }
      });
      
      endPositionSlider.valueProperty().addListener(new ChangeListener<Number>() 
      {
          public void changed(ObservableValue<? extends Number> arg0,
                  Number arg1, Number arg2) 
          {
            endPositionValue.setText(String.format("%.1f", arg2)); //new value
          }
      });
      
      playPositionSlider.valueProperty().addListener(new ChangeListener<Number>() 
      {
          public void changed(ObservableValue<? extends Number> arg0,
                  Number arg1, Number arg2) 
          {
            playPositionValue.setText(String.format("%.1f", arg2)); //new value
          }
      });
      
      
      return gridPane;
    }
    
    public void setMode(int mode)
    {
      if (this.mode==mode) return;
      this.mode=mode;
    }
    
    public VBox get()
    {
      return vbox;
    }
    
    private void pauseTrack()
    {
      mediaPlayer.pause();
      playButton.setText(">");
    }
    
    private void resumeTrack()
    {
      mediaPlayer.play();
    }
    
    private void stopPlaying()
    {
      mediaPlayer.stop();
      stopButton.setDisable(true);
      playButton.setText(">");
      playing=false;
    }   
    
    public void playPreviousTrack()
    {
      playlist.setPrevious();
      playTrack();
    }
    
    public void playTrack()
    {
      String sourcePath;
      TrackMeta trackMeta=null;
      
      if (mode==PLAYLIST)
      {
        PlaylistTrack playlistTrack=playlist.getNextTrack();
        if (playlistTrack==null) return;
        sourcePath=playlistTrack.path;
       //System.out.println("Playing from playlist: "+playlistTrack.title);
      }
      else 
      {
        String trackHash=SharedValues.selectedAllTracksPathHash.get();
        if (trackHash==null) return;
        trackMeta = Db.getTrackInfo(SharedValues.selectedAllTracksPathHash.get());
        sourcePath=trackMeta.path;
      }
      
      if (sourcePath==null)
      {
        System.out.println("Source Path is null");
        return;
      }
      
      
      playing=true;
      playButton.setText("||");
      stopButton.setDisable(false);
     // 
      File file = new File(sourcePath);
      
      mediaPlayer = createMediaPlayer(file.toURI().toString(), true);
      mediaPlayer.setVolume(volumeSlider.getValue());
      
      int totalTrackTime=trackMeta.duration;
      
      currentTrackDuration = new Duration(totalTrackTime*1000);
      
      System.out.println("Total Time: "+totalTrackTime);
      
      double startFraction = startPositionSlider.getValue();
      double endFraction = endPositionSlider.getValue();
      
      Duration startTime=currentTrackDuration.multiply(startFraction);
      Duration endTime=currentTrackDuration.multiply(endFraction);
      
      Duration cortinaTime = endTime.subtract(startTime);
      
      
      System.out.println("Start Time: "+ startTime);
      mediaPlayer.setStartTime(startTime);
      
      mediaView = new MediaView(mediaPlayer);

      eq = new Equalizer(mediaPlayer);
      equalizerTab.setContent(eq.getGridPane());
      
      /*
      Status status = mediaPlayer.getStatus();
      System.out.println("Player, status: "+status.toString());
      if (status == Status.UNKNOWN || status == Status.HALTED) { return; }
      if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) 
      {
        if (atEndOfMedia) 
        {
          mediaPlayer.seek(mediaPlayer.getStartTime());
          atEndOfMedia = false;
        }
        mediaPlayer.play();
      } 
      else 
      {
        mediaPlayer.pause();
      }
      */
      
      
      mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() 
      {
        public void invalidated(Observable ov) 
        {
          updateValues();
        }
      });
   
      mediaPlayer.setOnPlaying(new Runnable() 
      {
        public void run() 
        {
         // System.out.println("Playlist - mediaPlayer.setOnPlaying");
          
        
          /*
          if (stopRequested) 
          {
            mediaPlayer.pause();
            stopRequested = false;
          } 
          else 
          {
            playButton.setText("||");
          }
           */
        }
       
        
      });

      mediaPlayer.setOnPaused(new Runnable() 
      {
        public void run() 
        {
         // System.out.println("Playlist - mediaPlayer.setOnPaused");
          //System.out.println("onPaused");
          //playButton.setText(">");
        }
    });

    mediaPlayer.setOnReady(new Runnable() 
    {
      public void run() 
      {
       // System.out.println("Playlist - mediaPlayer.setOnReady");
        duration = mediaPlayer.getMedia().getDuration();
        updateValues();
      }
    });

    mediaPlayer.setOnEndOfMedia(new Runnable() 
    {
      public void run() 
      {
          stopButton.setDisable(true);
          playButton.setText(">");
          timeSlider.setValue(0);
          stopRequested = true;
          atEndOfMedia = true;
          mediaPlayer.stop();
          playTrack();
      }
    });
    
    
  }
    
    private MediaPlayer createMediaPlayer(String path, boolean autoPlay)
    {
      final String thisPath=path;
      Media  media = new Media(path);
      final MediaPlayer mp = new MediaPlayer(media);
      mp.getTotalDuration();
      mp.setOnError(new Runnable() 
      {
        public void run() 
        {
          System.out.println("Media error occurred: " + mp.getError());
          System.out.println("path: " + thisPath);
        }
      });
      mp.setAutoPlay(autoPlay);
      
      
      return mp;
    }
    
    protected void updateValues() 
    {
      //System.out.println("Player: update values");
      if (playTime != null && timeSlider != null && volumeSlider != null) 
      {
         Platform.runLater(new Runnable() 
         {
           public void run() 
           {
             Duration currentTime = mediaPlayer.getCurrentTime();
             playTime.setText(formatTime(currentTime, duration));
             timeSlider.setDisable(duration.isUnknown());
             if (!timeSlider.isDisabled()
                   && duration.greaterThan(Duration.ZERO)
                   && !timeSlider.isValueChanging()) 
             {
                timeSlider.setValue(currentTime.divide(duration).toMillis()* 100.0);
             }
             
            /*
             if (!volumeSlider.isValueChanging()) 
             {
               volumeSlider.setValue((int) Math.round(mediaPlayer.getVolume()* 100));
             }
            */
           }
         });
      }
    }

  private static String formatTime(Duration elapsed, Duration duration) 
  {
    int intElapsed = (int) Math.floor(elapsed.toSeconds());
    int elapsedHours = intElapsed / (60 * 60);
    if (elapsedHours > 0) 
    {
      intElapsed -= elapsedHours * 60 * 60;
    }
    int elapsedMinutes = intElapsed / 60;
    int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

    if (duration.greaterThan(Duration.ZERO)) 
    {
      int intDuration = (int) Math.floor(duration.toSeconds());
      int durationHours = intDuration / (60 * 60);
      if (durationHours > 0) { intDuration -= durationHours * 60 * 60; }
      int durationMinutes = intDuration / 60;
      int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
      if (durationHours > 0) 
      {
        return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds,
                      durationHours, durationMinutes, durationSeconds);
       } 
       else 
       {
         return String.format("%02d:%02d/%02d:%02d",
                      elapsedMinutes, elapsedSeconds, durationMinutes,
                      durationSeconds);
       }
     } 
     else 
     {
       if (elapsedHours > 0) 
       {
         return String.format("%d:%02d:%02d", elapsedHours,
                      elapsedMinutes, elapsedSeconds);
       } 
       else 
       {
         return String.format("%02d:%02d", elapsedMinutes,
                      elapsedSeconds);
       }
     }
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
  
  private void setupListeners() 
  {
    ChangeListener playlistFocusListener = new ChangeListener() 
    {
      public void changed(ObservableValue observable, Object oldValue, Object newValue) 
      {
        System.out.println("player mode set to PLAYLIST");
        setMode(PLAYLIST);
      }
    };   
    SharedValues.playlistFocus.addListener(playlistFocusListener);
  }
  
 }
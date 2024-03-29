package tangodj2;

import tangodj2.cleanup.CleanupTable;
import tangodj2.cleanup.CleanupTrack;
import tangodj2.cortina.CortinaTable;
import tangodj2.cortina.CortinaTrack;
import tangodj2.tango.TangoTable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CortinaTab extends Tab
{
 
  CleanupTable cleanupTable;
  CortinaTable cortinaTable;
  CortinaTab cortinaTab;
  GridPane gridPane = new GridPane();
  final Player player;
  
  public CortinaTab(Player player)
  {
    this.setText("Create Cortinas");
    this.cleanupTable=cleanupTable;
   // this.cortinaTable=cortinaTable;
    this.player=player;
    cortinaTab=this;
    
    cortinaTable = new CortinaTable();
    cleanupTable = new CleanupTable();
    
    gridPane.setPadding(new Insets(10, 10, 10, 10));
    gridPane.setHgap(30);
   
    gridPane.setStyle("-fx-background-color: DAE6F3; -fx-border-color: BLACK; -fx-border-style: SOLID; -fx-border-width: 3px;"); // border doesn't work
    gridPane.add(cleanupTable, 0, 1);
    
    Text label_1 = new Text("Source Tracks");
    Text label_2 = new Text("Cortinas");
    label_1.setFont(new Font("Arial", 20));
    label_2.setFont(new Font("Arial", 20));
    
    gridPane.add(label_1, 0, 0);
    gridPane.add(label_2, 1, 0);
    
    gridPane.add(cortinaTable, 1, 1);
    //gridPane.add(allTracksTable.getTable(), 1, 0);
    
    
   // hbox.getChildren().add(playlist.getTreeView());
   // hbox.setHgrow(playlist.getTreeView(), Priority.ALWAYS);
   // setupListeners();
    this.setContent(gridPane);
    setupListener();
 
  }
  
  
  private void setupListener()
  {
 // MOUSE CLEANUP TABLE ROW SELECTION
    cleanupTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() 
    {
      public void changed(ObservableValue observable, Object oldValue, Object newValue) 
      {
        CleanupTrack selectedTrack = (CleanupTrack)newValue;
        if (selectedTrack!=null)
        {
          player.setPlayMode(Player.PLAYMODE_CLEANUP_TO_CORTINA_TRACK);
          player.setCurrentTrackHash(selectedTrack.getPathHash());
          player.setCurrentTrackTitle(selectedTrack.getTitle());
          player.setCortinaNewControls(selectedTrack);
          player.setCortinaControlsActive(true);
        }
      }
    });
    
 // MOUSE CORTINA TABLE ROW SELECTION
    cortinaTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() 
    {
      public void changed(ObservableValue observable, Object oldValue, Object newValue) 
      {
        CortinaTrack cortinaTrack = (CortinaTrack)newValue;
        // TODO track Index?
        if (cortinaTrack!=null)
        {
          if (cortinaTrack.getPremade()==1)
          {
            player.setPlayMode(Player.PLAYMODE_PREMADE_CORTINA_SINGLE_TRACK);
            player.setPremadeCortinaEditControls(cortinaTrack);
            player.setPremadeCortinaControlsActive(true);
            //System.out.println("CortinaTab - cortinaTrackPath: "+cortinaTrack.getPath());
          }
          else
          {
            player.setPlayMode(Player.PLAYMODE_CORTINA_SINGLE_TRACK);
            player.setCortinaEditControls(cortinaTrack);
            player.setCortinaControlsActive(true);
          }
        }
      }
    });
    
    // PLAYER PLAYING LISTENER
    ChangeListener playingListener = new ChangeListener() 
    {
      public void changed(ObservableValue observable, Object oldValue, Object newValue) 
      {
    	  if (player.getActiveTab()!=Player.CORTINA_CREATE_TAB) cortinaTab.setDisable(Player.playing.get());
      }
    };   
   Player.playing.addListener(playingListener);
  
  
    
  }
  
}
  
  /*  
  private void setupListeners() 
  {
    ChangeListener tangoHashListener = new ChangeListener() 
    {
      public void changed(ObservableValue observable, Object oldValue, Object newValue) 
      {
      //  System.out.println("selected Tango Hash: "+sharedValues.selectedTangoPathHash.get());
        player.updateUIValues(SharedValues.selectedCleanupPathHash.get());
      }
    };   
    SharedValues.selectedCleanupPathHash.addListener(tangoHashListener);
  }
  */


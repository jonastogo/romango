package tangodj2;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
/*
 * TODO export playlist?
 * MP3 Tagtools
 * CDEX
 * Get rating from iTunes XML file?
 * Create a tangoGenre MP3tag and populate when making tandas?
 * Remember file locations for adding files
 * MP3 tag editor
 * Event Tab: fade/next track
 * table search or restriction or fulltext
 * try mediaPlayer.setOnReady instead of Timeline for loading MP3 tag info
 * Add treeitems below song title to show artist, time, album etc
 * Show total time for each tanda. Maybe even real end time from system clock
 * Add pre-made cortinas directly to cortinas table
 * When making cotinas, there should be a length counter
 *   from the time the set start position button is pressed
 */
public class TangoDJ2 extends Application 
{
  static Stage primaryStage;
  static PlaylistBuilderTab playlistBuilderTab;
  static PlaylistChoiceTab playlistChoiceTab;
  static CortinaTab cortinaTab;
  static EventTab eventTab;
  private Preferences prefs = new Preferences();
  
  MenuBar menuBar;
  Playlist playlist;
   
  Rectangle r = new Rectangle(10,10,10,10);
  Player player;
 
	
  public static void main(String[] args) 
  {
    launch(args);
  }
	
  public void start(Stage stage) 
  {
	primaryStage=stage;
	loadFonts();
	loadPreferences();
	VBox root = new VBox();
    Scene scene = new Scene(root, 950, 550, Color.WHITE);
    r.setFill(Color.RED);
   
    final URL stylesheet = getClass().getResource("style.css");
    scene.getStylesheets().add(stylesheet.toString());
    
    TabPane tabPane = new TabPane();
    BorderPane mainPane = new BorderPane();
          
    CreateDatabase cb = new CreateDatabase();
    
    try {if (!cb.exists()) cb.create(); } catch (Exception e) { e.printStackTrace(); }
    
    setupMenuBar();
      
    try { playlist = new Playlist();} 
    catch (SQLException se) { System.out.println("PROGRAM ALREADY RUNNING"); System.exit(0); } 
    catch (ClassNotFoundException e) { e.printStackTrace(); }
        
    Tab equalizerTab = new Tab();
  //  equalizerTab.setStyle("-fx-background-color: #bfc2c7;");
    equalizerTab.setText("Equalizer");
    
    player = new Player(equalizerTab);
    player.setPlaylist(playlist);
   
    playlistBuilderTab = new PlaylistBuilderTab(playlist,  player);
    tabPane.getTabs().add(playlistBuilderTab);
    
    playlistChoiceTab = new PlaylistChoiceTab();
    tabPane.getTabs().add(playlistChoiceTab);
    
    cortinaTab = new CortinaTab(player);
      
    // TAB SELECTION LISTENER
    tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>()
    {
      public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab mostRecentlySelectedTab)
      {
        if (mostRecentlySelectedTab.equals(cortinaTab))
        {
          player.setFeaturesMode(Player.CORTINA_CREATE);
        }
        if (mostRecentlySelectedTab.equals(playlistBuilderTab))
        {
          player.setFeaturesMode(Player.PLAYLIST_CREATE);
          player.setPlaylist(playlist);
        }
        if (mostRecentlySelectedTab.equals(eventTab))
        {
          player.setFeaturesMode(Player.PLAYLIST_CREATE);
          player.setPlaylist(eventTab.playlist);
        }
      }
    });
      
    tabPane.getTabs().add(equalizerTab);
    tabPane.getTabs().add(cortinaTab);
    
    eventTab = new EventTab(player);
    tabPane.getTabs().add(eventTab);
    
    mainPane.setCenter(tabPane);
      
   
    mainPane.setBottom(player.get());
     
    mainPane.prefHeightProperty().bind(scene.heightProperty());
    mainPane.prefWidthProperty().bind(scene.widthProperty());
    
    root.getChildren().addAll(menuBar, mainPane);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void setupMenuBar()
  {
    menuBar = new MenuBar();
    Menu menuFile = new Menu("File");
   
    MenuItem menuAddTangoDir = new MenuItem("Add Tango Folder");
    MenuItem menuAddTangoFile = new MenuItem("Add Tango Track");
    MenuItem menuAddCleanupDir = new MenuItem("Add Cortina\\Cleanup Folder");
    MenuItem menuAddCleanupFile = new MenuItem("Add Cortina\\Cleanup Track");
    MenuItem preferences = new MenuItem("Preferences");
    MenuItem about = new MenuItem("About");
    MenuItem manual = new MenuItem("Manual");
    
    Menu menuEdit = new Menu("Edit");
    Menu menuView = new Menu("View");
    Menu menuHelp = new Menu("Help");
    menuBar.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);
    
    menuAddTangoDir.setOnAction(new EventHandler<ActionEvent>() 
    {
      public void handle(ActionEvent t) 
      {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("C:\\music\\tango"));  // temporary 
        File selectedDirectory = 
        directoryChooser.showDialog(primaryStage);
                  
        if(selectedDirectory == null) { System.out.println("No Directory selected"); } 
        else
        {
          playlistBuilderTab.loadTangoDirectory(selectedDirectory.toPath().toString());
        }
      }
    });   
    menuAddCleanupDir.setOnAction(new EventHandler<ActionEvent>() 
    	    {
    	      public void handle(ActionEvent t) 
    	      {
    	        DirectoryChooser directoryChooser = new DirectoryChooser();
    	        directoryChooser.setInitialDirectory(new File("C:\\music\\tango"));  // temporary 
    	        File selectedDirectory = 
    	        directoryChooser.showDialog(primaryStage);
    	                  
    	        if(selectedDirectory == null) { System.out.println("No Directory selected"); } 
    	        else
    	        {
    	          playlistBuilderTab.loadCleanupDirectory(selectedDirectory.toPath().toString());
    	        }
    	      }
    	    });   
    	    
    menuAddTangoFile.setOnAction(new EventHandler<ActionEvent>() 
    {
      public void handle(ActionEvent t) 
      {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\music\\tango"));  // temporary 
        File selectedFile = 
        fileChooser.showOpenDialog(primaryStage);
                  
        if(selectedFile == null) { System.out.println("No File selected"); } 
        else
        {
          playlistBuilderTab.loadTangoFile(selectedFile.toPath().toString());
        }
      }
    });   
    
    menuAddCleanupFile.setOnAction(new EventHandler<ActionEvent>() 
    {
      public void handle(ActionEvent t) 
      {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\music\\tango"));  // temporary 
        File selectedFile = 
        fileChooser.showOpenDialog(primaryStage);
                  
        if(selectedFile == null) { System.out.println("No File selected"); } 
        else
        {
          playlistBuilderTab.loadCleanupFile(selectedFile.toPath().toString());
        }
      }
    });   
    	    
    preferences.setOnAction(new EventHandler<ActionEvent>() 
    {
      public void handle(ActionEvent t) 
      {
        new PreferencesDialog();	
      }});
    
    menuFile.getItems().addAll(menuAddTangoDir, menuAddTangoFile,menuAddCleanupDir, menuAddCleanupFile);
    menuEdit.getItems().add(preferences);
    menuHelp.getItems().addAll(about, manual);
  }
  
  private void loadFonts()
  {
	
	Font.loadFont(TangoDJ2.class.getResource("/resources/fonts/Carousel.ttf").toExternalForm(), 10  );
	Font.loadFont(TangoDJ2.class.getResource("/resources/fonts/Anagram.ttf").toExternalForm(), 10  );
	Font.loadFont(TangoDJ2.class.getResource("/resources/fonts/Carrington.ttf").toExternalForm(), 10  );
	Font.loadFont(TangoDJ2.class.getResource("/resources/fonts/DEFTONE.ttf").toExternalForm(), 10  );
	Font.loadFont(TangoDJ2.class.getResource("/resources/fonts/EastMarket.ttf").toExternalForm(), 10  );
	Font.loadFont(TangoDJ2.class.getResource("/resources/fonts/england.ttf").toExternalForm(), 10  );
	Font.loadFont(TangoDJ2.class.getResource("/resources/fonts/FFF_Tusj.ttf").toExternalForm(), 10  );
  }

  private void loadPreferences()
  {
	  prefs=Db.getPreferences();
  }
 
}

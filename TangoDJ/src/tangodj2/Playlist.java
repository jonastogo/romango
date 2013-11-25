package tangodj2;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import tangodj2.PlaylistTree.BaseTreeItem;
import tangodj2.PlaylistTree.CortinaTreeItem;
import tangodj2.PlaylistTree.PlaylistTreeItem;
import tangodj2.PlaylistTree.TandaTreeItem;
import tangodj2.PlaylistTree.TrackTreeItem;
import tangodj2.cortina.CortinaTrack;

public class Playlist 
{
  private PlaylistTreeItem playlistTreeItem;
  private TreeView<String> treeView;
  private PlaylistTrack previouslyPlayingTrack=null;
  private PlaylistTrack previouslySelectedTrack=null;
  private int nextTrack=0; 
  private int playingTrack=0;
  private int selectedPlaylistTrack=0;
  private ArrayList<PlaylistTrack> flatPlaylistTracks =  new ArrayList<PlaylistTrack>();
  private int selectedTanda=-1;
  private int numberOfTandas=-1;
  public static SimpleIntegerProperty playlistFocus = new SimpleIntegerProperty(0);
  
  public Playlist(int playlistId) 
  {
	  setupTreeView(playlistId);	
	  setNextTrackToPlay();  
  }
	
  public void printTracks()
  {
	int i=0;
	while( true)
	{
	  BaseTreeItem ti = (BaseTreeItem)treeView.getTreeItem(i);
	  if (ti==null) break;
	  System.out.println(i+") "+ti.getTreeType());
	  i++;
	}
  }
	
  public void stopPlaying()
  {
    if (flatPlaylistTracks.size()==0) return;
	  flatPlaylistTracks.get(playingTrack).baseTreeItem.setNextPlayImage(false);
	  flatPlaylistTracks.get(playingTrack).baseTreeItem.setNextPlayImage(true);
  }
	
  public PlaylistTrack getTrack(int trackNo)
  {
	  if (trackNo>=flatPlaylistTracks.size()) return null;
	  return flatPlaylistTracks.get(trackNo);
  }
	
  public void setPrevious()
  {
	  nextTrack-=2;
	  if (nextTrack<0) nextTrack=0;
  }
	
	
  public void setNextTrackToPlay()
  {
	if (selectedPlaylistTrack==-1) return;
	if (flatPlaylistTracks==null) return;
	if (flatPlaylistTracks.size()==0) return;
	PlaylistTrack playlistTrack = flatPlaylistTracks.get(selectedPlaylistTrack);
	
	if (playlistTrack.baseTreeItem.getStatus()!=TrackTreeItem.PLAYING)
    {  
      playlistTrack.baseTreeItem.setNextPlayImage(true);
      nextTrack=selectedPlaylistTrack;
      if (previouslySelectedTrack!=null) 
      {
        if (!previouslySelectedTrack.playing)
        previouslySelectedTrack.baseTreeItem.setNextPlayImage(false);
      }
      previouslySelectedTrack=playlistTrack;
    }
  }
	
  public void generateFlatList()
  {
    int i=0;
    flatPlaylistTracks.clear();
    int tandaCounter=-1;
    int tandaTrackCounter=0;
    int numberOfTracksInTanda=0;
    String tandaName=null;
    PlaylistTrack playlistTrack;
    numberOfTandas=0;
    int playableIndex=0;
    double totalPlaylistTime=0;
    
    while( true)
    {
      BaseTreeItem ti = (BaseTreeItem)treeView.getTreeItem(i);
      if (ti==null) break;
      if ("playlist".equals(ti.getTreeType()))
      {
        PlaylistTreeItem playlistTreeItem = (PlaylistTreeItem)ti;
        playlistTreeItem.setPlayableIndex(0);
        numberOfTandas=playlistTreeItem.getChildren().size();
      }
      else if ("tanda".equals(ti.getTreeType()))
      {
        tandaTrackCounter=0;
        tandaCounter++;
        TandaTreeItem tandaTreeItem = (TandaTreeItem)ti;
        tandaName = tandaTreeItem.getArtistAndStyle();
        numberOfTracksInTanda=tandaTreeItem.getChildren().size()-1; // minus 1 for cortina
        tandaTreeItem.setPlayableIndex(playableIndex);
      }
      else if ("tango".equals(ti.getTreeType())||"cleanup".equals(ti.getTreeType()))
      {
        TrackTreeItem trackTreeItem = (TrackTreeItem)ti;
        trackTreeItem.setPlayableIndex(playableIndex);
        playableIndex++;
        playlistTrack = new PlaylistTrack();
        playlistTrack.title=trackTreeItem.getValue();
        playlistTrack.tandaName=tandaName;
        if ((tandaCounter+1)<numberOfTandas)
        {
          playlistTrack.nextTandaName=((TandaTreeItem)playlistTreeItem.getChildren().get(tandaCounter+1)).getArtistAndStyle();
        }
        else playlistTrack.nextTandaName="Good Night";
        playlistTrack.album=trackTreeItem.getAlbum();
        playlistTrack.artist=trackTreeItem.getArtist();
        playlistTrack.path=trackTreeItem.getPath();
        playlistTrack.tandaNumber=tandaCounter;
        tandaTrackCounter++;
        playlistTrack.trackInTanda=tandaTrackCounter;
        playlistTrack.numberOfTracksInTanda=numberOfTracksInTanda;
        playlistTrack.cortina=false;
        playlistTrack.baseTreeItem=trackTreeItem;
        playlistTrack.trackHash=trackTreeItem.getTrackHash();
        flatPlaylistTracks.add(playlistTrack);
        
        playlistTrack.duration=trackTreeItem.getDuration();
        totalPlaylistTime+=playlistTrack.duration;
        
      }
      else if ("cortina".equals(ti.getTreeType()))
      {
        CortinaTreeItem cortinaTreeItem = (CortinaTreeItem)ti;
        cortinaTreeItem.setPlayableIndex(playableIndex);
        playableIndex++;
        playlistTrack = new PlaylistTrack();
        playlistTrack.title=cortinaTreeItem.getValue();
        if ((tandaCounter+1)<numberOfTandas)
        {
          playlistTrack.nextTandaName=((TandaTreeItem)playlistTreeItem.getChildren().get(tandaCounter+1)).getArtistAndStyle();
        }
        else playlistTrack.nextTandaName="Good Night";
        playlistTrack.album = cortinaTreeItem.getAlbum();
        playlistTrack.artist = cortinaTreeItem.getArtist();
        playlistTrack.tandaName=tandaName;
        playlistTrack.path=cortinaTreeItem.getPath();
        playlistTrack.tandaNumber=tandaCounter;
        playlistTrack.cortina=true;
        playlistTrack.baseTreeItem=cortinaTreeItem;
        playlistTrack.trackHash=cortinaTreeItem.getPathHash();
        
        playlistTrack.premade=cortinaTreeItem.getPremade();
        playlistTrack.startValue =cortinaTreeItem.getStart();
        playlistTrack.stopValue  =cortinaTreeItem.getStop();
        playlistTrack.fadein     =cortinaTreeItem.getFadein();
        playlistTrack.fadeout    =cortinaTreeItem.getFadeout();
        playlistTrack.delay      =cortinaTreeItem.getDelay();
        playlistTrack.original_duration  =cortinaTreeItem.getOriginal_duration();
        playlistTrack.duration=cortinaTreeItem.getDuration();
        totalPlaylistTime+=playlistTrack.duration;
        flatPlaylistTracks.add(playlistTrack);
        // didn't set or increment tandaTrackCounter or set tandaCounter
      }
      i++;
    }
    
    BaseTreeItem  ti = (BaseTreeItem)treeView.getTreeItem(i-1);
    if ("tanda".equals(ti.getTreeType()))
    {
      // if the last item in the tree is a tanda, there is no next track
      ti.setPlayableIndex(999);
    }
    
     System.out.println("Playlist total duration: "+formatIntoMMSS(totalPlaylistTime));
     //printFlatList();
  }
	
  public void printFlatList()
  {
	int i=0;
	System.out.println("print flat playlist");
	System.out.println("playingTrack: "+playingTrack);
	System.out.println("nextTrack: "+nextTrack);
	PlaylistTrack playlistTrack;
    Iterator<PlaylistTrack> it = flatPlaylistTracks.iterator();
    while(it.hasNext())
    {
      playlistTrack = it.next();
      System.out.println(i+") "
      +playlistTrack.tandaName+" "
      +playlistTrack.trackInTanda
      +" of "+playlistTrack.numberOfTracksInTanda+", "
      +formatIntoMMSS(playlistTrack.duration)+", "
      +playlistTrack.title+", "
      +playlistTrack.album+", "
      +playlistTrack.path);
      i++;
    }
  }
	
  static String formatIntoMMSS(double millisIn)
  {
    millisIn=millisIn/1000;
    int hours = (int)millisIn / 3600,
    remainder = (int)millisIn % 3600,
    minutes = remainder / 60,
    seconds = remainder % 60;
    DecimalFormat sec = new DecimalFormat( "00" );
    DecimalFormat min = new DecimalFormat( "##" );
    DecimalFormat hr = new DecimalFormat( "##" );
  //return ( (minutes < 10 ? "0" : "") + minutes
  //+ ":" + (seconds< 10 ? "0" : "") + seconds );
  
  return hr.format(hours)+":"+min.format(minutes)+":"+sec.format(seconds);

  }
  
  public TreeView<String> getTreeView() { return treeView; }
	
  public void addTanda(String artist, int styleId)
  {
	playlistTreeItem.addTanda(artist, styleId);
	generateFlatList();
  }
	
  public int getTandaCount() { return playlistTreeItem.getTandaCount();	}
	
  public TandaTreeItem getSelectedTanda()
  {
	if (selectedTanda==-1) selectedTanda=0;
	return getTanda(selectedTanda);
  }
	
  public TandaTreeItem  getTanda(int index)
  {
  TandaTreeItem tandaTreeItem = (TandaTreeItem)playlistTreeItem.getChildren().get(index);
	  System.out.println("Playlist tanda: "+tandaTreeItem.getArtist());
	  return tandaTreeItem;
	}
  
  
	
  private void setupTreeView(int playlistId) 
	{
	  playlistTreeItem =  Db.getPlaylist(playlistId);
	  treeView = new TreeView<String>(playlistTreeItem);
	  treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	 
	  treeView.getStyleClass().add("playlistTree");
	  treeView.getSelectionModel().select(playlistTreeItem);
		treeView.setShowRoot(true);
		ArrayList<TandaTreeItem> tandaTreeItems=null;
		try 
		{
		  tandaTreeItems = Db.getTandaTreeItems(playlistId);
		} catch (ClassNotFoundException | SQLException e) { e.printStackTrace();}
			
		 Iterator<TandaTreeItem> it = tandaTreeItems.iterator();
			
		 if (tandaTreeItems.size()>0)
		 {
		   playlistTreeItem.getChildren().addAll(tandaTreeItems);
		 }
			 
		 treeView.setCellFactory(new Callback<TreeView<String>,TreeCell<String>>()
		 {
		   @Override
		   public TreeCell<String> call(TreeView<String> p) 
		   {
			 return new MyCellFactory();
		   }
		  });
			 
		 /* 
		  * Detect tree item selected
		  */
		 ChangeListener<TreeItem<String>> treeViewChangeListener = new ChangeListener<TreeItem<String>>() 
		 {
		   public void changed(ObservableValue<? extends TreeItem<String>> observableValue, TreeItem<String> oldItem, TreeItem<String> newItem) 
		   {
			   if (newItem!=null)
			   {
			     BaseTreeItem bti = (BaseTreeItem)newItem;
			   
			     selectedPlaylistTrack=-1;
			     selectedPlaylistTrack=bti.getPlayableIndex();
			     System.out.println("Playlist - selectedIndex: "+selectedPlaylistTrack);
			     selectedTanda=getTandaNumber(selectedPlaylistTrack);
         
			     playlistFocus.set(playlistFocus.get()+1);
			     System.out.println("Tanda/Track: "+selectedTanda+"/"+selectedPlaylistTrack+" - "+ newItem.getValue()); 
			 }
		 }};
							 
		 treeView.getSelectionModel().selectedItemProperty().addListener(treeViewChangeListener);
		 generateFlatList();
	 }
  
   private int getTandaNumber(int selectedPlaylistTrack)
   {
     if (selectedPlaylistTrack==999) return numberOfTandas-1;
     if (selectedPlaylistTrack==-1) return 0;
     PlaylistTrack playlistTrack = flatPlaylistTracks.get(selectedPlaylistTrack);
     return playlistTrack.tandaNumber;
   }
     
     
			
	   private final class MyCellFactory extends TreeCell<String> 
	   {
	     private ContextMenu tandaContextMenu =  new ContextMenu();
	     private ContextMenu trackContextMenu =  new ContextMenu();
	     private ContextMenu playlistContextMenu =  new ContextMenu();
	     private ContextMenu cortinaContextMenu =  new ContextMenu();

	     public MyCellFactory() 
	     {
	       setupTandaContextMenu(tandaContextMenu);
	       setupTrackContextMenu(trackContextMenu);
           setupPlaylistContextMenu(playlistContextMenu);
	       setupCortinaContextMenu(cortinaContextMenu);
	     }
	       
	     @Override
	     public void updateItem(String item, boolean empty) 
	     {
	       super.updateItem(item, empty);

	       if (empty) 
	       {
	         setText(null);
	         setGraphic(null);
	       } 
	       else 
	       {
	         setText(getString());
	         BaseTreeItem bti = (BaseTreeItem)getTreeItem();
	         if (isSelected()) 
	         {
	           TreeItem treeItem = (TreeItem)bti;
	           System.out.println("selected tree cell: "+treeItem.getValue());
	         }
	         if ("playlist".equals(bti.getTreeType())) 
	         {  
	           this.getStyleClass().add("playlistBackground");
	           setFont(Font.font("Serif", 20));
	           setContextMenu(playlistContextMenu);
	         }
	         else if ("tanda".equals(bti.getTreeType())) 
	         {	   
	           this.getStyleClass().add("tandaTitleText");
	           this.getStyleClass().add("playlistBackground");
	           if (playlistTreeItem.getTandaPosition((TandaTreeItem)bti)==0) tandaContextMenu.getItems().get(0).setDisable(true);  // disable move up
	           if (playlistTreeItem.getTandaPosition((TandaTreeItem)bti)==playlistTreeItem.getTandaCount()-1) tandaContextMenu.getItems().get(1).setDisable(true); // disable move down
	          // Font tandaTitleFont=Font.font("Serif", 18);
	           
	           //setFont(Font.font("Serif", 18));
	           //Kludge alert
	           tandaContextMenu.setId(""+playlistTreeItem.getTandaPosition((TandaTreeItem)bti));
	           setContextMenu(tandaContextMenu);
	          // setGraphic(getTreeItem().getGraphic());
	         }
	         else if ("tango".equals(bti.getTreeType()))
	         {
	           //setFont(Font.font("Serif", 16));
	           this.getStyleClass().add("playlistBackground");
	           this.getStyleClass().add("tangoPlaylistText");
	           TrackTreeItem trackTreeItem = (TrackTreeItem)bti;
	           this.setTooltip(new Tooltip("Album: "+trackTreeItem.getAlbum()+"\n"
	               +"Orchestra: "+trackTreeItem.getArtist()));
	         
	           int trackCount=((TandaTreeItem)trackTreeItem.getParent()).getTrackCount();
	           int trackPosition=trackTreeItem.getTrackPosition(trackTreeItem);
	           
	           if (trackPosition==0) trackContextMenu.getItems().get(0).setDisable(true);  // disable move up
	           if (trackPosition==trackCount-1) trackContextMenu.getItems().get(1).setDisable(true); // disable move down
	           //Kludge alert
	           trackContextMenu.setId(trackTreeItem.getTandaAndTrackPosition(trackTreeItem));
	           setContextMenu(trackContextMenu);
	           //System.out.println("track update: "+trackTreeItem.getValue());
	           //trackTreeItem.setSelected(true);
	          // if (trackTreeItem.isSelected()) setGraphic(new ImageView(TrackTreeItem.greenCheckBallImage));
	          // else setGraphic(new ImageView(TrackTreeItem.dimBallImage));
	         }
	         else if ("cortina".equals(bti.getTreeType()))
           {
	           this.getStyleClass().add("cortinaPlaylistText");
            // setStyle("-fx-border-color:rgba(219, 42, 199,.41);");
             CortinaTreeItem cortinaTreeItem = (CortinaTreeItem)bti;
             this.getStyleClass().add("playlistBackground");
            // int trackCount=((TandaTreeItem)cortinaTreeItem.getParent()).getTrackCount();
            // System.out.println("Playlist - cortina track found: "+trackCount+" - "+cortinaTreeItem.getValue());
             //int trackPosition=trackTreeItem.getTrackPosition(trackTreeItem);
             
            // if (trackPosition==0) trackContextMenu.getItems().get(0).setDisable(true);  // disable move up
            // if (trackPosition==trackCount-1) trackContextMenu.getItems().get(1).setDisable(true); // disable move down
             //Kludge alert
           //  trackContextMenu.setId(trackTreeItem.getTandaAndTrackPosition(trackTreeItem));
           //  setContextMenu(trackContextMenu);
             //System.out.println("track update: "+trackTreeItem.getValue());
             //trackTreeItem.setSelected(true);
            // if (trackTreeItem.isSelected()) setGraphic(new ImageView(TrackTreeItem.greenCheckBallImage));
            // else setGraphic(new ImageView(TrackTreeItem.dimBallImage));
             setContextMenu(cortinaContextMenu);
           }
	         else if ("cleanup".equals(bti.getTreeType()))
           {
             this.getStyleClass().add("cleanupPlaylistText");
             this.getStyleClass().add("playlistBackground");
             TrackTreeItem cleanupTreeItem = (TrackTreeItem)bti;
             setContextMenu(trackContextMenu);
           }
	         setGraphic(getTreeItem().getGraphic());
	       }
	     }
	      
	     private String getString() 
	     {
	       return getItem() == null ? "" : getItem().toString();
	     }
	   }
	   
	   private void setupTandaContextMenu(final ContextMenu tandaContextMenu)
	   {
		   MenuItem moveUp = new MenuItem("Move Tanda Up"); 
	     MenuItem moveDown = new MenuItem("Move Tanda Down");
	     MenuItem delete = new MenuItem("Delete Tanda" );
	     tandaContextMenu.setOnShowing(new EventHandler() 
	     {
	       public void handle(Event e) 
	       {
	         //selectedTanda= Integer.parseInt(tandaContextMenu.getId());
	       }
	     });
	     tandaContextMenu.getItems().addAll(moveUp, moveDown, delete);
	     moveUp.setOnAction(new EventHandler() 
	     {
	       public void handle(Event t) 
	       {
	         playlistTreeItem.moveTandaUp(selectedTanda);   
	         generateFlatList();
	         
	       }
	     });
	     moveDown.setOnAction(new EventHandler() 
	     {
	       public void handle(Event t) 
	       {
	         playlistTreeItem.moveTandaDown(selectedTanda);   
	         generateFlatList();
	       }
	     });
	     delete.setOnAction(new EventHandler() 
	     {
	       public void handle(Event t) 
	       {
	         playlistTreeItem.deleteTanda(selectedTanda); 
	         generateFlatList();
	       }
	     });
	   } 
	   
	   private void setupPlaylistContextMenu(final ContextMenu playlistContextMenu)
     {
       MenuItem newTanda = new MenuItem("New Tanda"); 
     //  playlistContextMenu.setOnShowing(new EventHandler() 
      // {
     //    public void handle(Event e) 
      //   {
      //     SharedValues.selectedTanda= Integer.parseInt(tandaContextMenu.getId());
      //   }
     //  });
       playlistContextMenu.getItems().add(newTanda);
       newTanda.setOnAction(new EventHandler() 
       {
         public void handle(Event t) 
         {
           newTandaDialog();   
         }
       });
      
     } 
     
	   private void setupCortinaContextMenu(final ContextMenu cortinaContextMenu)
	     {
	       MenuItem removeCortina = new MenuItem("Remove Cortina");
	       MenuItem playNext = new MenuItem("Play Next");
	     
	       cortinaContextMenu.getItems().addAll(playNext, removeCortina);
	       removeCortina.setOnAction(new EventHandler() 
	       {
	         public void handle(Event t) 
	         {
	        	 playlistTreeItem.getTanda(selectedTanda).removeCortina();
	  	       generateFlatList();
	         }
	       });
	       playNext.setOnAction(new EventHandler() 
	       {
	         public void handle(Event t) 
	         {
	           setNextTrackToPlay();
	         }
	     });
	      
	     } 
	       
	   private void setupTrackContextMenu(final ContextMenu trackContextMenu)
	   {
		   MenuItem moveUp = new MenuItem("Move Track Up"); 
	     MenuItem moveDown = new MenuItem("Move Track Down");
	     MenuItem delete = new MenuItem("Remove Track");
	     MenuItem playNext = new MenuItem("Play Next");
	     
	     
	     trackContextMenu.setOnShowing(new EventHandler() 
	     {
	       public void handle(Event e) 
	       {
	         /*
	    	 String trackId = trackContextMenu.getId();  
	    	 String[] tokens = trackId.split(",");
	    	 
	    	 for (int i = 0; i < tokens.length; i++)
	    	 {
	    	   if (i==0)
	    	   {
	    	     selectedTanda=Integer.parseInt(tokens[i]);
	    	   }
	    	   if (i==1) 
	    	   {
	    	     selectedPlaylistTrack=Integer.parseInt(tokens[i]);
	    	   }
	       }
	       */
       }
	   });
	   trackContextMenu.getItems().addAll(moveUp, moveDown, delete, playNext);
	   
	   playNext.setOnAction(new EventHandler() 
       {
         public void handle(Event t) 
         {
           setNextTrackToPlay();
         }
     });
	   
	   
	   moveUp.setOnAction(new EventHandler() 
	   {
	     public void handle(Event t) 
	     {
	       playlistTreeItem.getTanda(selectedTanda).moveTrackUp(selectedPlaylistTrack);
	       generateFlatList();
	     }
	   });
	   
	   moveDown.setOnAction(new EventHandler() 
	   {
	     public void handle(Event t) 
	     {
	       playlistTreeItem.getTanda(selectedTanda).moveTrackDown(selectedPlaylistTrack);
	       generateFlatList();
	     }
	   });
	   
	   delete.setOnAction(new EventHandler() 
	   {
	     public void handle(Event t) 
	     {
	       playlistTreeItem.getTanda(selectedTanda).deleteTrack(selectedPlaylistTrack);
	       generateFlatList();
	     }
	   });
	 } 

	   private void newTandaDialog() 
	   {
	     final ComboBox comboBox = new ComboBox();
	     final TextBuilder seperatorBuilder = TextBuilder.create()
	            .fill(Color.BLACK)
	            .font(Font.font("Serif", 18));
	     
	     final Text alist = seperatorBuilder.text("A List").build();
	     Text blist =  seperatorBuilder.text("B List").build();
	     Text clist =  seperatorBuilder.text("C List").build();
	    
	     comboBox.getItems().add(alist);
	     comboBox.getItems().addAll(SharedValues.artistsA);
	     comboBox.getItems().add(blist);
	     comboBox.getItems().addAll(SharedValues.artistsB);
	     comboBox.getItems().add(clist);
	     comboBox.getItems().addAll(SharedValues.artistsC);
	    
	     final RadioButton rb1 = new RadioButton("Tango");
	     final RadioButton rb2 = new RadioButton("Vals");
	     final RadioButton rb3 = new RadioButton("Milonga");
	     final RadioButton rb4 = new RadioButton("Alternative");
	     final RadioButton rb5 = new RadioButton("Mixed");
	     final RadioButton rb6 = new RadioButton("Cleanup");
	     
	     rb1.setId(""+SharedValues.TANGO);
	     rb2.setId(""+SharedValues.VALS);
	     rb3.setId(""+SharedValues.MILONGA);
	     rb4.setId(""+SharedValues.ALTERNATIVE);
	     rb5.setId(""+SharedValues.MIXED);
	     rb6.setId(""+SharedValues.CLEANUP);
	     
	     final ToggleGroup styleGroup = new ToggleGroup();
	     
	     rb1.setToggleGroup(styleGroup);
	     rb2.setToggleGroup(styleGroup);
	     rb3.setToggleGroup(styleGroup);
	     rb4.setToggleGroup(styleGroup);
	     rb5.setToggleGroup(styleGroup);
	     rb6.setToggleGroup(styleGroup);
	        
	     rb1.setSelected(true);

	     final VBox styleBox = new VBox();
	     styleBox.getChildren().addAll(rb1,rb2,rb3,rb4,rb5,rb6);
	     final TextField tandaName = new TextField();
	     
         final Stage myDialog = new Stage();
         myDialog.initModality(Modality.APPLICATION_MODAL);
         Button okButton = new Button("SAVE");
         okButton.setOnAction(new EventHandler<ActionEvent>()
         {
           public void handle(ActionEvent arg0) 
           {
	           String artist = comboBox.getSelectionModel().getSelectedItem().toString();
	           int styleId = 0;
	           String selectedStr=styleGroup.getSelectedToggle().toString();
	           int i = selectedStr.indexOf("id=");
	           String numStr = selectedStr.substring(i+3, i+4);
	           try 
	           {
	             styleId= Integer.parseInt(numStr);
	           } catch (Exception e) {}
	           addTanda(artist, styleId);
	             myDialog.close();
           }});
       
         Text tandaLabel = new Text("Orchestra: ");
         tandaLabel.setFont(Font.font("Serif", 20));
         
         
         GridPane gridPane = new GridPane();
         gridPane.setPadding(new Insets(10, 10, 10, 10));
         gridPane.setVgap(2);
         gridPane.setHgap(5);
         gridPane.add(tandaLabel, 0, 0);
         gridPane.add(comboBox, 1, 0);
         //gridPane.add(new Text("Style"), 0, 1);
         gridPane.add(styleBox, 0, 1);
         gridPane.add(okButton, 1, 2);
         
         Scene myDialogScene = new Scene(gridPane, 300, 200);
         myDialog.setScene(myDialogScene);
         myDialog.show();
	   }

    public int getNextTrack()
    {
      return nextTrack;
    }

    public void setNextTrack(int nextTrack)
    {
      this.nextTrack = nextTrack;
    }

    public int getPlayingTrack()
    {
      return playingTrack;
    }

    public void setPlayingTrack(int playingTrack)
    {
      this.playingTrack = playingTrack;
    }

   public String getPlayingArtist()
   {
	 return flatPlaylistTracks.get(playingTrack).artist; 
   }
   
   
   public String getPlayingTitle()
   {
	 return flatPlaylistTracks.get(playingTrack).title; 
   }
   
   public String getPlayingTandaProgress()
   {
     PlaylistTrack playlistTrack = flatPlaylistTracks.get(playingTrack);
     String progress=playlistTrack.trackInTanda+" of "+playlistTrack.numberOfTracksInTanda;
     System.out.println("Playlist - progress: "+progress);
     return  progress;
   }
   
   
   
   public String getNextTandaInfo()
   {
	   return flatPlaylistTracks.get(playingTrack).nextTandaName; 
   }
	 
   public boolean isCortina()
   {
	 return  flatPlaylistTracks.get(playingTrack).cortina; 
   }
}

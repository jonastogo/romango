package tangodj2;

import tangodj2.PlaylistTree.TandaTreeItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AllTracksTable 
{
  private TableView<Track> table = new TableView<Track>();
  private Playlist playlist;
	 
  public AllTracksTable(Playlist playlist)
  {
	  this.playlist=playlist;
	  setupAllTracksTable();
	  Db.loadAllTracks();
	  Track firstTrack = SharedValues.allTracksData.get(0);
	  SharedValues.selectedAllTracksPathHash.set(firstTrack.getPathHash());
  }
	 
  public static void reloadData()
  {
    SharedValues.allTracksData.clear();
    Db.loadAllTracks();
  }
	 
  public TableView<Track> getTable() { return table; }
	
  private void setupAllTracksTable() 
  {
    double tableWidth=setupAllTracksColumns()+43;
		    
    table.setItems(SharedValues.allTracksData);
    table.setPrefWidth(tableWidth);
    table.setMinWidth(tableWidth);
    table.setMaxWidth(tableWidth);
    table.setEditable(false);
	    
    table.setOnKeyReleased(keyEvent);
	    
    table.getSelectionModel().selectedItemProperty()
	                 .addListener(new ChangeListener() 
	                 {
	              	    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
	              		Track selectedTrack = (Track)newValue;
	              		if (selectedTrack!=null)
	              		{
	              		  System.out.println("selected: "+selectedTrack.getTitle());
	              		//tanda.addTrack(new TandaTrack(selectedTrack.getTitle()));
	              		  SharedValues.title.set(selectedTrack.getTitle());
	              		  SharedValues.selectedAllTracksPathHash.set(selectedTrack.getPathHash());
	              		  
	              		}
	              	}
	             });
		    
	   }
		
		  
	   private double setupAllTracksColumns()
	   {
		 TableColumn titleCol = new TableColumn("Title");
		      titleCol.setMinWidth(100);
		      titleCol.setPrefWidth(150);
		      titleCol.setCellValueFactory(
		          new PropertyValueFactory<Track, String>("title"));
		      titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
		      titleCol.setOnEditCommit(
		          new EventHandler<CellEditEvent<Track, String>>() {
		              @Override
		              public void handle(CellEditEvent<Track, String> t) {
		                  ((Track) t.getTableView().getItems().get(
		                          t.getTablePosition().getRow())
		                          ).setTitle(t.getNewValue());
		              }
		          }
		      );

		      
		       
		  TableColumn artistCol = new TableColumn("Artist");
		      artistCol.setMinWidth(50);
		      artistCol.setPrefWidth(100);
		      artistCol.setCellValueFactory(
		          new PropertyValueFactory<Track, String>("artist"));
		      artistCol.setCellFactory(TextFieldTableCell.forTableColumn());
		      artistCol.setOnEditCommit(
		          new EventHandler<CellEditEvent<Track, String>>() {
		              @Override
		              public void handle(CellEditEvent<Track, String> t) {
		                  ((Track) t.getTableView().getItems().get(
		                      t.getTablePosition().getRow())
		                      ).setArtist(t.getNewValue());
		              }
		          }
		      );
		      
		  TableColumn albumCol = new TableColumn("Album");
		      albumCol.setMinWidth(50);
		      albumCol.setPrefWidth(150);
		      albumCol.setCellValueFactory(
		          new PropertyValueFactory<Track, String>("album"));
		      albumCol.setCellFactory(TextFieldTableCell.forTableColumn());
		      albumCol.setOnEditCommit(
		          new EventHandler<CellEditEvent<Track, String>>() {
		              @Override
		              public void handle(CellEditEvent<Track, String> t) {
		                  ((Track) t.getTableView().getItems().get(
		                      t.getTablePosition().getRow())
		                      ).setAlbum(t.getNewValue());
		              }
		          }
		      );
		      
		  TableColumn genreCol = new TableColumn("Genre");
		      genreCol.setMinWidth(30);
		      genreCol.setPrefWidth(50);
		      genreCol.setCellValueFactory(
		          new PropertyValueFactory<Track, String>("genre"));
		      genreCol.setCellFactory(TextFieldTableCell.forTableColumn());
		      genreCol.setOnEditCommit(
		          new EventHandler<CellEditEvent<Track, String>>() {
		              @Override
		              public void handle(CellEditEvent<Track, String> t) {
		                  ((Track) t.getTableView().getItems().get(
		                      t.getTablePosition().getRow())
		                      ).setGenre(t.getNewValue());
		              }
		          }
		      );
		      
		  TableColumn commentCol = new TableColumn("Comment");
		      commentCol.setMinWidth(50);
		      commentCol.setPrefWidth(100);
		      commentCol.setCellValueFactory(
		          new PropertyValueFactory<Track, String>("comment"));
		      commentCol.setCellFactory(TextFieldTableCell.forTableColumn());
		      commentCol.setOnEditCommit(
		          new EventHandler<CellEditEvent<Track, String>>() {
		              @Override
		              public void handle(CellEditEvent<Track, String> t) {
		                  ((Track) t.getTableView().getItems().get(
		                      t.getTablePosition().getRow())
		                      ).setComment(t.getNewValue());
		              }
		          }
		      );
		      
		      TableColumn durationCol = new TableColumn("Length");
		      durationCol.setMinWidth(50);
		      durationCol.setPrefWidth(50);
		     
		      durationCol.setCellValueFactory(
		          new PropertyValueFactory<Track, String>("duration"));
		      durationCol.setCellFactory(TextFieldTableCell.forTableColumn());
		      
		      TableColumn yearCol = new TableColumn("Year");
		      yearCol.setMinWidth(50);
		      yearCol.setPrefWidth(50);
		     
		      yearCol.setCellValueFactory(
		          new PropertyValueFactory<Track, String>("track_year"));
		      yearCol.setCellFactory(TextFieldTableCell.forTableColumn());
		      
		     
		  table.getColumns().addAll(titleCol, durationCol, yearCol, artistCol, albumCol, genreCol, commentCol);
		      
		  return titleCol.getWidth()+artistCol.getWidth()+albumCol.getWidth()+genreCol.getWidth()+commentCol.getWidth();
	  }
		
	   EventHandler<KeyEvent>keyEvent = new EventHandler<KeyEvent>() 
			   {
			 	public void handle(KeyEvent ke) 
			 	{
			 	  if (ke.getCode()==KeyCode.ESCAPE) { System.out.println("Esc Pressed"); }
			 	  if (ke.getCode()==KeyCode.UP)     { System.out.println("UP Pressed");  }
			 	  if (ke.getCode()==KeyCode.DOWN)   { System.out.println("DOWN Pressed");}
			 	  if (ke.getCode()==KeyCode.RIGHT)  
			 	  {   	
			 		if (playlist.getTandaCount()>0)
			 	 	{	 
			 	 	  if (SharedValues.selectedTanda==-1)	SharedValues.selectedTanda=0;
			 	 	  TandaTreeItem tandaTreeItem = playlist.getTanda(SharedValues.selectedTanda);
			 	 	  tandaTreeItem.addTrack(SharedValues.selectedAllTracksPathHash.get());
			 	 	}
			 	   }
			 	 }
			    };


}
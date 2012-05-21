package main;

import java.applet.Applet;  
import java.applet.AudioClip;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
  

public class GoClient extends Application  
{  
  final static int OPEN = 0;
  final static int BLACK = 1;
  final static int WHITE = 2;
   
  final static int START=0;
  final static int NORTH=1;
  final static int SOUTH=2;
  final static int EAST=3;
  final static int WEST=4;
   
  final static int GROUP_MEMBER=33;
  final static int LIBERTY=43;
  
  final static int STYLE_REGULAR=0;
  final static int STYLE_NUMBERED_1=1;
  final static int STYLE_NUMBERED_2=2;
  final static int STYLE_NUMBERED_3=3;
  final static int STYLE_NUMBERED_4=4;
  final static int STYLE_NUMBERED_5=5;
  final static int STYLE_NUMBERED_6=6;
  final static int STYLE_NUMBERED_7=7;
  final static int STYLE_NUMBERED_8=8;
  final static int STYLE_NUMBERED_9=9;
  final static int STYLE_LAST_MOVE=10;
  
  
   
  private ImageView quit;
  
  Image board_top_image;
  Image board_left_image;
  Image board_right_image;
  Image board_bottom_image;
   
  Image board_fill_image;
   
  Image board_ul_corner_image;
  Image board_ur_corner_image;
  Image board_ll_corner_image;
  Image board_lr_corner_image;
   
  Image grid_ul_corner_image;
  Image grid_ur_corner_image;
  Image grid_ll_corner_image;
  Image grid_lr_corner_image;
   
  Image grid_top_image;
  Image grid_left_image;
  Image grid_right_image;
  Image grid_bottom_image;
  Image grid_hoshi_image;
   
  Image grid_cross_image;
   
//  Image black_stone_image;
//  Image white_stone_image;
//  Image black_move_image;
//  Image white_move_image;
   
   
  ArrayList moveLine = new ArrayList();
  ArrayList moves = new ArrayList();
  ArrayList capturedStonesArray = new ArrayList();
   
  int[][] moveMap = new int[19][19];
  int[][] groupMap = new int[19][19];
  int lastMove=WHITE;
  int moveNumber=0;
  // SoundClip stoneSound;
  AudioClip stoneSound;
  AudioClip errorSound;
   
  Group boardGroup;
  Group movesGroup;
   
  int handicapInt=0;
  int captured[]= new int[3];
   
  Text handicapVal;
  Text movenoVal;
  Text capturedBlackVal;
  Text capturedWhiteVal;
  
   
   
  public void start(final Stage stage) throws Exception  
  {  
    setQuit();
    importImages();
    
    //Stone s = new Stone(black_stone_image, white_stone_image);
    
    stoneSound = Applet.newAudioClip(GoClient.class.getClassLoader().getResource("resources/sounds/stone.wav"));
    errorSound = Applet.newAudioClip(GoClient.class.getClassLoader().getResource("resources/sounds/error.wav"));
    
    
    
	  
    initiallizeMoveMap();
    captured[BLACK]=0;
    captured[WHITE]=0;
      
    Rectangle r = new Rectangle();
    r.setFill(Color.YELLOW);
    r.setX(0);
    r.setY(0);
    r.setWidth(335);
    r.setHeight(670);
    r.setArcWidth(20);
    r.setArcHeight(20);
      
    int yPos=125;
    Text moveno = new Text("Move #:");
    moveno.setX(40);
    moveno.setY(yPos+25);
    moveno.setFont(Font.font("Serif", 20));
      
    movenoVal = new Text("0");
    movenoVal.setX(120);
    movenoVal.setY(yPos+25);
    movenoVal.setFont(Font.font("Serif", 20));
      
    Text handicap = new Text("Handicap:");
    handicap.setX(25);
    handicap.setY(yPos);
    handicap.setFont(Font.font("Serif", 20));
    
    handicapVal = new Text(""+handicapInt);
    handicapVal.setX(120);
    handicapVal.setY(yPos);
    handicapVal.setFont(Font.font("Serif", 20));
    
    Text capturedBlack = new Text("Captured Black:");
    capturedBlack.setX(25);
    capturedBlack.setY(yPos+50);
    capturedBlack.setFont(Font.font("Serif", 20));
    
    capturedBlackVal = new Text("0");
    capturedBlackVal.setX(165);
    capturedBlackVal.setY(yPos+50);
    capturedBlackVal.setFont(Font.font("Serif", 20));
    
    Text capturedWhite = new Text("Captured White:");
    capturedWhite.setX(25);
    capturedWhite.setY(yPos+75);
    capturedWhite.setFont(Font.font("Serif", 20));
    
    capturedWhiteVal = new Text("0");
    capturedWhiteVal.setX(165);
    capturedWhiteVal.setY(yPos+75);
    capturedWhiteVal.setFont(Font.font("Serif", 20));
    
     
    Rectangle infoBox = new Rectangle();
    infoBox.setX(20);
    infoBox.setY(100);
    infoBox.setWidth(300);
    infoBox.setHeight(200);
    infoBox.setArcWidth(20);
    infoBox.setArcHeight(20); 
    infoBox.setFill(Color.GRAY);

    boardGroup = new Group();
    Group board = getBoardBackground();
    Group grid = getGrid();
    
    movesGroup = new Group();
      
    FlowPane flowPane = new FlowPane();
    flowPane.setPadding(new Insets(5, 5, 5, 5));
    flowPane.setVgap(4);
    flowPane.setHgap(4);
    flowPane.setPrefWrapLength(170); // preferred width allows for two columns
    flowPane.setStyle("-fx-background-color: DAE6F3;");
      
    boardGroup.getChildren().add(board);
    boardGroup.getChildren().add(grid);
    boardGroup.getChildren().add(movesGroup);
      
    FlowPane buttonFlowPane = new FlowPane();
    buttonFlowPane.setPadding(new Insets(5, 5, 5, 5));
    buttonFlowPane.setVgap(4);
    buttonFlowPane.setHgap(4);
    buttonFlowPane.setPrefWrapLength(320); // preferred width allows for two columns
    buttonFlowPane.setStyle("-fx-background-color: RED;");
      
    buttonFlowPane.getChildren().add(getClearButton());
    buttonFlowPane.getChildren().add(getSgfButton());
    buttonFlowPane.getChildren().add(getDeleteLastMoveButton());
    buttonFlowPane.getChildren().add(getPreviousMoveButton());
    buttonFlowPane.getChildren().add(getNextMoveButton());
      buttonFlowPane.getChildren().add(loginButton());
      //controlGroup.getChildren().add(buttonFlowPane);
    Group controlGroup = new Group();
    controlGroup.getChildren().add(r);
    controlGroup.getChildren().add(buttonFlowPane);
    controlGroup.getChildren().add(infoBox);
    controlGroup.getChildren().add(handicap);
    controlGroup.getChildren().add(handicapVal);
    controlGroup.getChildren().add(moveno);
    controlGroup.getChildren().add(movenoVal);
    controlGroup.getChildren().add(capturedBlack);
    controlGroup.getChildren().add(capturedBlackVal);
    controlGroup.getChildren().add(capturedWhite);
    controlGroup.getChildren().add(capturedWhiteVal);
  // controlGroup.getChildren().add(loginButton());
            
     
    flowPane.getChildren().add(boardGroup); 
    flowPane.getChildren().add(controlGroup);
      
    final Scene scene = new Scene(flowPane, 1020, 680);
     
    setupMouse();
      
    scene.setFill(null);
  
    //   stage.initStyle(StageStyle.TRANSPARENT);
    stage.setScene(scene);  
    stage.show();  
      
  } // end of start method
   
  private Button getSgfButton() 
  {
    Button b = new Button("Get SGF");
    // b.setLayoutX(10);
    // b.setLayoutY(10);
    EventHandler bHandler = new EventHandler<ActionEvent>() {
	          public void handle(ActionEvent event) {
	              System.out.println("Hello World!"); 
	              getMoves();  
	          } };
	  
     b.setOnAction(bHandler);
     return b;
  }

  private void setupMouse() 
  {
     
    boardGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {public void handle(MouseEvent t) 
    { 
      int thisMoveColor=0;
      BoardPosition bp = new BoardPosition(t.getX(),t.getY());
      if (lastMove==BLACK) thisMoveColor=WHITE;  else thisMoveColor=BLACK;
      Stone s = new Stone(thisMoveColor, t.getX(),t.getY(), STYLE_LAST_MOVE);
      placeStone(s);}});
  }

  Button loginButton()
{
    Button btn = new Button();
    
     btn.setText("Open Dialog");
     btn.setOnAction(new EventHandler<ActionEvent>() {

         @Override
         public void handle(ActionEvent event) {
             final Stage myDialog = new Stage();
          //   myDialog.initModality(Modality.WINDOW_MODAL);
          myDialog.initModality(Modality.APPLICATION_MODAL);
             Button okButton = new Button("CLOSE");
             okButton.setOnAction(new EventHandler<ActionEvent>(){

                 @Override
                 public void handle(ActionEvent arg0) {
                     myDialog.close();
                 }
              
             });
          
             Scene myDialogScene = new Scene(VBoxBuilder.create()
                     .children(new Text("Hello! it's My Dialog."), okButton)
                     .alignment(Pos.CENTER)
                     .padding(new Insets(10))
                     .build());
          
             myDialog.setScene(myDialogScene);
             myDialog.show();
         }
     });
     return btn;
}
  
  private Button getDeleteLastMoveButton() 
  {
    Button deleteLastMove = new Button("<X");
    EventHandler bHandler2 = new EventHandler<ActionEvent>() { public void handle(ActionEvent event) 
    {
      removeLastStone(); 
    }};
    deleteLastMove.setOnAction(bHandler2);
    return deleteLastMove; 
  }
  
  private Button getPreviousMoveButton() 
  {
    Button b = new Button("<");
    EventHandler bHandler = new EventHandler<ActionEvent>() { public void handle(ActionEvent event) 
    {
      rewindMove(); 
    }};
    b.setOnAction(bHandler);
    return b; 
  }
  
  
  // replay rewound moves
  private Button getNextMoveButton() 
  {
    Button b = new Button(">");
    EventHandler bHandler = new EventHandler<ActionEvent>() { public void handle(ActionEvent event) 
    {
      nextMove(); 
    }

        };
    b.setOnAction(bHandler);
    return b; 
  }
  
  private Button getClearButton() 
  {
    Button b = new Button("Clear");
    EventHandler bHandler = new EventHandler<ActionEvent>() { public void handle(ActionEvent event) 
    {
      clear(); 
    }

           

        };
    b.setOnAction(bHandler);
    return b; 
  }


  private void initiallizeMoveMap() 
  {
    for(int i=0; i<19; i++)
    {
      for(int j=0; j<19; j++)
      {
	moveMap[i][j]=OPEN;
      }
    }
  }
  
  private void clearGroupMap() 
  {
    for(int i=0; i<19; i++)
    {
      for(int j=0; j<19; j++)
      {
	groupMap[i][j]=OPEN;
      }
    }
  }
	
	private void printMoveMap() 
	   {
		for(int j=0; j<19; j++)
		{
		  for(int i=0; i<19; i++)
		  {
			System.out.print(moveMap[i][j]+" ");
		  }
		  System.out.println();
		}
    }

        /*
  static void findStone(Group moves, String boardPosition)
  {
	List movesList = moves.getChildren();
	Iterator it = movesList.iterator();
	Stone s;
	while(it.hasNext())
	{
	  s = (Stone)it.next();
	  if (s.getBoardPosition().equals(boardPosition))
	  {
		System.out.println("Found Stone... Color: "+s.getStoneColor());
		s.setVisible(false);
	  }
	}
  }

*/
   
   
   private void importImages()
   {
	 board_ul_corner_image = new Image(GoClient.class.getResourceAsStream("/images/wood4_ul.gif"));
	 board_ur_corner_image = new Image(GoClient.class.getResourceAsStream("/images/wood4_ur.gif"));
	 board_ll_corner_image = new Image(GoClient.class.getResourceAsStream("/images/wood4_dl.gif"));
	 board_lr_corner_image = new Image(GoClient.class.getResourceAsStream("/images/wood4_dr.gif"));
	 
	 board_top_image = new Image(GoClient.class.getResourceAsStream("/images/wood4_u.gif"));
	 board_left_image = new Image(GoClient.class.getResourceAsStream("/images/wood4_l.gif"));
	 board_right_image = new Image(GoClient.class.getResourceAsStream("/images/wood4_r.gif"));
	 board_bottom_image = new Image(GoClient.class.getResourceAsStream("/images/wood4_d.gif"));
	
	 board_fill_image = new Image(GoClient.class.getResourceAsStream("/images/wood4.gif"));
	 
	 grid_ul_corner_image = new Image(GoClient.class.getResourceAsStream("/images/ul.gif"));
	 grid_ur_corner_image = new Image(GoClient.class.getResourceAsStream("/images/ur.gif"));
	 grid_ll_corner_image = new Image(GoClient.class.getResourceAsStream("/images/dl.gif"));
	 grid_lr_corner_image = new Image(GoClient.class.getResourceAsStream("/images/dr.gif"));
	 
	 grid_top_image = new Image(GoClient.class.getResourceAsStream("/images/u.gif"));
	 grid_left_image = new Image(GoClient.class.getResourceAsStream("/images/el.gif"));
	 grid_right_image = new Image(GoClient.class.getResourceAsStream("/images/er.gif"));
	 grid_bottom_image = new Image(GoClient.class.getResourceAsStream("/images/d.gif"));
	 
	 grid_cross_image = new Image(GoClient.class.getResourceAsStream("/images/e.gif"));
	 grid_hoshi_image = new Image(GoClient.class.getResourceAsStream("/images/h.gif"));
	 
	// black_stone_image = new Image(GoClient.class.getResourceAsStream("/images/b.gif"));
	// white_stone_image = new Image(GoClient.class.getResourceAsStream("/images/w.gif"));
	// black_move_image = new Image(GoClient.class.getResourceAsStream("/images/bm.gif"));
	// white_move_image = new Image(GoClient.class.getResourceAsStream("/images/wm.gif"));
	 
	 
	// File sgfFile = new File(GoClient.class.getResourceAsStream("/sgf/test.sgf"));
   }
   
   private void getMoves()
   {
     clear();
     readTestSgfFile();
     Iterator it = moveLine.iterator();
     String sgfPosition="";
     String moveLine="";
	   
     while(it.hasNext())
     {
       moveLine=(String)it.next();
       if (moveLine.startsWith("HA"))
       {
	 handicapInt=moveLine.charAt(3)-48;  
         handicapVal.setText(""+handicapInt);
         continue;
       }
		      
       if (moveLine.startsWith("AB"))
       {
	 if (handicapInt>0)
	 {
	   int xPos=3;
	   int yPos=5;
	   for(int i=0; i<handicapInt; i++)
	   {
	     sgfPosition=  moveLine.substring(xPos,yPos);
	     //placeStone(BLACK, sgfPosition, STYLE_REGULAR);
             placeStone(new Stone(BLACK, sgfPosition, STYLE_REGULAR));
	     xPos+=4;
	     yPos+=4;
	   }
	 }
	 continue;
       }
	 
       if (moveLine.startsWith(";MN"))
       {
	// moveNumber=moveLine.charAt(4)-48;
	 System.out.println("Move Number: "+moveNumber);
	 sgfPosition = moveLine.substring(8,10);
         placeStone(new Stone(WHITE, sgfPosition, STYLE_REGULAR));
	
	 continue;
       }
       //   if (test) continue;
       if (moveLine.startsWith(";B"))
       {
	 sgfPosition=moveLine.substring(3,5);
         placeStone(new Stone(BLACK, sgfPosition, STYLE_REGULAR));
        // placeStone(BLACK, sgfPosition, STYLE_REGULAR);
	 continue;
       }
      
       if (moveLine.startsWith(";W"))
       {
	 sgfPosition=moveLine.substring(3,5);
        // placeStone(WHITE, sgfPosition, STYLE_REGULAR);
         placeStone(new Stone(WHITE, sgfPosition, STYLE_REGULAR));
	 continue;
       }
     }
	   
     movenoVal.setText(""+moveNumber);
     putMoveImageOnLastStone();
     stoneSound.play();
   }
   
   private void readTestSgfFile()
   {
     try 
     {
       InputStream in = GoClient.class.getResourceAsStream("/sgf/test.sgf"); 
       InputStreamReader isr = new InputStreamReader(in);
       BufferedReader br = new BufferedReader(isr);
       String line;
	      
       while ((line = br.readLine()) != null) { moveLine.add(line); }
     } catch (IOException io) { System.out.println("Ooops"); }
   }
   
   private Group getGrid()
   {
	 Group grid = new Group();
	 
	 grid.getChildren().add(getImageView(grid_ul_corner_image,  0,   0));
	 grid.getChildren().add(getImageView(grid_ur_corner_image,630,   0));
	 grid.getChildren().add(getImageView(grid_ll_corner_image,  0, 630));
	 grid.getChildren().add(getImageView(grid_lr_corner_image,630, 630));
	 
	 for(int i=0; i<17; i++)
	 {
	   grid.getChildren().add(getImageView(grid_top_image,((i+1)*35),0)); 
	 }
	 
	 for(int i=0; i<17; i++)
	 {
	   grid.getChildren().add(getImageView(grid_left_image,0,((i+1)*35))); 
	 }
	 
	 for(int j=0; j<17; j++)
	 {
	   for(int i=0; i<17; i++)
	   {
	     grid.getChildren().add(getImageView(grid_cross_image,((i+1)*35),((j+1)*35))); 
	   }
	 }
	 
	 for(int i=0; i<17; i++)
	 {
	   grid.getChildren().add(getImageView(grid_right_image,630,((i+1)*35))); 
	 }
	 
	 for(int i=0; i<17; i++)
	 {
	   grid.getChildren().add(getImageView(grid_bottom_image,((i+1)*35),630)); 
	 }
	 
	 grid.getChildren().add(getImageView(grid_hoshi_image,105,105));
	 grid.getChildren().add(getImageView(grid_hoshi_image,315,105));
	 grid.getChildren().add(getImageView(grid_hoshi_image,525,105));
	 grid.getChildren().add(getImageView(grid_hoshi_image,105,315));
	 grid.getChildren().add(getImageView(grid_hoshi_image,315,315));
	 grid.getChildren().add(getImageView(grid_hoshi_image,525,315));
	 grid.getChildren().add(getImageView(grid_hoshi_image,105,525));
	 grid.getChildren().add(getImageView(grid_hoshi_image,315,525));
	 grid.getChildren().add(getImageView(grid_hoshi_image,525,525));
	 
	// grid.getChildren().add(getImageView(black_stone_image,grid_origin.x+35,grid_origin.y+40));
	// grid.getChildren().add(getImageView(black_stone_image,grid_origin.x+(2*35),grid_origin.y+40));
	// grid.getChildren().add(getImageView(black_stone_image,grid_origin.x+(3*35),grid_origin.y+40));
	 
	 return grid;
   }
   
   private Group getBoardBackground()
   {
	 Group board = new Group();
	 
	 board.getChildren().add(getImageView(board_ul_corner_image,0,0));
	 board.getChildren().add(getImageView(board_ur_corner_image,660,0));
	 board.getChildren().add(getImageView(board_ll_corner_image,0,660));
	 board.getChildren().add(getImageView(board_lr_corner_image,660,660));
	 
	 board.getChildren().add(getImageView(board_left_image,0,10));
	 board.getChildren().add(getImageView(board_left_image,0,110));
	 board.getChildren().add(getImageView(board_left_image,0,210));
	 board.getChildren().add(getImageView(board_left_image,0,310));
	 board.getChildren().add(getImageView(board_left_image,0,410));
	 board.getChildren().add(getImageView(board_left_image,0,510));
	 board.getChildren().add(getImageView(board_left_image,0,560));
	// board.getChildren().add(getImageView(board_left_image,board_origin.x,460));
	 
	 board.getChildren().add(getImageView(board_right_image,660,10));
	 board.getChildren().add(getImageView(board_right_image,660,110));
	 board.getChildren().add(getImageView(board_right_image,660,210));
	 board.getChildren().add(getImageView(board_right_image,660,310));
	 board.getChildren().add(getImageView(board_right_image,660,410));
	 board.getChildren().add(getImageView(board_right_image,660,510));
	 board.getChildren().add(getImageView(board_right_image,660,560));
	// board.getChildren().add(getImageView(board_right_image,660,660));
	 		 
	 board.getChildren().add(getImageView(board_top_image,10,0));
	 board.getChildren().add(getImageView(board_top_image,160,0));
	 board.getChildren().add(getImageView(board_top_image,310,0));
	 board.getChildren().add(getImageView(board_top_image,460,0));
	 board.getChildren().add(getImageView(board_top_image,510,0));
	
	 board.getChildren().add(getImageView(board_bottom_image,10,660));
	 board.getChildren().add(getImageView(board_bottom_image,160,660));
	 board.getChildren().add(getImageView(board_bottom_image,310,660));
	 board.getChildren().add(getImageView(board_bottom_image,460,660));
	 board.getChildren().add(getImageView(board_bottom_image,510,660));
	
	 board.getChildren().add(getImageView(board_fill_image,10,10));
	 board.getChildren().add(getImageView(board_fill_image,160,10));
	 board.getChildren().add(getImageView(board_fill_image,310,10));
	 board.getChildren().add(getImageView(board_fill_image,460,10));
	 board.getChildren().add(getImageView(board_fill_image,510,10));
	 
	 
	 board.getChildren().add(getImageView(board_fill_image,10,160));
	 board.getChildren().add(getImageView(board_fill_image,160,160));
	 board.getChildren().add(getImageView(board_fill_image,310,160));
	 board.getChildren().add(getImageView(board_fill_image,460,160));
	 board.getChildren().add(getImageView(board_fill_image,510,160));
	 
	 board.getChildren().add(getImageView(board_fill_image,10,310));
	 board.getChildren().add(getImageView(board_fill_image,160,310));
	 board.getChildren().add(getImageView(board_fill_image,310,310));
	 board.getChildren().add(getImageView(board_fill_image,460,310));
	 board.getChildren().add(getImageView(board_fill_image,510,310));
	 
	 board.getChildren().add(getImageView(board_fill_image,10,460));
	 board.getChildren().add(getImageView(board_fill_image,160,460));
	 board.getChildren().add(getImageView(board_fill_image,310,460));
	 board.getChildren().add(getImageView(board_fill_image,460,460));
	 board.getChildren().add(getImageView(board_fill_image,510,460));
	 
	 board.getChildren().add(getImageView(board_fill_image,10,510));
	 board.getChildren().add(getImageView(board_fill_image,160,510));
	 board.getChildren().add(getImageView(board_fill_image,310,510));
	 board.getChildren().add(getImageView(board_fill_image,460,510));
	 board.getChildren().add(getImageView(board_fill_image,510,510));
	 
	 return board;
   }
   
   private ImageView getImageView(Image image, float x, float y)
   {
	 ImageView imageView = new ImageView(image);
	 imageView.setX(x);
	 imageView.setY(y);
	 return imageView;
   }

private void setQuit() 
{
  quit = new ImageView(new Image(GoClient.class.getResourceAsStream("/images/x1.png"))); 
  quit.setFitHeight(25);
  quit.setFitWidth(25);
  quit.setX(970);
  quit.setY(15);
	
  quit.setOnMouseClicked(new EventHandler<MouseEvent>() {public void handle(MouseEvent t) { System.exit(0);}});
}  
/*
private void placeStone(int color, String sgfPosition, int style) 
{
  Stone s = new Stone(color, sgfPosition, style);
  if (moveMap[s.x][s.y]!=OPEN) return;
  
  moveMap[s.x][s.y]=color;
  movesGroup.getChildren().add(s);
  lastMove=color;
  moves.add(new Move(s.x, s.y, color));
 // if (s.style==STYLE_LAST_MOVE) removeMoveImageFromPreviousMove();
  moveNumber++;
  movenoVal.setText(""+moveNumber);
  checkLibertiesOfNeighbors(s.x, s.y);
//  if (s.style==STYLE_LAST_MOVE) stoneSound.play();
}


private void placeStone(int color, int x, int y) 
{
  if (moveMap[x][y]!=OPEN) return;
  
  moveMap[x][y]=color;	
  movesGroup.getChildren().add(new Stone(color, x, y, STYLE_LAST_MOVE));
  lastMove=color;
  moves.add(new Move(x, y, color));
  removeMoveImageFromPreviousMove();
  moveNumber++;
  movenoVal.setText(""+moveNumber);
  checkLibertiesOfNeighbors(x, y);
  stoneSound.play();
}
*/

private void placeStone(Stone s) 
{
  if (moveMap[s.x][s.y]!=OPEN) return;
  
  moveMap[s.x][s.y]=s.getStoneColor();	
  movesGroup.getChildren().add(s);
  lastMove=s.getStoneColor();
  moves.add(new Move(s.x, s.y, s.getStoneColor()));
  if (s.style==STYLE_LAST_MOVE) removeMoveImageFromPreviousMove();
  moveNumber++;
  movenoVal.setText(""+moveNumber);
  checkLibertiesOfNeighbors(s.x, s.y);
  if (s.style!=STYLE_REGULAR)stoneSound.play();
}



void removeMoveImageFromPreviousMove()
{
  if (moveNumber==0) return;
  if (moveNumber>=moves.size()) return; 
  
  Move m = (Move) moves.get(moveNumber-1);
  ObservableList moveList  =movesGroup.getChildren();
  ListIterator it = moveList.listIterator(moveList.size());
  int color=0;
              
   Stone stone;
   BoardPosition bp;
   int i=moveList.size()-1;
   while(it.hasPrevious())
   {
     stone=(Stone)it.previous();
    // bp=stone.getBoardPosition();
     if ((stone.x==m.x)&&(stone.y==m.y)) 
     { 
       stone.setRegularImage();;
       break; 
     }
     i--;
    }
}

void restoreMoveImageToPrevousStone(Move m)
{
    ObservableList moveList  =movesGroup.getChildren();
  ListIterator it = moveList.listIterator(moveList.size());
  int color=0;
              
   Stone stone;
   BoardPosition bp;
   int i=moveList.size()-1;
   while(it.hasPrevious())
   {
     stone=(Stone)it.previous();
    // bp=stone.getBoardPosition();
     if ((stone.x==m.x)&&(stone.y==m.y)) 
     { 
       stone.setMoveImage();;
       break; 
     }
     i--;
    }
}

void putMoveImageOnLastStone()
{
  Move m = (Move)moves.get(moves.size()-1);
  ObservableList moveList  = movesGroup.getChildren();
  ListIterator it = moveList.listIterator(moveList.size());
  int color=0;
              
   Stone stone;
   BoardPosition bp;
   int i=moveList.size()-1;
   while(it.hasPrevious())
   {
     stone=(Stone)it.previous();
   //  bp=stone.getBoardPosition();
     if ((stone.x==m.x)&&(stone.y==m.y)) 
     { 
       stone.setMoveImage();;
       break; 
     }
     i--;
    }
}
  
void removeLastStone()  // NOT capture
{
  int color;
  int size = moves.size();
  if (size>0)
  {
    Move m = (Move) moves.get(size-1);
    
    color = moveMap[m.x][m.y];
    moveMap[m.x][m.y]=OPEN;
    if (color==BLACK) lastMove=WHITE;
    else lastMove=BLACK;
    
   
    removeStone(m.x, m.y);
    moves.remove(size-1);
  }
  
  size = moves.size();
  if (size>0)
  {
    restoreMoveImageToPrevousStone((Move)moves.get(size-1));
  }
  
  restoreCapturedPieces();
  moveNumber--;
   movenoVal.setText(""+moveNumber);
}



void removeStone(int x, int y)  // remove a stone... NOT capture
{
  ObservableList moveList  =movesGroup.getChildren();
  ListIterator it = moveList.listIterator(moveList.size());
  int color=0;
              
   Stone stone;
   BoardPosition bp;
   int i=moveList.size()-1;
   while(it.hasPrevious())
   {
     stone=(Stone)it.previous();
    // bp=stone.getBoardPosition();
    // sp=bp.getSimplePosition();
     if ((stone.x==x)&&(stone.y==y)) 
     { 
        // System.out.println("found "+i);
       moveMap[x][y]=OPEN; 
       moveList.remove(i); 
       break; 
     }
     i--;
    }
  }
   /** 
    * Main function used to run JavaFX 2.0 example. 
    *  
    * @param arguments Command-line arguments: none expected. 
    */  
   public static void main(final String[] arguments)  
   {  
      Application.launch(arguments);  
   }

    private Neighbors countLiberties(int x, int y, int color, int noCheck) 
    {
        boolean debug=false;
        
        Neighbors n = new Neighbors(x, y, color);
        if (noCheck==START) clearGroupMap();
        if (debug) 
        { 
          if (noCheck==START) 
          {
            System.out.println(); 
          }
          System.out.println("Stone: "+x+"-"+y); 
        }
        
        // check if been here already
       if (groupMap[x][y]==GROUP_MEMBER) return null;
       groupMap[x][y]=GROUP_MEMBER;
        
       
       if (noCheck!=NORTH) directionCheck(x,y,color, n,NORTH); 
       if (noCheck!=SOUTH) directionCheck(x,y,color, n,SOUTH);
       if (noCheck!=WEST)  directionCheck(x,y,color, n,WEST);
       if (noCheck!=EAST)  directionCheck(x,y,color, n,EAST);
        
       return n;
    }
    
    void directionCheck(int x, int y, int color, Neighbors n, int direction)
    {
      boolean debug=false;  
      int checkX=x, checkY=y;
      int oppositeDirection=0;
      
      if (direction==NORTH) { if (y==0) return;  checkY=y-1; oppositeDirection=SOUTH; }
      if (direction==SOUTH) { if (y==18) return; checkY=y+1; oppositeDirection=NORTH; }
      if (direction==EAST)  { if (x==18) return; checkX=x+1; oppositeDirection=WEST;  }
      if (direction==WEST)  { if (x==0) return;  checkX=x-1; oppositeDirection=EAST;  }
      
      if (moveMap[checkX][checkY]==OPEN) 
      {
        if (groupMap[checkX][checkY]!=LIBERTY)
        {    
          groupMap[checkX][checkY]=LIBERTY;   
          n.liberties++;
          n.libertyPositions.add(new SimplePosition(checkX,checkY));
          if (debug) System.out.println("  liberty Soutn");
        }
      }
      else if (moveMap[checkX][checkY]==color) 
      {
        if (debug) System.out.println("  moving South");
        n.accumulate(countLiberties(checkX, checkY, color, oppositeDirection));
      }
            
    }

    private Neighbors checkNorth(int color) 
    {
        Neighbors n = new Neighbors(color);
        return n;
    }

    private void checkLibertiesOfNeighbors(int x, int y) 
    {
      boolean debug=true;
      int checkX=x, checkY=y, color=0, startColor=0;
      
      startColor = moveMap[x][y];
      if (debug) System.out.println("Start Color is "+startColor);
      
      //CHECK NORTH
      checkDirectionForNeighbors(x,y,NORTH);
      checkDirectionForNeighbors(x,y,SOUTH);
      checkDirectionForNeighbors(x,y,EAST);
      checkDirectionForNeighbors(x,y,WEST);
      
      if (debug) System.out.println();
    }

     void checkDirectionForNeighbors(int x, int y, int direction)
     {
       boolean debug=false;
       int checkX=x, checkY=y, color=0, startColor=0;
       String directionString="";
       startColor = moveMap[x][y];
            
       if (direction==NORTH) { if (y==0) return;  checkY=y-1; directionString="NORTH"; }
       if (direction==SOUTH) { if (y==18) return; checkY=y+1; directionString="SOUTH"; }
       if (direction==EAST)  { if (x==18) return; checkX=x+1; directionString="EAST";  }
       if (direction==WEST)  { if (x==0) return;  checkX=x-1; directionString="WEST";  }
       
       
         color = moveMap[checkX][checkY];
         if (debug) System.out.println(directionString+" Color is: "+color);
         if ((color!=OPEN)&&(color!=startColor))
         {
           Neighbors n = countLiberties(checkX, checkY, color, START);
           if (n.liberties==0) captureGroup(n.groupPositions);
           if (debug) System.out.println("Group to the "+directionString+" has "+n.liberties+" liberties");
         }
         else 
         {
           if (debug) System.out.println("No Group or non-enemy to the "+directionString);   
         }
       }

    private void captureGroup(List<SimplePosition> groupPositions) 
    {
        System.out.println("CAPTURE GROUP");
      Iterator it=groupPositions.iterator();
      SimplePosition sp;
      while(it.hasNext())
      {
         sp=(SimplePosition)it.next(); 
         System.out.println("Position: "+sp.x+"-"+sp.y);
         capturePiece(sp.x, sp.y);
         
      }
    }

    private void capturePiece(int x, int y) 
    {
      ObservableList moveList  =movesGroup.getChildren();
      Iterator it = moveList.iterator();
      int color=0;
              
      Stone stone;
      BoardPosition bp;
      SimplePosition sp;
      int i=0;
      while(it.hasNext())
      {
        stone=(Stone)it.next();
    //    bp=stone.getBoardPosition();
     //   sp=bp.getSimplePosition();
        if ((stone.x==x)&&(stone.y==y)) 
        { 
          stone.setCaptureMoveNumber(moveNumber);  
          capturedStonesArray.add(stone);
          color=moveMap[x][y];
          captured[color]++;
          moveMap[x][y]=OPEN; 
          moveList.remove(i); break; 
        }
        i++;
      }
      
      capturedBlackVal.setText(""+captured[BLACK]);
      capturedWhiteVal.setText(""+captured[WHITE]);
    }

    private void restoreCapturedPieces() 
    {
      Iterator it = capturedStonesArray.iterator();
      Stone s;
      BoardPosition bp;
      boolean capturedStonesFound=false;
      
      while(it.hasNext())
      {
        s = (Stone)it.next();
      //  bp=s.getBoardPosition();
        if ((s.getCaptureMoveNumber()==moveNumber)&&(s.forget==false))
        {
          movesGroup.getChildren().add(s);
          moveMap[s.x][s.y]=s.getStoneColor();
          capturedStonesFound=true;
         // capturedStonesArray.remove(i);
        }
      }
      
      if (capturedStonesFound)
      {
        Iterator it2 = capturedStonesArray.iterator();
        int i=0;
        while(it2.hasNext())
        {
          s = (Stone)it2.next();
         // bp=s.getBoardPosition();
          if (s.getCaptureMoveNumber()==moveNumber)
          {
            s.forget=true;
          }
          i++;
        }
      }
    }
    
    private void rewindMove() 
    {
      throw new UnsupportedOperationException("Not yet implemented");
    }
       
      
  private void nextMove() 
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }
   
   private void clear() 
   {
     ObservableList list=movesGroup.getChildren();  
     while (list.size() > 0)
     {
       list.remove(list.size()-1);
     }
     
     initiallizeMoveMap();
     moves = new ArrayList();
     capturedStonesArray = new ArrayList();
     moveNumber=0;
     captured[BLACK]=0;
     captured[WHITE]=0;
     handicapVal.setText("0");
     movenoVal.setText("0");
     capturedBlackVal.setText("0");
     capturedWhiteVal.setText("0");
   }
    
}  
package tangodj2;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Equalizer 
{
	private static final double START_FREQ = 250.0;
    private static final int BAND_COUNT = 7;
    
    private SpectrumBar[] spectrumBars;
    private SpectrumListener spectrumListener;
    GridPane gp = new GridPane();
    BorderPane borderPane = new BorderPane();
    
    public Equalizer(MediaPlayer mp)
    {
     gp.getStyleClass().addAll("pane","grid");
    // gp.getStyleClass().add("pane");
     // gp.setStyle("-fx-background-color: #bfc2c7;");
    	createEQBands(mp);
      createSpectrumBars( mp);
      spectrumListener = new SpectrumListener(START_FREQ, mp, spectrumBars);
      mp.setAudioSpectrumListener(spectrumListener);
      borderPane.setLayoutY(100);
      // KLUDGE
      Rectangle r = new Rectangle(1000, 75, Color.BLACK);
      borderPane.setTop(r);
      borderPane.setCenter(gp);
    }
    
    public BorderPane getGridPane()
    {
    	return borderPane;
    }

	private void createEQBands(MediaPlayer mp) {
	    final ObservableList<EqualizerBand> bands =
	            mp.getAudioEqualizer().getBands();

	    bands.clear();
	    
	    double min = EqualizerBand.MIN_GAIN;
	    double max = EqualizerBand.MAX_GAIN;
	    double mid = (max - min) / 2;
	    double freq = START_FREQ;

	    // Create the equalizer bands with the gains preset to
	    // a nice cosine wave pattern.
	    for (int j = 0; j < BAND_COUNT; j++) {
	      // Use j and BAND_COUNT to calculate a value between 0 and 2*pi
	      double theta = (double)j / (double)(BAND_COUNT-1) * (2*Math.PI);
	      
	      // The cos function calculates a scale value between 0 and 0.4
	      double scale = 0.4 * (1 + Math.cos(theta));
	      
	      // Set the gain to be a value between the midpoint and 0.9*max.
	      double gain = min + mid + (mid * scale);
	      
	      bands.add(new EqualizerBand(freq, freq/2, gain));
	      freq *= 2;
	    }
	    
	    for (int i = 0; i < bands.size(); ++i) {
	      EqualizerBand eb = bands.get(i);
	      Slider s = createEQSlider(eb, min, max);

	      final Label l = new Label(formatFrequency(eb.getCenterFrequency()));
	      l.getStyleClass().addAll("mediaText", "eqLabel");

	      GridPane.setHalignment(l, HPos.CENTER);
	      GridPane.setHalignment(s, HPos.CENTER);
	      GridPane.setHgrow(s, Priority.ALWAYS);

	      gp.add(l, i, 1);
	      gp.add(s, i, 2);
	    }
	  }
	
	private Slider createEQSlider(EqualizerBand eb, double min, double max) {
	    final Slider s = new Slider(min, max, eb.getGain());
	    s.getStyleClass().add("eqSlider");
	    s.setOrientation(Orientation.VERTICAL);
	    s.valueProperty().bindBidirectional(eb.gainProperty());
	    s.setPrefWidth(44);
	    return s;
	  }

	private void createSpectrumBars(MediaPlayer mp) {
	  spectrumBars = new SpectrumBar[BAND_COUNT];

	  for (int i = 0; i < spectrumBars.length; i++) {
	    spectrumBars[i] = new SpectrumBar(100, 20);
	    spectrumBars[i].setMaxWidth(44);
	    GridPane.setHalignment(spectrumBars[i], HPos.CENTER);
	    gp.add(spectrumBars[i], i, 0);
	  }
	}

	  private String formatFrequency(double centerFrequency) {
	    if (centerFrequency < 1000) {
	      return String.format("%.0f Hz", centerFrequency);
	    } else {
	      return String.format("%.1f kHz", centerFrequency / 1000);
	    }
	  }
}
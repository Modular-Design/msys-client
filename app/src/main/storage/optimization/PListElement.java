package msys.client.optimization;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import mdd.client.GUIEventHandler;
import mdd.client.IGUIEventClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class PListElement extends Group implements ChangeListener<Bounds> {
    private GridPane header = new GridPane();
    private VBox dna = new VBox();
    private Rectangle titlebar;
    private Rectangle background;

    public JSONArray jdna;
    public double fitness;
    protected GUIEventHandler _eventHandler;

    Label _status = new Label();
    Label _fitness = new Label();
    Label _time = new Label();

    public PListElement(Permutation per){
        _eventHandler = GUIEventHandler.getEventHandler(1);
        setFocusTraversable(true);
        dna.setSpacing(10);
        dna.setTranslateX(5);
        dna.setTranslateY(30);

        titlebar = new Rectangle(0.0f, 0.0f, 1.0, 20f);
        titlebar.setArcWidth(10.0f);
        titlebar.setArcHeight(10.0f);

        //background = new Rectangle(0.0f, 0.0f, 150-20, 150 + 40);
        background = new Rectangle(0.0f, 0.0f, 1.0, 1.0);
        background.setArcWidth(10.0f);
        background.setArcHeight(10.0f);
        background.setFill(Color.GRAY);
        header.setTranslateX(10);
        Color color = Color.DARKCYAN;
        titlebar.setFill(color);

        header.add(_status,0,0);
        header.add(_fitness,1,0);
        header.add(_time,2,0);

        header.setHgap(5.0f);

        configure(per);
        getChildren().clear();
        getChildren().addAll( background, titlebar, header, dna);
        dna.layoutBoundsProperty().addListener(this);
        header.layoutBoundsProperty().addListener(this);
    }

    protected void configure(Permutation per){
        Vector<String> keys = per.getKeys();
        Vector<Integer> ids = per.getIDs();
        for (int i = 0; i < keys.size(); ++i){
            String key = keys.get(i);
            if (ids.get(i) != null) {
                int id = ids.get(i);
                if (key.equals("status")) {
                    switch (per.getValue(id).shortValue()) {
                        case -1:
                            _status.setText("status: error");
                            break;
                        case 0:
                        case 1:
                            _status.setText("status: ok");
                            addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                                _eventHandler.publishEvent(10, "OPEN", per.jprocessor);
                            });
                            break;
                        default:
                            _status.setText("status: unknown " + per.getValue(id).shortValue());
                    }
                }
                if (key.equals("fitness")) {
                    fitness = per.getValue(id);
                    _fitness.setText("fitness: " + per.getValue(id).toString());
                }
                if (key.equals("time")) {
                    _time.setText("time: " + per.getValue(id).toString());
                }
            }
        }
        if (dna.getChildren().isEmpty()){
            jdna = per.jdna;
            for (int j = 0; j < per.jdna.size(); ++j){
                dna.getChildren().add(new Label(per.jdna.get(j).toString()));
            }
        }
    }

    @Override
    public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldValue, Bounds newValue) {
        double width = header.getWidth();
        if (newValue.getWidth() > width){
            width =newValue.getWidth();
            width += 10;
        }else{
            width += 10;
            width +=10;
        }
        titlebar.setWidth(width);
        titlebar.setHeight(20.0f);
        background.setWidth(width);
        background.setHeight(dna.getTranslateY()+dna.getHeight()+10);
    }
}

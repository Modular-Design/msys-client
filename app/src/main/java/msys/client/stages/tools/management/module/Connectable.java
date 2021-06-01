package msys.client.stages.tools.management.module;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.VisualElement;

import java.util.Map;

public class Connectable extends VisualElement {
    private Map<String, Object> config;
    private Polygon in_shape = new Polygon();
    private Rectangle out_shape = new Rectangle(15, 15);
    private Label name = new Label("TEST");
    private Group input_layout = new Group();
    private Group output_layout = new Group();


    public Connectable(Map<String, Object> config) {
        super(0,2);
        this.config = config;

        in_shape.setFill(Color.GREEN);
        in_shape.setStrokeWidth(1);
        in_shape.setStroke(Color.BLACK);
        // in_shape.setTranslateY(5);
        out_shape.setFill(Color.GREEN);
        out_shape.setStrokeWidth(1);
        out_shape.setStroke(Color.BLACK);
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {

    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {

    }


    public Node asInput(boolean inverted){

        HBox layout;
        in_shape.getPoints().clear();
        if (inverted){
            in_shape.getPoints().addAll(
                    -15.0, 7.5,
                    0.0, 0.0,
                    0.0, 15.0
            );
            layout = new HBox(name, in_shape);
        } else {
            in_shape.getPoints().addAll(
                    15.0, 7.5,
                    0.0, 0.0,
                    0.0, 15.0
            );
            layout = new HBox(in_shape, name);
        }
        input_layout.getChildren().addAll(layout);
        return input_layout;
    }

    public Node asInput(){
        return asInput(false);
    }

    public Node asOutput(boolean inverted){
        HBox layout;
        if (inverted){
            layout= new HBox(out_shape, name);
        } else {
            layout= new HBox(name, out_shape);
        }
        output_layout.getChildren().addAll(layout);
        return output_layout;
    }

    public Node asOutput(){
        return asOutput(false);
    }


    @Override
    public Node getVisual() {
        return null;
    }
}

package msys.client.stages.tools.management.module;

import com.google.gson.Gson;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.VisualElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Connectable extends VisualElement {
    private Map<String, Object> config;
    private Metadata metadata;
    private String identifier;
    private Polygon in_shape = new Polygon();
    private Rectangle out_shape = new Rectangle(15, 15);
    private Label name = new Label("TEST");
    private HBox input_layout = new HBox();
    private HBox output_layout = new HBox();


    public Connectable(Map<String, Object> config) {
        super(0,2);

        updateLayout(config);

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
        updateLayout(msg);
    }

    private void updateLayout(Map<String , Object> config){
        this.config = config;
        this.metadata = Parser.extractMetaData(this.config);
        this.name.setText(Parser.extractName(this.config));
        this.identifier = Parser.extractIdentifier(this.config);
    }


    public Node asInput(boolean inverted){

        HBox layout;
        in_shape.getPoints().clear();
        if (inverted){
            // inverted right
            in_shape.getPoints().addAll(
                    -15.0, 7.5,
                    0.0, 0.0,
                    0.0, 15.0
            );
            layout = new HBox(name, in_shape);
            input_layout.setAlignment(Pos.TOP_RIGHT);
        } else {
            // normal left
            in_shape.getPoints().addAll(
                    15.0, 7.5,
                    0.0, 0.0,
                    0.0, 15.0
            );
            layout = new HBox(in_shape, name);
            input_layout.setAlignment(Pos.TOP_LEFT);
        }
        input_layout.getChildren().clear();
        input_layout.getChildren().addAll(layout);
        return input_layout;
    }

    @SuppressWarnings("unchecked")
    private void addEvents(Group group){
        group.addEventHandler(MouseEvent.DRAG_DETECTED, e -> {
            Dragboard db = group.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(this.identifier);
            db.setContent(content);
            e.consume();
        });

        group.addEventHandler(MouseEvent.MOUSE_DRAGGED, Event::consume);

        group.addEventHandler(DragEvent.DRAG_OVER, e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        group.addEventHandler(DragEvent.DRAG_DROPPED, e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                ArrayList<String> from = (ArrayList<String>)new Gson().fromJson(this.identifier, ArrayList.class);;
                if (from != null){
                    System.out.println("Dropped: " + db.getString());
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("from", from);
                    msg.put("to", (ArrayList<String>)new Gson().fromJson(this.identifier, ArrayList.class));
                    msg.put("url", "/modules/connect");
                    publishEvent("Client",0, Events.CONNECT, msg);
                    success = true;
                }

            }
            e.setDropCompleted(success);
            e.consume();
        });
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
        output_layout.getChildren().clear();
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

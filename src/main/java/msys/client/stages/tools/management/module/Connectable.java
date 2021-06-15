package msys.client.stages.tools.management.module;

import com.google.gson.Gson;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    private Circle in_center = new Circle();
    private Rectangle out_shape = new Rectangle(15, 15);
    private Circle out_center = new Circle();
    private Label name = new Label("TEST");
    private HBox input_layout = new HBox();
    private HBox output_layout = new HBox();


    public Connectable(int handler_no, Map<String, Object> config) {
        super(Parser.extractIdentifier(config), handler_no,2);

        in_center.setFill(Color.RED);
        out_center.setFill(Color.RED);

        updateLayout(config);

        in_shape.setFill(Color.GREEN);
        in_shape.setStrokeWidth(1);
        in_shape.setStroke(Color.BLACK);
        // in_shape.setTranslateY(5);
        out_shape.setFill(Color.GREEN);
        out_shape.setStrokeWidth(1);
        out_shape.setStroke(Color.BLACK);

        addEvents(input_layout);
        addEvents(output_layout);
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
        StackPane stack = new StackPane();
        in_shape.getPoints().clear();
        stack.getChildren().addAll(in_center, in_shape);
        if (inverted){
            // inverted right
            in_shape.getPoints().addAll(
                    -15.0, 7.5,
                    0.0, 0.0,
                    0.0, 15.0
            );

            layout = new HBox(name, stack);
            input_layout.setAlignment(Pos.TOP_RIGHT);
        } else {
            // normal left
            in_shape.getPoints().addAll(
                    15.0, 7.5,
                    0.0, 0.0,
                    0.0, 15.0
            );
            layout = new HBox(stack, name);
            input_layout.setAlignment(Pos.TOP_LEFT);
        }
        input_layout.getChildren().clear();
        input_layout.getChildren().addAll(layout);
        return input_layout;
    }

    @SuppressWarnings("unchecked")
    private void addEvents(Node node){
        node.addEventHandler(MouseEvent.DRAG_DETECTED, e -> {
            Dragboard db = node.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(this.identifier);
            db.setContent(content);
            e.consume();
        });

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, Event::consume);

        node.addEventHandler(DragEvent.DRAG_OVER, e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        node.addEventHandler(DragEvent.DRAG_DROPPED, e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                ArrayList<String> from = (ArrayList<String>)new Gson().fromJson(this.identifier, ArrayList.class);;
                if (from != null){
                    System.out.println("Dropped: " + db.getString());
                    System.out.println("Onto: " + this.identifier);
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("from", (ArrayList<String>) new Gson().fromJson(db.getString(), ArrayList.class));
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
        StackPane stack = new StackPane();
        stack.getChildren().addAll(out_center, out_shape);
        if (inverted){
            layout= new HBox(out_shape, name);
        } else {
            layout= new HBox(name, stack);
        }
        output_layout.getChildren().clear();
        output_layout.getChildren().addAll(layout);
        return output_layout;
    }

    public Node asOutput(){
        return asOutput(false);
    }

    public Circle getInputCenter(){
        return in_center;
    }

    public Circle getOutputCenter(){
        return out_center;
    }


    @Override
    public Node getVisual() {
        return null;
    }
}

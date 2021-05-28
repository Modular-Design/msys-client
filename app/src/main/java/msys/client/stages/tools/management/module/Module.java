package msys.client.stages.tools.management.module;

import com.google.gson.Gson;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import msys.client.Client;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.VisualElement;

import java.util.ArrayList;
import java.util.Map;

public class Module extends VisualElement {
    public boolean extern = true;
    private Group intern_layout = new Group();
    private BorderPane extern_layout = new BorderPane();
    private Map<String , Object> config;
    private String name;
    private ArrayList<String> identifier;

    public Module(Map<String, Object> config) {
        super(0);
        updateLayout(config);
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, Integer level, Events event, Map<String, Object> msg) {

    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, Events event, Map<String, Object> msg) {

    }

    @SuppressWarnings("unchecked")
    private static Map<String , Object> extractMetaData(Map<String , Object> config){
        return (Map<String , Object>) config.get("metadata");
    }

    private static String extractName(Map<String , Object> config){
        Map<String , Object> meta = Module.extractMetaData(config);
        if (meta != null){
            String name = (String) meta.get("name");
            if (name != null && !name.equals("")){
                return name;
            }
        }
        return (String) config.get("id");
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<String> extractIdentifier(Map<String , Object> config){
        return (ArrayList<String>) config.get("identifier");
    }

    public String getName(){
        return name;
    }

    private void updateLayout(Map<String , Object> config){
        this.config = config;
        this.name = Module.extractName(this.config);
        this.identifier = Module.extractIdentifier(this.config);
        updateInternLayout();
        updateExternLayout();
    }

    private void updateInternLayout(){

        intern_layout = new Group();
    }

    private void updateExternLayout(){

        VBox inputs = new VBox(new Button("input"));
        inputs.setPrefWidth(150);
        //VBox.setVgrow(inputs, Priority.ALWAYS);

        Group modules = new Group(new Button("module"));


        VBox outputs = new VBox(new Button("output"));
        outputs.setPrefWidth(150);

        ScrollPane sp_inputs = new ScrollPane();
        sp_inputs.setContent(inputs);
        VBox.setVgrow(sp_inputs, Priority.ALWAYS);

        ScrollPane sp_modules = new ScrollPane();
        sp_modules.setContent(modules);
        VBox.setVgrow(sp_modules, Priority.ALWAYS);

        ScrollPane sp_outputs = new ScrollPane();
        sp_outputs.setContent(outputs);
        VBox.setVgrow(sp_outputs, Priority.ALWAYS);



        BorderPane split = new BorderPane();
        extern_layout.setLeft(new VBox(new HBox(new Label("inputs")),sp_inputs));
        extern_layout.setCenter(new VBox(sp_modules));
        extern_layout.setRight(new VBox(new HBox(new Label("outputs")),sp_outputs));


        extern_layout.addEventHandler(DragEvent.DRAG_OVER, e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            e.consume();
        });

        extern_layout.setOnDragDropped(new EventHandler<>() {
            @Override
            @SuppressWarnings("unchecked")
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    System.out.println("Dropped: " + db.getString());
                    Map<String, Object> msg = (Map<String, Object>) new Gson().fromJson(db.getString(), Map.class);
                    String path = new Gson().toJson(identifier);
                    msg.put("url", "/modules/"+path);
                    publishEvent(0,Events.ADD, msg);
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });



        //split.setCenter(sp_modules);
        //split.setLeft(outputs);
        VBox.setVgrow(sp_inputs, Priority.ALWAYS);
        //extern_layout.getChildren().add(new VBox(split));
    }

    public Node getInternLayout(){
        return intern_layout;
    }

    public Node getExternLayout(){
        return extern_layout;
    }

    @Override
    public Node getVisual() {
        if(extern){
            return extern_layout;
        }else{
            return intern_layout;
        }
    }
}

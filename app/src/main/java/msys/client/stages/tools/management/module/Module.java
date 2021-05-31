package msys.client.stages.tools.management.module;

import com.google.gson.Gson;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.VisualElement;

import java.util.ArrayList;
import java.util.Map;

public class Module extends VisualElement {
    public boolean extern = true;
    private BorderPane intern_layout = new BorderPane();
    private ArrayList<Option> options = new ArrayList<>();
    private ArrayList<Module> modules = new ArrayList<>();
    private ArrayList<Connectable> inputs = new ArrayList<>();
    private ArrayList<Connectable> outputs = new ArrayList<>();
    private Group extern_layout = new Group();
    private Map<String , Object> config;
    private String name;
    private String identifier;

    public Module(Map<String, Object> config) {
        super(0, 3);
        updateLayout(config);
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {
        if (receiver != null){
            if (receiver.equals(identifier)){
                processGUIEvent(sender, event, msg);
            }
        }
    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {
        System.out.println("[Module]: "+msg);
        if (event.equals("add")){
            modules.add(new Module(msg));
        }
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
    private static String extractIdentifier(Map<String , Object> config){
        return new Gson().toJson(config.get("identifier"));
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Option> extractOptions(Map<String , Object> config){
        System.out.println("[Module] extract: "+ config.toString());
        ArrayList<Option> result = new ArrayList<>();
        ArrayList<Map<String, Object>> options = (ArrayList<Map<String, Object>>)config.get("options");
        if (options != null){
            for (Map<String, Object> option : options){
                result.add(new Option(option));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Connectable> extractInputs(Map<String , Object> config){
        System.out.println("[Module] extract: "+ config.toString());
        ArrayList<Connectable> result = new ArrayList<>();
        ArrayList<Map<String, Object>> inputs = (ArrayList<Map<String, Object>>)config.get("inputs");
        if (inputs != null){
            for (Map<String, Object> input : inputs){
                result.add(new Connectable(input));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Connectable> extractOutputs(Map<String , Object> config){
        System.out.println("[Module] extract: "+ config.toString());
        ArrayList<Connectable> result = new ArrayList<>();
        ArrayList<Map<String, Object>> outputs = (ArrayList<Map<String, Object>>)config.get("outputs");
        if (outputs != null){
            for (Map<String, Object> output : outputs){
                result.add(new Connectable(output));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Module> extractModules(Map<String , Object> config){
        System.out.println("[Module] extract: "+ config.toString());
        ArrayList<Module> result = new ArrayList<>();
        ArrayList<Map<String, Object>> modules = (ArrayList<Map<String, Object>>)config.get("modules");
        if (modules != null){
            for (Map<String, Object> module : modules){
                result.add(new Module(module));
            }
        }
        return result;
    }

    public String getName(){
        return name;
    }

    private void updateLayout(Map<String , Object> config){
        this.config = config;
        this.name = Module.extractName(this.config);
        this.identifier = Module.extractIdentifier(this.config);
        this.modules = Module.extractModules(this.config);
        this.options = Module.extractOptions(this.config);
        this.inputs = Module.extractInputs(this.config);
        this.outputs = Module.extractOutputs(this.config);

        System.out.println("[Module]: " + this.identifier);
        updateInternLayout();
        updateExternLayout();
    }

    private void updateInternLayout(){
        VBox inputs = new VBox();
        inputs.setPrefWidth(150);
        if (this.inputs != null){
            System.out.println("[Module]: Inputs");
            for (Connectable input: this.inputs){
                inputs.getChildren().add(input.asOutput());
            }
        }
        //VBox.setVgrow(inputs, Priority.ALWAYS);

        Group modules = new Group();
        if (this.modules != null){
            for (Module module: this.modules){
                modules.getChildren().add(module.getExternLayout());
            }
        }


        VBox outputs = new VBox(new Button("output"));
        outputs.setPrefWidth(150);
        if (this.outputs != null){
            for (Connectable output: this.outputs){
                outputs.getChildren().add(output.asInput());
            }
        }

        ScrollPane sp_inputs = new ScrollPane();
        BorderPane bp_inputs = new BorderPane();
        //bp_inputs.setRight(inputs);
        inputs.setAlignment(Pos.TOP_LEFT);
        sp_inputs.setContent(inputs);
        VBox.setVgrow(sp_inputs, Priority.ALWAYS);

        ScrollPane sp_modules = new ScrollPane();
        sp_modules.setContent(modules);
        VBox.setVgrow(sp_modules, Priority.ALWAYS);

        ScrollPane sp_outputs = new ScrollPane();
        BorderPane bp_outputs = new BorderPane();
        bp_outputs.setLeft(outputs);
        sp_outputs.setContent(bp_outputs);
        VBox.setVgrow(sp_outputs, Priority.ALWAYS);



        BorderPane split = new BorderPane();
        intern_layout.setLeft(new VBox(new HBox(new Label("inputs")),sp_inputs));
        intern_layout.setCenter(new VBox(sp_modules));
        intern_layout.setRight(new VBox(new HBox(new Label("outputs")),sp_outputs));


        intern_layout.addEventHandler(DragEvent.DRAG_OVER, e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            e.consume();
        });

        intern_layout.setOnDragDropped(new EventHandler<>() {
            @Override
            @SuppressWarnings("unchecked")
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    Map<String, Object> msg = (Map<String, Object>) new Gson().fromJson(db.getString(), Map.class);
                    msg.put("url", "/modules/"+identifier);
                    publishEvent("Client", 0,Events.ADD, msg);
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

    private void updateExternLayout(){

        //Borderpane
        StackPane header  = new StackPane();
        Rectangle header_bg = new Rectangle();
        Label title = new Label(this.name);
        header.getChildren().addAll(header_bg, title);

        header_bg.setFill(Color.GRAY);
        header_bg.widthProperty().bind(header.widthProperty());
        header_bg.heightProperty().bind(header.heightProperty());

        VBox options = new VBox();
        for (Option opt : this.options){
            options.getChildren().add(opt.getVisual());
        }
        VBox top_layout = new VBox(header, options);

        BorderPane main_placement = new BorderPane();
        main_placement.setTop(top_layout);

        Rectangle main_background = new Rectangle();

        StackPane main = new StackPane();
        main.getChildren().add(main_background);
        main.getChildren().add(main_placement);// on Top


        extern_layout = new Group();
        extern_layout.getChildren().addAll(main);
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

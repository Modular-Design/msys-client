package msys.client.stages.tools.management.module;

import com.google.gson.Gson;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.VisualElement;

import java.util.ArrayList;
import java.util.HashMap;
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
    private boolean selected = false;
    private Point2D position = new Point2D(0,0);
    private Metadata metadata;

    public Module(Map<String, Object> config) {
        super(0, 3);
        updateLayout(config);
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {
        if (receiver != null){
            if (receiver.equals(identifier)){
                System.out.println("[Module]: categorizeGUIEvent ");
                processGUIEvent(sender, event, msg);
            }
            else{
                if(event.equals("select")){
                    selected = false;
                    updateExternLayout();
                }
            }
        }
    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {
        System.out.println("[Module]: processGUIEvent: "+event +"msg: "+  msg);
        if (event.equals("add")){
            modules.add(new Module(msg));
            updateInternLayout();
        }
        if (event.equals("status")){
            updateLayout(msg);
        }
    }



    public String getName(){
        return name;
    }

    private void updateLayout(Map<String , Object> config){
        this.config = config;
        this.metadata = Parser.extractMetaData(this.config);
        this.name = Parser.extractName(this.config);
        this.identifier = Parser.extractIdentifier(this.config);
        this.modules = Parser.extractModules(this.config);
        this.options = Parser.extractOptions(this.config);
        this.inputs = Parser.extractInputs(this.config);
        this.outputs = Parser.extractOutputs(this.config);


        System.out.println("[Module]: updateLayout: " + this.identifier);
        updateInternLayout();
        updateExternLayout();
    }

    private void updateInternLayout(){
        VBox inputs = new VBox();
        inputs.setPrefWidth(150);
        if (this.inputs != null){
            System.out.println("[Module]: updateInternLayout: ");
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


        VBox inputs = new VBox();
        inputs.setPrefWidth(150);
        if (this.inputs != null){
            System.out.println("[Module]: updateInternLayout: ");
            for (Connectable input: this.inputs){
                inputs.getChildren().add(input.asInput(this.metadata.inverted));
            }
        }

        VBox outputs = new VBox();
        if (this.outputs != null){
            System.out.println("[Module]: updateInternLayout: ");
            for (Connectable output: this.outputs){
                outputs.getChildren().add(output.asOutput(this.metadata.inverted));
            }
        }

        BorderPane bottom_placement = new BorderPane();
        if(this.metadata.inverted){
            bottom_placement.setRight(inputs);
            bottom_placement.setLeft(outputs);
        }else{
            bottom_placement.setLeft(inputs);
            bottom_placement.setRight(outputs);
        }


        VBox main_placement = new VBox(top_layout, bottom_placement);
        AnchorPane main_layout = new AnchorPane(main_placement);
        double anchor = 0;
        if (selected){
            anchor = 2;
        }
        AnchorPane.setTopAnchor(main_placement, anchor);
        AnchorPane.setLeftAnchor(main_placement, anchor);
        AnchorPane.setRightAnchor(main_placement, anchor);
        AnchorPane.setBottomAnchor(main_placement, anchor);

        Rectangle main_background = new Rectangle();
        main_background.setFill(Color.LIGHTGREY);
        if(selected){
            main_background.setStrokeWidth(2);
            main_background.setStroke(Color.YELLOW);

            main_background.widthProperty().bind(main_layout.widthProperty().subtract(2));
            main_background.heightProperty().bind(main_layout.heightProperty().subtract(2));
        }else{
            main_background.widthProperty().bind(main_layout.widthProperty());
            main_background.heightProperty().bind(main_layout.heightProperty());
        }

        StackPane main = new StackPane();
        main.getChildren().add(main_background);
        main.getChildren().add(main_layout);// on Top

        extern_layout.getChildren().clear();
        extern_layout.getChildren().addAll(main);

        extern_layout.setTranslateX(metadata.pos.get("x"));
        extern_layout.setTranslateY(metadata.pos.get("y"));

        ContextMenu contextMenu = new ContextMenu();

        MenuItem menu_update = new MenuItem("_Update");
        MenuItem menu_inverted = new MenuItem("_Invert");
        contextMenu.getItems().addAll(menu_update, menu_inverted);

        menu_inverted.setOnAction((event) -> {
            this.metadata.inverted = !this.metadata.inverted;
            Map<String, Object> msg = Parser.updateMetadata(this.config, this.metadata);
            msg.put("url", "/modules/"+identifier);
            publishEvent("CLIENT",0, Events.CHANGE, msg);
            updateExternLayout();
        });


        main.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                selected = true;
                publishEvent(identifier, 1, "select", null);
                this.position = new Point2D(e.getSceneX(), e.getSceneY());
                e.consume();
                updateExternLayout();
            }
            if(e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(main, e.getScreenX(), e.getScreenY());
                e.consume();
            }
        });


        main.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            // System.out.println("[Module]: dragg event detected!");
            if(e.getButton() == MouseButton.PRIMARY) {
                if (selected) {
                    double x = extern_layout.getTranslateX() + (e.getSceneX() - this.position.getX());
                    double y = extern_layout.getTranslateY() + (e.getSceneY() - this.position.getY());
                    extern_layout.setTranslateX(x);
                    extern_layout.setTranslateY(y);
                    this.position = new Point2D(e.getSceneX(), e.getSceneY());
                }
            }
        });


        main.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            // System.out.println("[Module]: dragg event detected!");
            if(e.getButton() == MouseButton.PRIMARY) {
                if (selected){
                    this.metadata.pos.put("x", extern_layout.getTranslateX());
                    this.metadata.pos.put("y", extern_layout.getTranslateY());
                    Map<String, Object> msg = Parser.updateMetadata(this.config, this.metadata);
                    msg.put("url", "/modules/"+identifier);
                    publishEvent("CLIENT",0, Events.CHANGE, msg);
                }
            }
        });


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

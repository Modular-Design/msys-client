package msys.client.stages.tools.management.module;

import com.google.gson.Gson;
import javafx.event.EventHandler;
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
    private ArrayList<Connection> connections = new ArrayList<>();
    private Group extern_layout = new Group();
    private Map<String , Object> config = new HashMap<>();
    private String name;
    private String identifier;
    private boolean selected = false;
    private Point2D position = new Point2D(0,0);
    private Metadata metadata;
    private String parentID;

    public Module(int handler_no, Map<String, Object> config) {
        super(Parser.extractIdentifier(config), handler_no, 3);
        updateLayout(config);
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {
        if (receiver != null){
            if(event.equals(Events.SELECT)){
                if (!receiver.equals(identifier)){
                    selected = false;
                    updateExternLayout();
                }
                return;
            }
            if (receiver.equals(getID())){
                processGUIEvent(sender, event, msg);
            }
        }
    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {
        System.out.println("[Module]: processGUIEvent: name: "+ name +", event: "+event +", msg: "+  msg);
        if (event.equals(Events.ADD)){
            //Module nmodule = new Module(msg);
            if (!Parser.isValid(msg)){
                return;
            }
            Module module = new Module(getHandlerNumber(), msg);
            modules.add(module);
            module.changePosition(position.getX(), position.getY());
            updateInternLayout();
        }
        if (event.equals(Events.STATUS)){
            updateLayout(msg);
        }
        if (event.equals(Events.CHANGE)){
            if (!Parser.isValid(msg)){
                return;
            }
            updateLayout(msg);
        }
        if (event.equals(Events.RELOAD)){
            System.out.println("[Module] reload");
            this.connections = Parser.extractConnections(getHandlerNumber(), this.config);
            updateInternLayout();
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

        int last = this.identifier.lastIndexOf(",");
        if (last == -1){
            this.parentID = null;
        }else {
            this.parentID = this.identifier.substring(0, last) + "]";
        }

        this.modules = Parser.extractModules(getHandlerNumber(), this.config);
        this.options = Parser.extractOptions(getHandlerNumber(), this.config);
        this.inputs = Parser.extractInputs(getHandlerNumber(), this.config);
        this.outputs = Parser.extractOutputs(getHandlerNumber(), this.config);
        this.connections = Parser.extractConnections(getHandlerNumber(), this.config);


        System.out.println("[Module]: updateLayout: " + this.name);
        updateInternLayout();
        updateExternLayout();
    }

    private void updateInternLayout(){
        VBox inputs = new VBox();
        inputs.setPrefWidth(150);
        if (this.inputs != null){
            for (Connectable input: this.inputs){
                inputs.getChildren().add(input.asOutput());
            }
        }
        //VBox.setVgrow(inputs, Priority.ALWAYS);

        Group module_connectiion = new Group();
        module_connectiion.getChildren().clear();

        if (this.connections != null){
            for (Connection connection: this.connections){
                module_connectiion.getChildren().add(connection.getVisual());
            }
        }

        if (this.modules != null){
            for (Module module: this.modules){
                module_connectiion.getChildren().add(module.getExternLayout());
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
        inputs.setAlignment(Pos.TOP_LEFT);
        sp_inputs.setContent(inputs);
        VBox.setVgrow(sp_inputs, Priority.ALWAYS);


        ScrollPane sp_modules = new ScrollPane();
        AnchorPane modules_border = new AnchorPane(module_connectiion);
        AnchorPane.setTopAnchor(module_connectiion, 10.0);
        AnchorPane.setRightAnchor(module_connectiion, 10.0);
        AnchorPane.setBottomAnchor(module_connectiion, 10.0);
        AnchorPane.setLeftAnchor(module_connectiion, 10.0);
        sp_modules.setContent(modules_border);
        VBox.setVgrow(sp_modules, Priority.ALWAYS);

        ScrollPane sp_outputs = new ScrollPane();
        BorderPane bp_outputs = new BorderPane();
        bp_outputs.setLeft(outputs);
        sp_outputs.setContent(bp_outputs);
        VBox.setVgrow(sp_outputs, Priority.ALWAYS);

        intern_layout.getChildren().clear();
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
                    //position = new Point2D(event.getSceneX() - modules_border.getLayoutX(), event.getSceneY()-modules_border.getLayoutY());
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });



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
            for (Connectable input: this.inputs){
                inputs.getChildren().add(input.asInput(this.metadata.inverted));
            }
        }

        VBox outputs = new VBox();
        if (this.outputs != null){
            for (Connectable output: this.outputs){
                outputs.getChildren().add(output.asOutput(this.metadata.inverted));
            }
        }

        BorderPane bottom_placement = new BorderPane();
        if (this.metadata == null){
            System.out.println("[Module]: Metadata is null in:" + this.identifier);
        }

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
        MenuItem menu_delete = new MenuItem("_Delete");
        contextMenu.getItems().addAll(menu_update, menu_inverted, menu_delete);

        menu_update.setOnAction((event) -> {
            Map<String, Object> msg = new HashMap<>();
            msg.put("url", "/modules/update/"+identifier);
            publishEvent("CLIENT",0, Events.CHANGE, msg);
            updateExternLayout();
        });

        menu_inverted.setOnAction((event) -> {
            this.metadata.inverted = !this.metadata.inverted;
            Map<String, Object> msg = Parser.updateMetadata(this.config, this.metadata);
            msg.put("url", "/modules/"+identifier);
            publishEvent("CLIENT",0, Events.CHANGE, msg);
            updateExternLayout();
        });

        menu_delete.setOnAction((event) -> {
            Map<String, Object> msg = new HashMap<>();
            msg.put("url", "/modules/delete/"+identifier);
            publishEvent("CLIENT",0, Events.DELETE, msg);
            updateExternLayout();
        });


        main.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
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
            System.out.println("[Module]: dragg event detected!");
            if(e.getButton() == MouseButton.PRIMARY) {
                if (selected){
                    changePosition(extern_layout.getTranslateX(), extern_layout.getTranslateY());
                }
            }
        });

        publishEvent(parentID, 3, Events.RELOAD, null);

    }

    public void changePosition(double x, double y){
        this.metadata.pos.put("x", x);
        this.metadata.pos.put("y", y);
        Map<String, Object> msg = Parser.updateMetadata(this.config, this.metadata);
        msg.put("url", "/modules/"+identifier);
        publishEvent("CLIENT",0, Events.CHANGE, msg);
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

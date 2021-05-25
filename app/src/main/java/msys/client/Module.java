package msys.client;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import mdd.client.connectable.Connectable;
import mdd.client.connectable.Input;
import mdd.client.connectable.Output;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;

import java.util.Vector;


public class Module extends Group implements IGUIEventClient, ChangeListener<Bounds> {
    private boolean _dragged = false;
    private Point2D _pos = new Point2D(0.0f,0.0f);
    private Rectangle titlebar;
    private Rectangle background;
    private Label caption;
    protected GUIEventHandler _eventHandler;

    public String Name = "TEST";
    public long Appendix = 0;
    public String ID = "TEST-1";
    public Vector<String> Prefix;

    protected JSONObject jID = new JSONObject();

    protected String Type = "module";
    protected String Key = "";

    public VBox vlayout = new VBox();
    GridPane configs = new GridPane();
    public BorderPane hlayout = new BorderPane();
    public VBox  inputs = new VBox();
    public VBox outputs = new VBox();
    public boolean isSelected = false;

    protected boolean moveable = true;
    protected int _index;

    public Module(JSONObject json, int handler_index){
        _index = handler_index;
        setFocusTraversable(true);
        _eventHandler = GUIEventHandler.getEventHandler(_index);
        _eventHandler.addEventListener(this, 5);

        inputs.setSpacing(10);
        inputs.setAlignment(Pos.TOP_LEFT);
        outputs.setSpacing(10);

        outputs.setAlignment(Pos.TOP_RIGHT);

        hlayout.setLeft(inputs);
        hlayout.setRight(outputs);

        inputs.setTranslateX(-13);
        outputs.setTranslateX(13);
        vlayout.getChildren().addAll(configs, hlayout);
        vlayout.setTranslateX(5);
        vlayout.setTranslateY(30);

        titlebar = new Rectangle(0.0f, 0.0f, 1.0, 20f);
        titlebar.setArcWidth(10.0f);
        titlebar.setArcHeight(10.0f);

        //background = new Rectangle(0.0f, 0.0f, 150-20, 150 + 40);
        background = new Rectangle(0.0f, 0.0f, 1.0, 1.0);
        background.setArcWidth(10.0f);
        background.setArcHeight(10.0f);
        background.setFill(Color.GRAY);
        caption = new Label();
        caption.setText(Name);
        caption.setFont(new Font("Arial", 15));
        caption.getStyleClass().add("text");
        caption.setTranslateX(10);

        Color color = Color.DARKCYAN;
        titlebar.setFill(color);
        configure(json);

        getChildren().clear();
        getChildren().addAll( background, titlebar, caption, vlayout);

        addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                isSelected = true;
                System.out.println("[Module]: select: " + ID);
                _eventHandler.publishEvent("MOUSE_PRESSED", getJSONIdentifier());
                _pos = new Point2D(e.getSceneX(), e.getSceneY());
            }
            if(e.getButton() == MouseButton.SECONDARY) {

            }
            e.consume();
        });


        addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            // System.out.println("[Module]: dragg event detected!");
            if(e.getButton() == MouseButton.PRIMARY) {
                if (moveable) {
                    if (isSelected) {
                        setTranslateX(getTranslateX() + (e.getSceneX() - _pos.getX()));
                        setTranslateY(getTranslateY() + (e.getSceneY() - _pos.getY()));
                        _pos = new Point2D(e.getSceneX(), e.getSceneY());
                    }
                }
            }
            if(e.getButton() == MouseButton.SECONDARY) {

            }
        });


        addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            // System.out.println("[Module]: dragg event detected!");
            if(e.getButton() == MouseButton.PRIMARY) {
                if (isSelected && moveable){
                    JSONObject jgui_args = new JSONObject();
                    jgui_args.put("posX", getTranslateX());
                    jgui_args.put("posY", getTranslateY());
                    JSONObject jgui = new JSONObject();
                    jgui.put("GUI", jgui_args);
                    JSONObject jmsg = new JSONObject();
                    jmsg.put("operation", "change");
                    JSONObject jargs = new JSONObject();
                    jargs.put("subject",  getJSONIdentifier());
                    jargs.put("object",   jgui);
                    jmsg.put("args", jargs);
                    _eventHandler.publishEvent(10, "try", jmsg);
                }
            }
            if(e.getButton() == MouseButton.SECONDARY) {

            }
        });
        vlayout.layoutBoundsProperty().addListener(this);
    }

    public void configure(JSONObject json){
        if (json.containsKey("type")){
            Type = json.get("type").toString();
        }

        if (json.containsKey("ID")){
            jID = (JSONObject)json.get("ID");

            Key = jID.get("key").toString();
            Appendix = (long)jID.get("appendix");
            ID = Key +Appendix;
            Prefix = new Vector<>();
            JSONArray jarr = (JSONArray)jID.get("prefix");
            for (Object o : jarr) {
                Prefix.add(o.toString());
            }
        }

        if (json.containsKey("inputs")){
            updateConnectable(ConnectableType.INPUT,json);
        }
        if (json.containsKey("outputs")){
            updateConnectable(ConnectableType.OUTPUT,json);
        }

        if (json.containsKey("configure")) {
            configs.getChildren().clear();
            configs.setHgap(20);
            configs.setVgap(20);

            JSONObject  jconfigs= (JSONObject)json.get("configure");

            Iterator<String> itr = jconfigs.keySet().iterator();
            int counter = 0;
            while (itr.hasNext()){
                String key = itr.next();
                JSONObject jconfig = (JSONObject) jconfigs.get(key);
                Label clabel = new Label(key);
                configs.add(clabel,0,counter);
                configs.add(new Configuration("configure", this, key, jconfig, _index),1,counter);
                ++counter;
            }
        }


        if (json.containsKey("GUI")) {
            JSONObject jgui = (JSONObject) json.get("GUI");
            if (jgui.containsKey("name")) {
                try {
                    Name = ((JSONObject)jgui.get("name")).get("value").toString();
                }catch(ClassCastException e){
                    Name= jgui.get("name").toString();
                }
                caption.setText(Name);
            }
            caption.setText(Name);
            if (jgui.containsKey("color")) {
                titlebar.setFill(Color.web(((JSONObject)jgui.get("color")).get("value").toString()));
            }
            if (moveable) {
                if (jgui.containsKey("posX")) {
                    setTranslateX((double) jgui.get("posX"));
                }
                if (jgui.containsKey("posY")) {
                    setTranslateY((double) jgui.get("posY"));
                }
            }
        }
    }

    protected void updateConnectable(ConnectableType ctype,JSONObject json){
        JSONArray jarray = new JSONArray();
        ObservableList<Node> connectables;
        if (ctype == ConnectableType.INPUT){
            connectables = inputs.getChildren();
            if (json.containsKey("inputs")){
                jarray = (JSONArray)json.get("inputs");
            }
        }else{
            connectables = outputs.getChildren();
            if (json.containsKey("outputs")){
                jarray = (JSONArray)json.get("outputs");
            }
        }
        int jsize = 0;
        if(jarray != null){
            jsize = jarray.size();
        }
        //remove
        int size = connectables.size() - jsize;
        for (int i = 0; i < size; ++i){
            connectables.remove(connectables.size()-1);
        }
        //change
        size = connectables.size();
        for (int i = 0; i < size; ++i){
            ((Connectable)connectables.get(i)).configure((JSONObject)jarray.get(i));
        }
        //add
        size = jsize-connectables.size();
        for (int i = 0; i < size; ++i){
            JSONObject jobj = (JSONObject)jarray.get(i);
            if (ctype == ConnectableType.INPUT){
                connectables.add(new Input(this,jobj, _index));
            }
            else{
                connectables.add(new Output(this,jobj, _index));
            }
        }
    }

    public Input getInput(String id){
        for (Node node : inputs.getChildren()){
            if(node instanceof Input){
                Input in = (Input) node;
                if (in.ID.equals(id)){
                    return in;
                }
            }

        }
        System.out.println("[Module]: getInput: WRONG ID!");
        return null;
    }

    public Output getOutput(String id){
        for (Node node : outputs.getChildren()){
            if(node instanceof Output){
                Output out = (Output) node;
                if (out.ID.equals(id)){
                    return out;
                }
            }

        }
        System.out.println("[Module]: getOutput: WRONG ID!");
        return null;
    }



    public JSONObject getJSONIdentifier(){
        return jID;
    }

    public Vector<String> getAsPrefix(){
        Vector<String> ret = (Vector<String>)Prefix.clone();
        ret.add(ID);
        return ret;
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
        if (isSelected){
            if (event.equals("KeyTyped")){
                if (!args.containsKey("key")){return;}
                String key = args.get("key").toString();
                if (key.equals("DELETE")){
                    _eventHandler.publishEvent(5,"delete", getJSONIdentifier());
                    System.out.println("[Module]: delete: " + getJSONIdentifier().toString());
                }
                return;
            }
        }else
        if (event == "MOUSE_PRESSED"){
            if (!args.equals(getJSONIdentifier())){
                isSelected = false;
                return;
            }
            return;
        }else
        if (event.equals("change")){
            if (!args.containsKey("args")){
                return;
            }
            JSONObject jargs = (JSONObject) args.get("args");
            if (!jargs.containsKey("subject")){
                return;
            }
            JSONObject jsub = (JSONObject) jargs.get("subject");
            if (!jsub.equals(getJSONIdentifier())){
                return;
            }
            if (!args.containsKey("operation")){
                return;
            }
            JSONObject jobj = (JSONObject) jargs.get("object");
            switch((String) args.get("operation")){
                case "change":
                    configure(jobj);
            }
            return;
        }
    }

    public int getHandlerIndex(){
        return _index;
    }

    @Override
    public void close() throws Exception {
        _eventHandler.removeEventListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
        double width = caption.getWidth();
        if (newValue.getWidth() > width){
            width =newValue.getWidth();
            width += 10;
            outputs.setTranslateX(13);
        }else{
            width += 10;
            outputs.setTranslateX(13+(width - vlayout.getLayoutBounds().getWidth()));
            width +=10;
        }
        titlebar.setWidth(width);
        titlebar.setHeight(20.0f);
        background.setWidth(width);
        background.setHeight(newValue.getHeight()+vlayout.getTranslateY()+10);
    }
}

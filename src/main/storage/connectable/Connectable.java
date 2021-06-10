package msys.client.connectable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import mdd.client.ConnectableType;
import mdd.client.GUIEventHandler;
import mdd.client.IGUIEventClient;
import mdd.client.Module;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Vector;
import java.util.function.DoubleUnaryOperator;

public class Connectable extends GridPane implements IGUIEventClient,ChangeListener<Number>{
    protected GUIEventHandler _eventHandler;
    private Circle _border;
    private Circle _circle;
    protected boolean _connected = false;
    protected int _connections = 0;

    public boolean optimizable = false;
    private Module _parent;
    private ConnectableType _type;

    protected StackPane _connection;
    protected Label _label = new Label();
    public String Name = "";
    public String Key = "";
    public String ID = "";
    public long Appendix = 0;
    public Vector<Double> Value = new Vector<>();

    protected JSONObject jID = new JSONObject();

    JSONObject jconfig = new JSONObject();
    JSONObject jlimit = new JSONObject();

    public DoubleProperty x = new SimpleDoubleProperty();
    public DoubleProperty y = new SimpleDoubleProperty();

    protected int _index;

    public Connectable(Module parent, ConnectableType type, JSONObject json, int handler_index){
        _index = handler_index;
        _eventHandler = GUIEventHandler.getEventHandler(_index);
        _eventHandler.addEventListener(this);
        _parent = parent;
        _type = type;

        setHgap(5);
        setVgap(5);

        configure(json);

        _border = new Circle(0,0, 7);
        _border.setFill(Color.BLACK);
        _circle = new Circle(0,0,5);
        _circle.setFill(Color.WHITE);

        _connection = new StackPane(_border,_circle);

        _connection.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                if (!_connected) {
                    optimizable = !optimizable;
                    JSONObject jvalue = new JSONObject();
                    jvalue.put("optimizable", optimizable);
                    JSONObject jmsg = new JSONObject();
                    jmsg.put("operation", "change");
                    JSONObject jargs = new JSONObject();
                    jargs.put("subject",  getJSONIdentifier());
                    jargs.put("object",   jvalue);
                    jmsg.put("args", jargs);
                    _eventHandler.publishEvent("try", jmsg);
                    updateColor();
                }
            }

        });

        layoutXProperty().addListener(this);
        layoutYProperty().addListener(this);

        _parent.translateXProperty().addListener(this);
        _parent.translateYProperty().addListener(this);

        _connection.layoutXProperty().addListener(this);

        _parent.hlayout.translateXProperty().addListener(this);
        _parent.hlayout.translateYProperty().addListener(this);

        _parent.hlayout.layoutXProperty().addListener(this);
        _parent.hlayout.layoutYProperty().addListener(this);

        _parent.vlayout.translateXProperty().addListener(this);
        _parent.vlayout.translateYProperty().addListener(this);

        _parent.vlayout.layoutXProperty().addListener(this);
        _parent.vlayout.layoutYProperty().addListener(this);

        if (_type == ConnectableType.INPUT){
            _parent.inputs.layoutXProperty().addListener(this);
            _parent.inputs.layoutYProperty().addListener(this);
        }
        else{
            _parent.outputs.layoutXProperty().addListener(this);
            _parent.outputs.layoutYProperty().addListener(this);
        }



        _connection.addEventHandler(MouseEvent.DRAG_DETECTED, e -> {
            Dragboard db = _connection.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            JSONObject jmsg = generateJSON();
            content.putString(jmsg.toString());
            System.out.println("[Connectable]: started drag with id = " + jmsg.toString());
            db.setContent(content);
            _eventHandler.publishEvent("DRAG_LINE", jmsg);
            e.consume();
        });

        _connection.addEventHandler(MouseEvent.MOUSE_DRAGGED, Event::consume);

        _connection.addEventHandler(DragEvent.DRAG_OVER, e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        _connection.addEventHandler(DragEvent.DRAG_DROPPED, e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                JSONParser parser = new JSONParser();
                JSONObject jmsg = null;
                try {
                    jmsg = (JSONObject)parser.parse(db.getString());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                JSONArray jsonList = new JSONArray();
                jsonList.add(jmsg);
                jsonList.add(generateJSON());

                ClipboardContent content = new ClipboardContent();
                content.putString(jsonList.toString());
                db.setContent(content);

                System.out.println("Dropped: " + db.getString());
                success = true;
            }
            //e.setDropCompleted(success);
            //e.consume();
        });

        _connection.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                _eventHandler.publishEvent("MOUSE_PRESSED", generateJSON());
            }
            if(e.getButton() == MouseButton.SECONDARY) {

            }
            e.consume();
        });

        updateColor();
    }

    public void configure(JSONObject json){
        if (json.containsKey("configure")){
            jconfig = (JSONObject) json.get("configure");
            if (jconfig.containsKey("value")){
                Value.clear();
                for (Object o: (JSONArray)jconfig.get("value")) {
                    Value.add((Double)o);
                }
            }
            if (jconfig.containsKey("name")){
                Name = (String) jconfig.get("name");
            }
            if (jconfig.containsKey("optimizable")){
                JSONObject jopt =  (JSONObject)jconfig.get("optimizable");
                String state = jopt.get("value").toString();
                if (state.equals("true")){
                    optimizable = true;
                }else{
                    optimizable = false;
                }
                if (_circle != null){
                    updateColor();
                }
            }
        }

        if (json.containsKey("ID")){
            jID = (JSONObject)json.get("ID");
            if (json.containsKey("key")){
                Key = (String) json.get("key");
            }
            if (jID.containsKey("appendix")){
                Appendix = (long) jID.get("appendix");
            }
        }

        if (json.containsKey("limit")) {
            jlimit = (JSONObject) json.get("limit");
        }else{
            jlimit = null;
        }


        ID = Key + Appendix;
        _label.setText(Name);
    }

    protected void updateColor(){
        if (_connected){
            _circle.setFill(Color.YELLOW);
        } else{
            if (optimizable) {
                _circle.setFill(Color.GREEN);
            } else {
                _circle.setFill(Color.WHITE);
            }
        }
    }

    private JSONObject generateJSON(){
        return jID;
    }

    public JSONObject getJSONIdentifier(){
        return jID;
    }

    public boolean isConnected(){
       return _connected;
    }

    public void setConnected(boolean state){
        if (state){
            ++_connections;
            if (_type == ConnectableType.INPUT && _connections > 1){
                _connections = 1;
            }
        }
        else {
            --_connections;
            if (_connections < 0){
                _connections = 0;
            }
            if (_connections > 0){
                state = true;
            }
        }
        if (state != _connected){
            _connected = state;
            updateColor();
        }
    }

    public StackPane getConnector(){
        return _connection;
    }

    public Label getLabel(){
        return _label;
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        double dx, dy;
        if (_type == ConnectableType.INPUT) {
            dx = _parent.inputs.getTranslateX() + _parent.inputs.getLayoutX();
            dy = _parent.inputs.getLayoutY();
        }
        else{
             dx =  _parent.outputs.getTranslateX() + _parent.outputs.getLayoutX();
             dy =   _parent.outputs.getLayoutY();
        }
        Bounds con_bounds = _connection.getLayoutBounds();
        x.setValue(dx + _parent.getTranslateX()+ _parent.hlayout.getTranslateX() +  _parent.hlayout.getLayoutX() + _parent.vlayout.getTranslateX() + _parent.vlayout.getLayoutX() + _connection.getLayoutX() + con_bounds.getWidth()/2  + getLayoutX());
        y.setValue(dy + _parent.getTranslateY()+ _parent.hlayout.getTranslateY() +  _parent.hlayout.getLayoutY()+  _parent.vlayout.getTranslateY() + _parent.vlayout.getLayoutY() + _connection.getLayoutY() + con_bounds.getHeight()/2 + getLayoutY());
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
        if (event.equals("change")){
            if (!args.containsKey("operation")){return;}
            if (!args.containsKey("args")){return;}
            JSONObject jargs = (JSONObject) args.get("args");
            if (!jargs.containsKey("subject")){return;}
            JSONObject jsub = (JSONObject)jargs.get("subject");
            if (!jsub.equals(getJSONIdentifier())){
                return;
            }
            configure(jsub);
        }else
        if (event == "GET_PROPERTY"){
            if (args.equals(getJSONIdentifier())){
                JSONObject jmsg = new JSONObject();
                jmsg.put("operation","state");
                JSONObject jinfo = new JSONObject();
                jinfo.put("configure", jconfig);
                if (jlimit != null){
                    jinfo.put("limit", jlimit);
                }

                JSONObject jargs = new JSONObject();
                jargs.put("info",jinfo);
                jmsg.put("args",jargs);
                _eventHandler.publishEvent("receive", jmsg);
                return;
            }
            return;
        }
    }

    @Override
    public void close() throws Exception {
        _eventHandler.removeEventListener(this);
    }
}

package msys.client.connectable;

import javafx.scene.shape.Line;
import mdd.client.GUIEventHandler;
import mdd.client.IGUIEventClient;
import mdd.client.connectable.Input;
import mdd.client.connectable.Output;
import org.json.simple.JSONObject;


public class Connection extends Line implements IGUIEventClient {
    private GUIEventHandler _eventHandler;
    public Input in;
    public Output out;

    public Connection(){
        super();
    }

    public Connection(Input in, Output out, int handler_index){
        super(0,0,100,100);
        _eventHandler = GUIEventHandler.getEventHandler(handler_index);
        _eventHandler.addEventListener(this);
        this.in = in;
        this.out = out;
        System.out.println("Connection: "+ in.x.toString());
        this.startXProperty().bind(in.x);
        this.startYProperty().bind(in.y);

        this.endXProperty().bind(out.x);
        this.endYProperty().bind(out.y);

        in.setConnected(true);
        out.setConnected(true);
        JSONObject json = new JSONObject();
        json.put("con", this.toString());
        json.put("in", in.toString());
        json.put("out", out.toString());
        _eventHandler.publishEvent("CONNECTED", json);
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
    }

    @Override
    public void close() throws Exception {
        in.setConnected(false);
        out.setConnected(false);
        _eventHandler.removeEventListener(this);
    }
}

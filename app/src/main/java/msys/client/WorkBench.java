package msys.client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import mdd.client.connectable.ParameterList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WorkBench extends Tab implements IGUIEventClient{
    private ScrollPane _scrollPane;
    public Processor _processor;
    private Point2D _pos = new Point2D(0.0f,0.0f);
    private GUIEventHandler _eventHandler;
    private SplitPane _sp_in_main_out;
    private ParameterList _gp_in, _gp_out;
    protected int _index;

    public WorkBench(Processor processor){
        super(processor.Name);
        _index = processor.getHandlerIndex();
        _eventHandler = GUIEventHandler.getEventHandler(_index);
        _eventHandler.addEventListener(this);
        _processor = processor;
        setId(_processor.getAsPrefix().toString());
        _sp_in_main_out = new SplitPane();
        _scrollPane = new ScrollPane(_processor.all);
        _gp_in = new ParameterList(_processor, "input", _index);
        _gp_out = new ParameterList(_processor, "output", _index);
        _sp_in_main_out.getItems().addAll(_gp_in, _scrollPane,_gp_out);
        _sp_in_main_out.setDividerPositions(0.1f, 0.9f);
        setContent(_sp_in_main_out);

        _scrollPane.addEventHandler(DragEvent.DRAG_DROPPED, e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject)parser.parse(db.getString());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                if (json.containsKey("server")){
                    if(json.get("server").equals(Client.client.getID())){
                        if(json.containsKey("add")){
                            //System.out.println("[WorkBench]: Drag detected: " + json.toString());
                            JSONObject jmsg = new JSONObject();
                            jmsg.put("operation", "add");
                            JSONObject jargs = new JSONObject();
                            jargs.put("subject",  _processor.getJSONIdentifier());
                            jargs.put("object",   json.get("add"));
                            jmsg.put("args", jargs);
                            _eventHandler.publishEvent(10,"try", jmsg);
                            success = true;
                        }
                    }
                }
            }
            e.setDropCompleted(success);
            e.consume();
        });

        _scrollPane.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                JSONObject json = new JSONObject();
                json.put("type", "optimizer");
                _eventHandler.publishEvent("MOUSE_PRESSED", json);
            }
            if(e.getButton() == MouseButton.SECONDARY) {

            }
            e.consume();
        });
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {

    }

    @Override
    public void close() throws Exception {
        _eventHandler.removeEventListener(this);
    }
}

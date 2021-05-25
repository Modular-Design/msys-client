package msys.client.connectable;

import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import mdd.client.GUIEventHandler;
import mdd.client.IGUIEventClient;
import mdd.client.Module;
import mdd.client.Processor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ParameterList extends ScrollPane implements IGUIEventClient {
    private GUIEventHandler _eventHandler;
    public Processor _processor;
    private VBox params = new VBox();
    private String _type;
    protected int _index;

    public ParameterList(Processor processor, String type, int handler_index){
        _index = handler_index;
        _type = type;
        _processor = processor;
        _eventHandler = GUIEventHandler.getEventHandler(_index);
        if (_type.equals("input")){
            params = _processor.params_in;
        }else {
            params = _processor.params_out;
        }

        this.setContent(params);

        addEventHandler(DragEvent.DRAG_OVER, e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                JSONParser parser = new JSONParser();
                JSONObject jmsg = null;
                try {
                    jmsg = (JSONObject)parser.parse(db.getString());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                if (!jmsg.containsKey("type")){
                    return;
                }
                if (!jmsg.get("type").toString().equals(_type)){
                    return;
                }
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        _eventHandler.addEventListener(this);
        addEventHandler(DragEvent.DRAG_DROPPED, e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                JSONParser parser = new JSONParser();
                JSONObject jmsg = null;
                try {
                    jmsg = (JSONObject)parser.parse(db.getString());
                    if (!jmsg.containsKey("prefix")){return;}
                    JSONArray jprefix = (JSONArray)jmsg.get("prefix");
                    JSONArray jprocessor = (JSONArray)_processor.getJSONIdentifier().get("prefix");
                    if (jprocessor.size() + 2 != jprefix.size()){return;}
                    for (int i = 0; i < jprocessor.size(); ++i){
                        if(!jprocessor.get(i).toString().equals(jprefix.get(i).toString())){
                            return;
                        }
                    }
                    if(!_processor.ID.equals(jprefix.get(jprefix.size()-2).toString())){
                        return;
                    }
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                JSONObject jtry = new JSONObject();
                jtry.put("operation", "add");
                JSONObject jargs = new JSONObject();
                jargs.put("subject",  _processor.getJSONIdentifier());
                jargs.put("object",   jmsg);
                jtry.put("args", jargs);
                _eventHandler.publishEvent(10,"try", jtry);
            }
        });
    }

    public void add(Module mod){
        params.getChildren().add(mod);
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
    }

    @Override
    public void close() throws Exception {

    }
}

package msys.client;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Configuration extends Group {
    private String _name = "";
    private String _topic = "";
    private JSONObject jparent_id;
    protected GUIEventHandler _eventHandler;
    protected int _index;

    Configuration(String topic, Module parent, String key, JSONObject json, int handler_index){
        _index = handler_index;
        jparent_id = parent.getJSONIdentifier();
        _name = key;
        _topic = topic;
        setUp(json);
    }

    Configuration(String topic, JSONObject parent_id, String key, JSONObject json, int handler_index){
        _index = handler_index;
        jparent_id = parent_id;
        _name = key;
        _topic = topic;
        setUp(json);
    }


    private void setUp(JSONObject json){
        _eventHandler = GUIEventHandler.getEventHandler(_index);

        if (json.containsKey("options")){
            ObservableList<String> options = FXCollections.observableArrayList();
            System.out.println(json.toString());
            for (Object opt : (JSONArray) json.get("options")){
                String item = opt.toString();
                options.add(opt.toString());

            }
            ComboBox cbox = new ComboBox(options);
            cbox.getSelectionModel().select(json.get("value").toString());
            cbox.setOnAction(e->{
                sendJSONRequest(cbox.getValue().toString());
            });
            getChildren().addAll(cbox);
        }
        else if(_name.equals("color")){
            ColorPicker colorPicker = new ColorPicker();
            colorPicker.setValue(Color.web(json.get("value").toString()));
            colorPicker.setOnAction(new EventHandler() {
                public void handle(Event t) {
                    sendJSONRequest(toHexString(colorPicker.getValue()));
                }
            });
            getChildren().addAll(colorPicker);
        }
        else {
            TextField cfield = new TextField(json.get("value").toString());
            cfield.setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    sendJSONRequest(cfield.getText().toString());
                }
            });
            getChildren().addAll(cfield);
        }
    }

    private void sendJSONRequest(String value){
        JSONObject jconfi = new JSONObject();
        jconfi.put("value", value);
        JSONObject jconfigs = new JSONObject();
        jconfigs.put(_name, jconfi);
        JSONObject jconfigure = new JSONObject();
        jconfigure.put(_topic, jconfigs);
        JSONObject jmsg = new JSONObject();
        jmsg.put("operation", "change");
        JSONObject jargs = new JSONObject();
        jargs.put("subject", jparent_id);
        jargs.put("object", jconfigure);
        jmsg.put("args", jargs);
        _eventHandler.publishEvent("try", jmsg);
    }

    private String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    private String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }
}

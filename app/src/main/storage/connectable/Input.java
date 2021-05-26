package msys.client.connectable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import mdd.client.ConnectableType;
import mdd.client.Module;
import org.json.simple.JSONObject;

public class Input  extends Connectable {
    private TextField _input;

    public Input(Module parent, JSONObject json, int handler_index){
        super(parent, ConnectableType.INPUT, json, handler_index);
        add(_connection,0,0);
        add(_label,1,0);
        add(_input,1,1);

        setAlignment(Pos.CENTER_LEFT);
        _input.setPrefWidth(50);

        _input.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                JSONObject jvalue = new JSONObject();
                jvalue.put("value", _input.getText());
                JSONObject jmsg = new JSONObject();
                jmsg.put("operation", "change");
                JSONObject jargs = new JSONObject();
                jargs.put("subject",  getJSONIdentifier());
                jargs.put("object",   jvalue);
                jmsg.put("args", jargs);
                _eventHandler.publishEvent("try", jmsg);
            }
        });
    }

    @Override
    public void configure(JSONObject json){
        super.configure(json);
        if (_input == null){
            _input = new TextField("");
        }
        _input.setText(Value.toString());
    }

    public TextField getInput(){
        return _input;
    }

    protected void updateColor(){
        super.updateColor();
        if (_connected || optimizable){
            _input.setVisible(false);
            _input.setManaged(false);
        }
        else{
            _input.setVisible(true);
            _input.setManaged(true);
        }
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
        super.processGUIEvent(event, args);
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
            if (jobj.containsKey("value")){
                _input.setText(jobj.get("value").toString());
            }
        }
    }
}

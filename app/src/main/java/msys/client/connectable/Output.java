package msys.client.connectable;

import javafx.geometry.Pos;
import mdd.client.ConnectableType;
import mdd.client.Module;
import mdd.client.connectable.Connectable;
import org.json.simple.JSONObject;

public class Output extends Connectable {

    public Output(Module parent, JSONObject json, int handler_index){
        super(parent, ConnectableType.OUTPUT, json, handler_index);
        add(_connection,1,0);
        add(_label,0,0);
        setAlignment(Pos.CENTER_RIGHT);
    }

}

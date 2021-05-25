package msys.client.connectable;

import mdd.client.Module;
import org.json.simple.JSONObject;

public class Parameter extends Module {

    public Parameter(JSONObject json, int handler_index){
        super(json, handler_index);
        moveable = false;
    }
}

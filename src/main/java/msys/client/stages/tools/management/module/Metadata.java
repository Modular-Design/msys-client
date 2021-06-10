package msys.client.stages.tools.management.module;

import java.util.HashMap;
import java.util.Map;

public class Metadata {
    public String name;
    public Map<String, Double> pos;
    public Boolean inverted;

    @SuppressWarnings("unchecked")
    public Metadata(Map<String, Object> config){
        name = (String)config.get("name");
        pos = (Map<String, Double>) config.get("pos");
        inverted = (Boolean) config.get("inverted");
        if (inverted == null){
            inverted = false;
        }
        if (pos == null){
            pos = new HashMap<>();
            pos.put("x", 0.0);
            pos.put("y", 0.0);
        }
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        if (name != null){
            map.put("name", name);
        }
        map.put("pos", pos);
        map.put("inverted", inverted);
        return map;
    }
}

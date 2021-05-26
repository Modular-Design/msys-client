package msys.client.eventhandling;



import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class GUIEventHandler {
    private static Vector<GUIEventHandler> _handler = new Vector<>();
    private ConcurrentSkipListMap<Integer,Vector<IGUIEventClient>> _priorities = new ConcurrentSkipListMap<>();

    public GUIEventHandler(){

    }

    public void addEventListener(IGUIEventClient client, Integer level){
        Vector<IGUIEventClient> clients =  _priorities.get(level);
        if (clients != null ){
            clients.add(client);
        }else{
            clients = new Vector<>();
            clients.add(client);
            _priorities.put(level, clients);
        }
    }

    public void addEventListener(IGUIEventClient client){
        addEventListener(client, 0);
    }

    public void removeEventListener(IGUIEventClient client){
        for (Map.Entry<Integer,Vector<IGUIEventClient>> entry : _priorities.entrySet()){
            if (entry.getValue().remove(client)){
                return;
            }
        }
    }

    public void publishEvent(Integer level, String event, Map<String,Object> args){
        System.out.println("EventHandler: " + event + " | " + args.toString());
        Integer start_key = _priorities.ceilingKey(level);
        if (start_key != null){
            for (Map.Entry<Integer,Vector<IGUIEventClient>> entry : _priorities.entrySet()){
                if (entry.getKey() >= start_key){
                    Vector<IGUIEventClient> clients = entry.getValue();
                    for (int i = 0; i < clients.size(); ++i) {
                        clients.elementAt(i).processGUIEvent(event, args);
                    }
                }
            }
        }
    }

    public void publishEvent(String event, Map<String,Object> args){
        publishEvent(0,event, args);
    }

    public static GUIEventHandler getEventHandler(int index){
        while(_handler.size() <= index){
            _handler.add(new GUIEventHandler());
        }
        return _handler.get(index);
    }
}


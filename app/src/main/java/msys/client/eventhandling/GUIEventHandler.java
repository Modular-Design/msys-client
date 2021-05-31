package msys.client.eventhandling;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;


public class GUIEventHandler {

    private static Vector<GUIEventHandler> _handler = new Vector<>();
    private ConcurrentSkipListMap<Integer,Vector<IGUIEventClient>> _priorities = new ConcurrentSkipListMap<>();

    /**
     *  level 0: Client
     *  level 1: Connectables
     *  level 2: Connections
     *  level 3: Modules
     *  level 4: Manager
     */
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


    /**
     * publish an event into the process stack
     * @param sender
     * @param receiver
     * @param level
     * @param event
     * @param msg
     */
    public void publishEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String,Object> msg){
        Integer start_key = _priorities.ceilingKey(level);
        if (start_key != null){
            for (Map.Entry<Integer,Vector<IGUIEventClient>> entry : _priorities.entrySet()){
                if (entry.getKey() >= start_key){
                    Vector<IGUIEventClient> clients = entry.getValue();
                    for (int i = 0; i < clients.size(); ++i) {
                        clients.elementAt(i).categorizeGUIEvent(sender, receiver, level, event, msg);
                    }
                }
            }
        }
    }

    public void publishEvent(Integer level, String event, Map<String,Object> msg){
        this.publishEvent(null, null, level, event, msg);
    }

    public static GUIEventHandler getEventHandler(int index){
        while(_handler.size() <= index){
            _handler.add(new GUIEventHandler());
        }
        return _handler.get(index);
    }
}


package msys.client.eventhandling;

import java.util.Map;

public abstract class GUIEventClient implements IGUIEventClient{
    protected GUIEventHandler handler;

    /**
     *  level 0: Client
     *  level 1: Connectables
     *  level 2: Connections
     *  level 3: Modules
     *  level 4: Manager
     */
    public GUIEventClient(int handler_no, int level){
        handler = GUIEventHandler.getEventHandler(handler_no);
        handler.addEventListener(this, level);
    }

    public void publishEvent(String receiver, Integer level, String event, Map<String,Object> msg){
        handler.publishEvent(this, receiver, level, event, msg);
    }

    @Override
    public void close() throws Exception {
        handler.removeEventListener(this);
    }
}

package msys.client.eventhandling;

import java.util.Map;

public abstract class GUIEventClient implements IGUIEventClient{
    protected GUIEventHandler handler;

    public GUIEventClient(int handler_no){
        handler = GUIEventHandler.getEventHandler(handler_no);
        handler.addEventListener(this);
    }

    public void publishEvent(Integer level, Events event, Map<String,Object> msg){
        handler.publishEvent(this, level, event, msg);
    }

    @Override
    public void close() throws Exception {
        handler.removeEventListener(this);
    }
}

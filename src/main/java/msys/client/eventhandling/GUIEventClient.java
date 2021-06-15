package msys.client.eventhandling;

import java.util.Map;

public abstract class GUIEventClient implements IGUIEventClient{
    protected GUIEventHandler handler;
    protected String id;
    protected int handler_no;
    protected int level;

    /**
     *  level 0: Client
     *  level 1: Connectables
     *  level 2: Connections
     *  level 3: Modules
     *  level 4: Manager
     */
    public GUIEventClient(String id, int handler_no, int level){
        this.id = id;
        this.handler_no = handler_no;
        this.level = level;
        handler = GUIEventHandler.getEventHandler(handler_no);
        handler.addEventListener(this, level);
    }

    public void publishEvent(String receiver, Integer level, String event, Map<String,Object> msg){
        handler.publishEvent(this, receiver, level, event, msg);
    }

    public String getID(){
        return id;
    }

    public int getHandlerNumber(){
        return handler_no;
    }

    public int getHandlerLevel(){
        return level;
    }

    @Override
    public void close() throws Exception {
        handler.removeEventListener(this);
    }
}

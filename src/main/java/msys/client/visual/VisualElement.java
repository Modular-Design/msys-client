package msys.client.visual;

import msys.client.eventhandling.GUIEventClient;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.IVisual;

import java.util.Map;

public abstract class VisualElement extends GUIEventClient implements IVisual {
    /**
     *  level 0: Client
     *  level 1: Connectables
     *  level 2: Connections
     *  level 3: Modules
     *  level 4: Manager
     */
    public VisualElement(String id, int handler_no, int level) {
        super(id, handler_no, level);
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {
        if (receiver != null){
            if (receiver.equals(getID())){
                processGUIEvent(sender, event, msg);
            }
        }
    }
}

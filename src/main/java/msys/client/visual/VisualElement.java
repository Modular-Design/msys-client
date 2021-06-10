package msys.client.visual;

import msys.client.eventhandling.GUIEventClient;
import msys.client.visual.IVisual;

public abstract class VisualElement extends GUIEventClient implements IVisual {
    /**
     *  level 0: Client
     *  level 1: Connectables
     *  level 2: Connections
     *  level 3: Modules
     *  level 4: Manager
     */
    public VisualElement(int handler_no, int level) {
        super(handler_no, level);
    }
}

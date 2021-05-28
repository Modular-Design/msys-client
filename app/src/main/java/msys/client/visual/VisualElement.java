package msys.client.visual;

import msys.client.eventhandling.GUIEventClient;
import msys.client.visual.IVisual;

public abstract class VisualElement extends GUIEventClient implements IVisual {
    public VisualElement(int handler_no) {
        super(handler_no);
    }
}

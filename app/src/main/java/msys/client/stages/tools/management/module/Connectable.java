package msys.client.stages.tools.management.module;

import javafx.scene.Node;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.VisualElement;

import java.util.Map;

public class Connectable extends VisualElement {
    public Connectable() {
        super(0);
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, Integer level, Events event, Map<String, Object> msg) {

    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, Events event, Map<String, Object> msg) {

    }

    @Override
    public Node getVisual() {
        return null;
    }
}

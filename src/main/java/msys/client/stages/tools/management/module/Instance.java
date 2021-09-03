package msys.client.stages.tools.management.module;

import javafx.scene.Node;
import msys.client.communication.WebSocketConnector;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.VisualRemoteElement;
import org.java_websocket.client.WebSocketClient;

import java.util.Map;

public class Instance extends VisualRemoteElement {
    private Module module;
    private WebSocketConnector connector;

    public Instance(String id, int handler_no, int level) {
        super(id, handler_no, level);
    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {

    }

    @Override
    public Node getVisual() {
        return null;
    }
}

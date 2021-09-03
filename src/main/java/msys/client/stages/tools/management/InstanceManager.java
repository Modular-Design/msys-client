package msys.client.stages.tools.management;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import msys.client.communication.NetworkEvents;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.eventhandling.Receivers;
import msys.client.stages.tools.management.module.Module;
import msys.client.visual.VisualElement;
import msys.client.visual.VisualRemoteElement;

import java.util.HashMap;
import java.util.Map;

public class InstanceManager extends VisualRemoteElement {
    private TabPane root = new TabPane();

    public InstanceManager(int handler_no) {
        super(Receivers.InstanceManager, handler_no, 4);


    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {
        if(event.equals(Events.OPEN)){
            Map<String, Object> map = new HashMap<>();
            map.put("url",this.getHost()+"/factory/open/"+msg.get("key"));
            publishEvent(Receivers.Client,0, NetworkEvents.GET, map);
            return;
        }

        // Module module = new Module(getHandlerNumber(), msg);
        Tab tab = new Tab();
        // tab.setText(module.getName());
        // tab.setContent(module.getInternLayout());
        root.getTabs().add(tab);
    }


    @Override
    public Node getVisual() {
        return root;
    }

    @Override
    public void setHost(String host) {
        super.setHost(host);
        Map<String, Object> map = new HashMap<>();
        map.put("url",this.getHost() + "/factory/active");
        publishEvent(Receivers.Client,0, NetworkEvents.GET, map);
    }
}

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

import java.util.HashMap;
import java.util.Map;

public class ModuleManager extends VisualElement {
    private TabPane root = new TabPane();

    public ModuleManager(int handler_no) {
        super("ModuleManager", handler_no, 4);
        Map<String, Object> map = new HashMap<>();
        map.put("url","/factory/all");
        publishEvent(Receivers.Client,0, NetworkEvents.GET, map);

    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {
        System.out.println("[ModuleManager]: processGUIEvent: "+event +", msg: "+  msg);
        Module module = new Module(getHandlerNumber(), msg);
        Tab tab = new Tab();
        tab.setText(module.getName());
        tab.setContent(module.getInternLayout());
        root.getTabs().add(tab);

    }


    @Override
    public Node getVisual() {
        return root;
    }
}

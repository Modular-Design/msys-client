package msys.client.stages.tools.management;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.stages.tools.management.module.Module;
import msys.client.visual.VisualElement;

import java.util.HashMap;
import java.util.Map;

public class ModuleManger extends VisualElement {
    private TabPane root = new TabPane();

    public ModuleManger() {
        super(0);
        Map<String, Object> map = new HashMap<>();
        map.put("url","/modules/root/");
        publishEvent(0,Events.GET, map);

    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, Integer level, Events event, Map<String, Object> msg) {

    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, Events event, Map<String, Object> msg) {
        Module module = new Module(msg);
        Tab tab = new Tab();
        tab.setText(module.getName());
        tab.setContent(module.getExternLayout());
        root.getTabs().add(tab);

    }


    @Override
    public Node getVisual() {
        return root;
    }
}

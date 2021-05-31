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
        super(0, 4);
        Map<String, Object> map = new HashMap<>();
        map.put("url","/modules/root/");
        publishEvent("client",0, Events.GET, map);

    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {
        if (receiver != null){
            if (receiver.equals("ModuleManager")){
                processGUIEvent(sender, event, msg);
            }
        }
    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {
        Module module = new Module(msg);
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

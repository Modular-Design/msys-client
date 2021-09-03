package msys.client.stages.tools.management.tool;

import com.google.gson.Gson;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import msys.client.communication.NetworkEvents;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.eventhandling.Receivers;
import msys.client.visual.VisualRemoteElement;

import java.util.HashMap;
import java.util.Map;

public class Tool extends VisualRemoteElement {
    private Map<String, String> content;
    private Button root = new Button();

    public Tool(Map<String, String> content) {
        super("Tool", 0, -1);
        this.content = content;
        root.setText(content.get("name"));

        root.addEventHandler(MouseEvent.DRAG_DETECTED, e -> {
            Dragboard db = root.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent clipboard = new ClipboardContent();
            clipboard.putString(new Gson().toJson(content));
            System.out.println("[Tool]: started drag with " + content.toString());
            db.setContent(clipboard);
            e.consume();
        });

        root.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(e.getButton().equals(MouseButton.PRIMARY)){
                if(e.getClickCount() == 2){
                    Map<String, Object> map = new HashMap<>();
                    map.put("host", this.getHost());
                    map.put("key", this.content.get("key"));
                    publishEvent(Receivers.InstanceManager, 0, Events.OPEN, map);
                    e.consume();
                }
            }
        });


    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {
        System.out.println("[Tool]: " + msg.toString());
    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {

    }

    @Override
    public Node getVisual() {
        return root;
    }
}

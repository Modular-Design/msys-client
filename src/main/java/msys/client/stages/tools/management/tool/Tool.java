package msys.client.stages.tools.management.tool;

import com.google.gson.Gson;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.VisualElement;

import java.util.Map;

public class Tool extends VisualElement {
    private Map<String, String> content;
    private Button root = new Button();

    public Tool(Map<String, String> content) {
        super(0, -1);
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

    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {

    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {

    }

    @Override
    public Node getVisual() {
        return root;
    }
}

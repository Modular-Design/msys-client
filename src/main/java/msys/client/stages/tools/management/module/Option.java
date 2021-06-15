package msys.client.stages.tools.management.module;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.VisualElement;

import java.util.ArrayList;
import java.util.Map;

public class Option extends VisualElement {
    private Map<String, Object> config;
    private TitledPane root;
    /**
     * level 0: Client
     * level 1: Connectables
     * level 2: Connections
     * level 3: Modules
     * level 4: Manager
     *
     */
    @SuppressWarnings("unchecked")
    public Option(int handler_no, Map<String, Object> config) {
        super(null, handler_no, 1);
        this.config = config;

        VBox layout = new VBox();


        ArrayList<String> selection = (ArrayList<String>)this.config.get("selection");
        if (selection != null) {
            boolean single = (boolean) this.config.get("single");
            if (single) {
                ToggleGroup radio_group = new ToggleGroup();
                for (String s: selection){
                    RadioButton btn = new RadioButton(s);
                    btn.setToggleGroup(radio_group);
                    layout.getChildren().add(btn);
                }
            } else {
                for (String s: selection){
                    layout.getChildren().add(new CheckBox(s));
                }
            }
        } else {
            layout.getChildren().add(new TextField((String)this.config.get("value")));
        }


        root = new TitledPane((String)this.config.get("title"), layout);
        String description = (String)this.config.get("description");
        if (description != null){
            root.setTooltip(new Tooltip(description));
        }
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

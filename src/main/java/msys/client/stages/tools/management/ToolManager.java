package msys.client.stages.tools.management;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import msys.client.Client;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.GUIEventHandler;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.stages.tools.management.tool.Tool;
import msys.client.visual.VisualElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToolManager extends VisualElement {
    private HBox root = new HBox();
    private ToolBar vertical_bar = new ToolBar();
    private VBox  extentsions = new VBox();
    private VBox  fast_access = new VBox();
    private VBox expandable  = new VBox();
    private BorderPane expandable_header = new BorderPane();
    private VBox tool_box = new VBox();
    private Map<String, Map<String,Object>> extension_overview = new HashMap<>();
    private final GUIEventHandler _eventHandler = GUIEventHandler.getEventHandler(0);

    public ToolManager(){
        super(0, 4);
        extentsions.setAlignment(Pos.TOP_CENTER);
        fast_access.setAlignment(Pos.BOTTOM_CENTER);

        Button favorites = new Button("Favorites");
        favorites.setRotate(-90);
        fast_access.getChildren().add(new Group(favorites));

        vertical_bar.setOrientation(Orientation.VERTICAL);
        vertical_bar.getItems().addAll(extentsions, new Separator(Orientation.VERTICAL), fast_access);
        VBox.setVgrow(vertical_bar, Priority.ALWAYS);
        root.getChildren().add(new VBox(vertical_bar));

        ScrollPane scrollPane = new ScrollPane();


        scrollPane.setContent(tool_box);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Button minimize = new Button("-");
        minimize.setAlignment(Pos.CENTER_RIGHT);
        minimize.setOnAction(event -> {
            expandable.setVisible(false);
            expandable.setManaged(false);
        });

        expandable_header.setRight(minimize);
        expandable.getChildren().addAll(expandable_header,scrollPane);
        expandable.setPrefWidth(150);
        expandable.setVisible(false);
        expandable.setManaged(false);
        root.getChildren().add(expandable);

        Map<String, Object> map = new HashMap<>();
        map.put("url","/modules/");
        publishEvent("Client", 0,Events.GET, map);
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {
        // never list process besides  direct messages
        if (receiver != null){
            if (receiver.equals("ToolManager")){
                processGUIEvent(sender, event, msg);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {
        ArrayList<Map<String,String>> extentions = (ArrayList<Map<String,String>>)msg.get("modules");

        int counter = 0;
        for (Map<String,String> elem: extentions){
            String pkg = elem.get("package");
            if (extension_overview.get(pkg) == null){
                Map<String,Object> map = new HashMap<>();
                Button menu = new Button("_"+(counter++)+":"+pkg);
                menu.setOnAction(btn_event -> {
                    ArrayList<Tool> tools = (ArrayList<Tool>) extension_overview.get(pkg).get("tools");
                    tool_box.getChildren().clear();
                    for (Tool tool : tools) {
                        tool_box.getChildren().add(tool.getVisual());
                    }
                    expandable.setVisible(true);
                    expandable.setManaged(true);
                });
                menu.setRotate(-90);
                extentsions.getChildren().add(new Group(menu));
                map.put("menu", menu);
                map.put("tools", new ArrayList<Tool>());
                extension_overview.put(pkg, map);
            }
            ((ArrayList<Tool>)extension_overview.get(pkg).get("tools")).add(new Tool(elem));
        }


    }

    @Override
    public Node getVisual() {
        return root;
    }
}

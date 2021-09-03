package msys.client.stages.tools.management;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import msys.client.communication.NetworkEvents;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.GUIEventHandler;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.eventhandling.Receivers;
import msys.client.stages.tools.management.tool.Tool;
import msys.client.visual.VisualElement;
import msys.client.visual.VisualRemoteElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToolManager extends VisualRemoteElement {
    private final HBox root = new HBox();
    private final ToolBar vertical_bar = new ToolBar();
    private final VBox  extentsions = new VBox();
    private final VBox  fast_access = new VBox();
    private final VBox expandable  = new VBox();
    private final BorderPane expandable_header = new BorderPane();
    private final VBox tool_box = new VBox();

    private final Map<String, String> endpoints = new HashMap<>();
    private final Map<String,Map<String,Tool>> tools = new HashMap<>();

    private final GUIEventHandler _eventHandler = GUIEventHandler.getEventHandler(0);

    public ToolManager(int handler_no){
        super("ToolManager", handler_no, 4);

        endpoints.put("/resources", "Nodes");
        endpoints.put("/factory/all", "Modules");

        generate_menus(endpoints);

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
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {
        if (msg.get("url") == null){
            return;
        }
        String url = (String) msg.get("url");
        for (String endpoint : endpoints.keySet()){
            if (!url.equals(this.getHost()+endpoint)){
                continue;
            }

            if(tools.get(endpoint) == null){
                tools.put(endpoint, new HashMap<>());
            }else{
                tools.get(endpoint).clear();
            }

            for (String key: msg.keySet()){
                if(key.equals("url")){
                    continue;
                }

                Map<String, String> content = (Map<String, String>) msg.get(key);
                content.put("name", content.get("name")+"("+key+")");
                content.put("key", key);
                if (tools.get(endpoint).get(key) == null){
                    var tool = new Tool(content);
                    tools.get(endpoint).put(key, tool);
                    tool.setHost(this.getHost());
                }
            }
        }
    }

    private void generate_menus(Map<String, String> map){
        int counter = 0;
        for (String key: map.keySet()){
            Button menu = new Button("_"+(counter++)+":"+ map.get(key));
            menu.setOnAction(btn_event -> {
                tool_box.getChildren().clear();
                var menu_tools = tools.get(key);
                if(menu_tools!= null){
                    for (Tool tool: menu_tools.values()) {
                        tool_box.getChildren().add(tool.getVisual());
                    }
                }
                expandable.setVisible(true);
                expandable.setManaged(true);
            });
            menu.setRotate(-90);
            extentsions.getChildren().add(new Group(menu));
        }
    }

    @Override
    public Node getVisual() {
        return root;
    }

    @Override
    public void setHost(String host) {
        super.setHost(host);

        for (String endpoint: endpoints.keySet()){
            Map<String, Object> map = new HashMap<>();
            map.put("url",this.getHost()+endpoint);
            publishEvent(Receivers.Client, 0, NetworkEvents.GET, map);
        }

    }
}

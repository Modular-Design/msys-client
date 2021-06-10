package msys.client.stages.tools.toolbox;

import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;


public class Toolbox extends ScrollPane implements IGUIEventClient{
    private GUIEventHandler _eventhandler;
    private GridPane gridpane_modules = new GridPane();
    private GridPane gridpane_processors = new GridPane();

    public Toolbox(){
        super();
        _eventhandler = GUIEventHandler.getEventHandler(0);
        _eventhandler.addEventListener(this);
        Accordion accordion = new Accordion();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        TitledPane pane1 = new TitledPane("Modules", gridpane_modules);
        TitledPane pane2 = new TitledPane("Processors", gridpane_processors);

        accordion.getPanes().add(pane1);
        accordion.getPanes().add(pane2);


        JSONObject json = new JSONObject();
        JSONObject jargs = new JSONObject();

        jargs.put("module", "all");
        jargs.put("processor", "all");
        json.put("operation", "state");
        json.put("args", jargs);

        _eventhandler.publishEvent("get", json);

        setContent(accordion);
        setFitToWidth(true);
    }

    @Override
    public void close() throws Exception {
        _eventhandler.removeEventListener(this);
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
        if (event.equals("receive")){
            if (!args.containsKey("operation")){ return; }
            if (!args.get("operation").toString().equals("state")){ return; }
            JSONObject jargs = (JSONObject)args.get("args");
            System.out.println("[Toolbox]: "+ args.toString());
            if (jargs != null) {
                if (jargs.containsKey("module")) {
                    JSONArray array = (JSONArray) jargs.get("module");
                    gridpane_modules.getChildren().clear();
                    for (int i = 0; i < array.size(); i++) {
                        Tool tool = new Tool((JSONObject) array.get(i));
                        gridpane_modules.add(tool, 0, i);
                    }
                }
                if (jargs.containsKey("processor")) {
                    JSONArray array = (JSONArray) jargs.get("processor");
                    gridpane_processors.getChildren().clear();
                    for (int i = 0; i < array.size(); i++) {
                        Tool tool = new Tool((JSONObject) array.get(i));
                        gridpane_processors.add(tool, 0, i);
                    }
                }
            }
        }
    }
}

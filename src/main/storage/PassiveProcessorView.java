package msys.client;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.simple.JSONObject;


public class PassiveProcessorView extends Stage implements IGUIEventClient {
    private Workshop workshop  = new Workshop(1);
    private PropertyManager manager = new PropertyManager(1);
    private SplitPane splitPane = new SplitPane();

    protected GUIEventHandler _eventHandler;

    public PassiveProcessorView(){
        super();
        _eventHandler = GUIEventHandler.getEventHandler(1);
        _eventHandler.addEventListener(this, 10);

        setTitle("Process View");

        splitPane.getItems().addAll(workshop, manager);
        Scene scene = new Scene(splitPane, 450, 450);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("dark-theme.css")));
        setScene(scene);
        splitPane.setDividerPositions(0.7);
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
        if (event.equals("OPEN")) {
            JSONObject jmsg = new JSONObject();
            jmsg.put("processor", args);
            _eventHandler.publishEvent("load",jmsg);
            show();
        }
    }
}

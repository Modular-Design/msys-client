package msys.client;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;

public class PropertyManager extends ScrollPane implements IGUIEventClient{
    private GUIEventHandler _eventHandler;
    private JSONObject jsel  = new JSONObject();
    private TitledPane pane1;
    private TitledPane pane2;
    protected int _index;

    public PropertyManager(){
        super();
        _index = 0;
        configure();
    }

    public PropertyManager(int handler_index){
        super();
        _index = handler_index;
        configure();
    }

    private void configure(){
        _eventHandler = GUIEventHandler.getEventHandler(_index);
        _eventHandler.addEventListener(this);
        Accordion accordion = new Accordion();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        pane1 = new TitledPane("Properties", null);
        pane2 = new TitledPane("other Properties", null);

        accordion.getPanes().add(pane1);
        accordion.getPanes().add(pane2);
        accordion.setExpandedPane(pane1);
        setContent(accordion);
        setFitToWidth(true);
    }

    private GridPane createLayout(String topic, JSONObject json){
        GridPane glayout = new GridPane();
        glayout.getChildren().clear();
        glayout.setHgap(5);
        glayout.setVgap(5);

        Iterator<String> itr = json.keySet().iterator();
        int counter = 0;
        while (itr.hasNext()){
            String key = itr.next();
            JSONObject jconfig;
            try {
                jconfig = (JSONObject) json.get(key);
            }catch (ClassCastException e){
                jconfig = new JSONObject();
                jconfig.put("value", json.get(key));
            }
            Label clabel = new Label(key);
            glayout.add(clabel,0,counter);
            glayout.add(new Configuration(topic, jsel, key, jconfig, _index),1,counter);
            ++counter;
        }

        return glayout;
    }

    private void setContent(TitledPane pane, JSONObject json){
        TabPane tabPane = new TabPane();
        Iterator<String> itr = json.keySet().iterator();
        while (itr.hasNext()){
            String key = itr.next();
            JSONObject jcontent = (JSONObject) json.get(key);
            Tab tab = new Tab(key  , new ScrollPane(createLayout(key, jcontent)));
            tabPane.getTabs().add(tab);
        }
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        pane.setContent(tabPane);
        return;
    }

    @Override
    public void close() throws Exception {
        _eventHandler.removeEventListener(this);
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
        if (event == "MOUSE_PRESSED"){
            if (!args.equals(jsel)){
                jsel = args;
                if(_index == 0){
                    JSONObject jmsg = new JSONObject();
                    jmsg.put("operation", "state");
                    jmsg.put("args", args);
                    _eventHandler.publishEvent("get", jmsg);
                }
                else{
                    _eventHandler.publishEvent("GET_PROPERTY", args);
                }
                return;
            }
            return;
        }
        if (event.equals("receive")){
            if (!args.containsKey("operation")){ return; }
            if (!args.get("operation").toString().equals("state")){ return; }
            JSONObject jargs = (JSONObject)args.get("args");
            System.out.println("[Toolbox]: "+ args.toString());
            if (jargs != null) {
                if (jargs.containsKey("info")){
                    JSONObject jinfo = (JSONObject)jargs.get("info");
                    setContent(pane1,jinfo);
                    System.out.println("[PropertyManger]: " + jinfo.toString());
                }
            }
        }
    }
}

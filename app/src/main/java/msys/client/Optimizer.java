package msys.client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mdd.client.optimization.DataModel;
import mdd.client.optimization.PDiagramm;
import mdd.client.optimization.PList;
import mdd.client.optimization.Permutation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Optimizer extends Stage implements IGUIEventClient {
private DataModel data = new DataModel();
private PList pList = new PList(data);
private PDiagramm pDiagramm = new PDiagramm(data);

protected GUIEventHandler _eventHandler;
private ScrollPane scrollpane = new ScrollPane();

    public Optimizer(){
        super();
        _eventHandler = GUIEventHandler.getEventHandler(0);
        _eventHandler.addEventListener(this, 5);

        setTitle("Optimizer");
        Scene scene = new Scene(new VBox(), 450, 450);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("dark-theme.css")));

        MenuBar menuBar = new MenuBar();

        scrollpane.setContent(pList);
        // --- Menu View
        Menu menuView = new Menu("View");
        MenuItem mi_list = new MenuItem("List");
        mi_list.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                scrollpane.setContent(pList);
            }
        });

        MenuItem mi_dia = new MenuItem("Diagramm");
        mi_dia.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                scrollpane.setContent(pDiagramm);
            }
        });

        menuView.getItems().addAll(mi_list, mi_dia);

        menuBar.getMenus().addAll(menuView);
        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, scrollpane);

        setScene(scene);
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
        if (event.equals("change")){
            if (!args.containsKey("args")){
                return;
            }
            JSONObject jargs = (JSONObject) args.get("args");
            if (!jargs.containsKey("subject")){
                return;
            }
            JSONObject jsub = (JSONObject) jargs.get("subject");
            if (!jsub.get("type").equals("optimizer")){
                return;
            }
            if (!args.containsKey("operation")){
                return;
            }
            JSONObject jobj = (JSONObject) jargs.get("object");
            switch((String) args.get("operation")){
                case "add":
                    if (jobj.containsKey("permutations")){
                        JSONArray indis= (JSONArray)jobj.get("permutations");
                        long lgen = (long)jobj.get("step");
                        for (int i = 0; i < indis.size(); ++i){
                            //TODO clear when start again
                            data.add(0, new Permutation(lgen, (JSONObject)indis.get(i)));
                        }
                    }else if(jobj.containsKey("best")){

                    }
                    break;
                case "change":
                    //configure(jobj);
            }
            return;
        }
        if (event == "load"){
            if(args.containsKey("optimizer")){
                JSONObject jargs = (JSONObject) args.get("optimizer");
                JSONArray indis= (JSONArray)jargs.get("permutations");
                if (indis != null){
                    data.clear();
                    for (int i = 0; i < indis.size(); ++i){
                        //TODO clear when start again
                        JSONObject jind = (JSONObject)indis.get(i);
                        long lgen = Long.decode(jind.get("step").toString());
                        data.add(0, new Permutation(lgen, jind));
                    }
                }
            }
        }
    }
}

package msys.client;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Vector;


public class Workshop extends TabPane implements IGUIEventClient {
    private GUIEventHandler _eventHandler;
    private Processor _main_processor;
    protected int _index;

    public Workshop(){
        super();
        _index = 0;
        configure();
    }

    public Workshop(int handler_index){
        super();
        _index = handler_index;
        configure();
    }

    private void configure(){
        _eventHandler = GUIEventHandler.getEventHandler(_index);
        _eventHandler.addEventListener(this);

        addEventHandler(DragEvent.DRAG_OVER, e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject)parser.parse(db.getString());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                if (json.containsKey("server")){
                    if(json.get("server").equals(Client.client.getID())){
                        e.acceptTransferModes(TransferMode.MOVE);
                    }
                }
            }
            e.consume();
        });
        Client.client.request_state();
        //loadExample();
        //_main_processor = new Processor(new Vector<String>(),"0");
        //showMainProcessor();
    }

    public void showMainProcessor(){
        showProcessor(_main_processor);
    }

    private void showProcessor(Processor processor){
        int existingID = -1;
        for (int i = 0; i < getTabs().size(); ++i){
            if (getTabs().get(i).getId().equals(processor.getAsPrefix().toString())){
                existingID = i;
            }
        }
        if (existingID == -1) {
            WorkBench bench = new WorkBench(processor);

            getTabs().add(bench);
            getSelectionModel().select(bench);
            bench.getTabPane().addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                if(e.getButton() == MouseButton.PRIMARY) {
                    _eventHandler.publishEvent("MOUSE_PRESSED", bench._processor.getJSONIdentifier());
                }
                if(e.getButton() == MouseButton.SECONDARY) {

                }
                e.consume();
            });
        }else{
            getSelectionModel().select(getTabs().get(existingID));
        }
    }


    @Override
    public void close() throws Exception {
        _eventHandler.removeEventListener(this);
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {

        if (event == "OPEN_PROCESSOR"){
            System.out.println("[Workshop]: OPEN_PROCESSOR with path = " +args.get("path").toString());
            Processor processor = _main_processor.getProcessor((Vector<String>)args.get("path"));
            if (processor != null){
                showProcessor(processor);
            }else{
                System.out.println("[Workshop]: Something went wrong!");
            }

        }
        if (event == "load"){
            if(args.containsKey("processor")){
                //if (_main_processor != null){
                //    try {
                //        _main_processor.close();
                //    } catch (Exception e) {
                //        e.printStackTrace();
                //    }
                //}
                JSONObject jargs = (JSONObject) args.get("processor");
                System.out.println("[Workshop]: load " +jargs.toString());
                _main_processor = new Processor(jargs, _index);
                showMainProcessor();
            }
        }
        if (event.equals("change")) {
            if (!args.containsKey("operation")) {
                return;
            }
            if (args.get("operation").toString().equals("remove")) {
                if (!args.containsKey("args")) {
                    return;
                }
                JSONObject jargs = (JSONObject) args.get("args");
                JSONObject jobj = (JSONObject) jargs.get("object");
                for (int i = 0; i < getTabs().size(); ++i){
                    JSONObject json = ((WorkBench)getTabs().get(i))._processor.getJSONIdentifier();
                    if (json.equals(jobj)) {
                        getTabs().remove(i);
                        break;
                    }
                }
            }
        }

    }
}

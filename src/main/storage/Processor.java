package msys.client.stages.tools.project;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import mdd.client.connectable.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Vector;

public class Processor extends java.lang.Module {
    public Group modules;
    public VBox params_in;
    public VBox params_out;
    public Group connections;
    public Group all;


    private boolean detected_drag = false;
    private boolean draw_temp_connection = false;
    public Connection temp_connection = new Connection();
    private Connectable connectable;

    public Processor(JSONObject json, int handler_index){
        super(json, handler_index);
        Type = "processor";

        modules.addEventHandler(MouseEvent.DRAG_DETECTED, e -> {
            // System.out.println("[Module]: dragg event detected!");
            if(e.getButton() == MouseButton.PRIMARY) {
                detected_drag = true;
            }
            if(e.getButton() == MouseButton.SECONDARY) {

            }
        });

        all.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            // System.out.println("[Module]: dragg event detected!");
            if(e.getButton() == MouseButton.PRIMARY) {
                if (draw_temp_connection){
                    System.out.println("[Processor] draw Line!");
                    temp_connection.setEndX(e.getSceneX());
                    temp_connection.setEndY(e.getSceneY());
                }
            }
            if(e.getButton() == MouseButton.SECONDARY) {

            }
        });

        all.addEventHandler(DragEvent.DRAG_DROPPED, e -> {
            //connections.getChildren().remove(temp_connection);
            System.out.println("[Processor] all");
            detected_drag = false;
        });

        modules.addEventHandler(DragEvent.DRAG_DROPPED, e -> {
            //connections.getChildren().remove(temp_connection);
            System.out.println("[Processor]");
            detected_drag = false;

            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                JSONParser parser = new JSONParser();
                JSONArray jsonList = null;
                try {
                    jsonList = (JSONArray)parser.parse(db.getString());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }

                assert jsonList != null;
                if (jsonList.size() == 2){
                    Input in = null;
                    Output out = null;
                    for (Object o : jsonList) {
                        JSONObject jobj = (JSONObject) o;
                        if (jobj.containsKey("type")) {
                            String type = jobj.get("type").toString();
                            JSONArray jprefix = (JSONArray)jobj.get("prefix");
                            String sparent = jprefix.get(jprefix.size()-1).toString();
                            String sID = jobj.get("key").toString()+jobj.get("appendix").toString();
                            if (type.equals("input")) {
                                System.out.println("[Processor] Dropped found type = " + "INPUT");
                                in = getModule(sparent).getInput(sID);
                            } else if (type.equals("output")) {
                                System.out.println("[Processor] Dropped found type = " + "OUTPUT");
                                out = getModule(sparent).getOutput(sID);
                            }
                        }
                    }
                    if (in != null && out != null) {
                        JSONObject jmsg = new JSONObject();
                        jmsg.put("operation","add");
                        JSONObject jargs = new JSONObject();
                        jargs.put("subject",  getJSONIdentifier());
                        JSONObject jconnection = new JSONObject();
                        jconnection.put("type","connection");
                        jconnection.put("input",in.getJSONIdentifier());
                        jconnection.put("output",out.getJSONIdentifier());
                        jargs.put("object", jconnection);
                        jmsg.put("args", jargs);
                        _eventHandler.publishEvent(10,"try",jmsg);

                        success = true;
                    }

                }

                System.out.println("[Processor] Dropped: " + db.getString());

            }
            e.setDropCompleted(success);
            e.consume();
        });
    }

    @Override
    public void configure(JSONObject json){
        super.configure(json);
        if (!json.containsKey("modules") && !json.containsKey("params") && !json.containsKey("connections")){return;}
        if (modules == null){
            modules = new Group();
            params_in = new VBox();
            params_out = new VBox();
            connections = new Group();
            all = new Group();
            all.getChildren().addAll(connections, modules);
        }
        else{
            modules.getChildren().clear();
            params_in.getChildren().clear();
            params_out.getChildren().clear();
            connections.getChildren().clear();
        }

        if (json.containsKey("modules")){
            for (Object o : (JSONArray) json.get("modules")){
                JSONObject jobj = (JSONObject) o;
                if (jobj.get("type").equals("module")){
                    modules.getChildren().add(new java.lang.Module(jobj, _index));
                }else if(jobj.get("type").equals("processor")){
                    modules.getChildren().add(new Processor(jobj, _index));
                }
            }
        }


        if (json.containsKey("params")){
            JSONObject jparam = (JSONObject)json.get("params");
            //inputs
            if (jparam.containsKey("inputs")){
                if (jparam.get("inputs") != null){
                    JSONArray jparam_in = (JSONArray)jparam.get("inputs");
                    for (int i = 0; i < jparam_in.size(); ++i){
                        JSONObject jmod = (JSONObject)jparam_in.get(i);
                        jmod.put("inputs", null);
                        params_in.getChildren().add(new Parameter(jmod, _index));
                    }
                }
            }
            //outputs
            if (jparam.containsKey("outputs")){
                if (jparam.get("outputs") != null){
                    JSONArray jparam_out = (JSONArray)jparam.get("outputs");
                    for (int i = 0; i < jparam_out.size(); ++i){
                        JSONObject jmod = (JSONObject)jparam_out.get(i);
                        jmod.put("outputs", null);
                        params_out.getChildren().add(new Parameter(jmod, _index));
                    }
                }
            }
        }


        if (json.containsKey("connections")){
            for (Object o : (JSONArray) json.get("connections")){
                JSONObject jobj = (JSONObject) o;
                JSONObject jout = (JSONObject)jobj.get("output");
                for (Object ins : (JSONArray) jobj.get("inputs")){
                    JSONObject jin = (JSONObject) ins;
                    //TODO update prefix
                    int lsize = ((JSONArray)jin.get("prefix")).size()-1;
                    java.lang.Module min = getModule(((JSONArray)jin.get("prefix")).get(lsize).toString());
                    lsize = ((JSONArray)jout.get("prefix")).size()-1;
                    java.lang.Module mout = getModule(((JSONArray)jout.get("prefix")).get(lsize).toString());
                    Input in = min.getInput(jin.get("key").toString()+jin.get("appendix").toString());
                    Output out = mout.getOutput(jout.get("key").toString()+jout.get("appendix").toString());
                    Connection con = new Connection(in, out, _index);
                    if (min instanceof Parameter || mout instanceof Parameter){
                        con.setVisible(false);
                        con.setManaged(false);
                    }
                    connections.getChildren().add(con);
                }
            }
        }


    }

    public java.lang.Module getModule(String id){
        for (Node node : modules.getChildren()){
            if (node instanceof java.lang.Module){
                java.lang.Module m = (java.lang.Module)node;
                if (m.ID.equals(id)){
                    return m;
                }
            }
        }
        for (Node node : params_in.getChildren()){
            if (node instanceof java.lang.Module){
                java.lang.Module m = (java.lang.Module)node;
                if (m.ID.equals(id)){
                    return m;
                }
            }
        }
        for (Node node : params_out.getChildren()){
            if (node instanceof java.lang.Module){
                java.lang.Module m = (java.lang.Module)node;
                if (m.ID.equals(id)){
                    return m;
                }
            }
        }
        return null;
    }

    public java.lang.Module getModule(Vector<String> id_path){
        id_path.remove(0);
        if (id_path.size() == 1){
            return getModule(id_path.firstElement());
        }
        else {
            Processor new_processor = (Processor)getModule(id_path.firstElement());
            return new_processor.getModule(id_path);
        }
    }

    public Processor getProcessor(Vector<String> id_path){
        id_path.remove(0);
        Processor new_processor = (Processor)getModule(id_path.firstElement());
        if (id_path.size() == 1){
            return new_processor;
        }else {
            return new_processor.getProcessor(id_path);
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
        for (int i = 0; i < connections.getChildren().size(); ++i)
        {
            try {
                ((Connectable)modules.getChildren().get(i)).close();
            }
            catch (Exception e){

            }
        }
        for (int i = 0; i < modules.getChildren().size(); ++i)
        {
            try {
                Object obj = modules.getChildren().get(i);
                if (obj instanceof Processor){
                    ((Processor)obj).close();
                }
                else {
                    ((java.lang.Module)obj).close();
                }
                modules.getChildren().remove(i);
            }
            catch (Exception e){

            }
        }
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
        super.processGUIEvent(event, args);
        if (isSelected){
            if (event.equals("KeyTyped")){
                if (!args.containsKey("key")){return;}
                String key = args.get("key").toString();
                if (key.equals("ENTER")){
                    JSONObject json = new JSONObject();
                    json.put("path", getAsPrefix());
                    _eventHandler.publishEvent("OPEN_PROCESSOR", json);
                }
                return;
            }
        }
        if (detected_drag){
            if (event.equals("DRAG_LINE")){
                draw_temp_connection = true;

                JSONObject json = args;
                if (json.containsKey("type")) {
                    String type = json.get("type").toString();
                    if (type.equals("INPUT")) {
                        System.out.println("[Processor] Line from type = " + "INPUT");
                        JSONArray jprefix = (JSONArray)json.get("prefix");
                        connectable = getModule(jprefix.get(jprefix.size()-1).toString()).getInput(json.get("ID").toString());
                    } else if (type.equals("OUTPUT")) {
                        System.out.println("[Processor] Line from type = " + "OUTPUT");
                        JSONArray jprefix = (JSONArray)json.get("prefix");
                        connectable = getModule(jprefix.get(jprefix.size()-1).toString()).getOutput(json.get("ID").toString());
                    }
                    if (connectable == null){
                        System.out.println("[Processor]: Couldn't find Connectable!");
                    }
                }
                if (connectable != null){
                    temp_connection= new Connection();
                    temp_connection.startXProperty().bind(connectable.x);
                    temp_connection.startYProperty().bind(connectable.y);
                    temp_connection.setEndX(connectable.x.getValue()+100);
                    temp_connection.setEndY(connectable.y.getValue()+100);
                }
                //connections.getChildren().add(temp_connection);
                return;
            }
        }
        if (event.equals("change")){
            if (!args.containsKey("operation")){return;}
            if (!args.containsKey("args")){return;}
            JSONObject jargs = (JSONObject) args.get("args");
            if (!jargs.containsKey("subject")){return;}
            JSONObject jsub = (JSONObject)jargs.get("subject");
            if (!jsub.equals(getJSONIdentifier())){
                return;
            }

           if (!jargs.containsKey("object")){return;}
            JSONObject jobj = (JSONObject)jargs.get("object");
            switch (args.get("operation").toString()){
                case "add":
                    switch (jobj.get("type").toString()){
                        case "connection":
                            System.out.println("[Processor]: Add Connection");
                            System.out.println(jobj.toString());
                            JSONObject jin = (JSONObject)jobj.get("input");
                            JSONObject jout = (JSONObject)jobj.get("output");
                            int lsize = ((JSONArray)jin.get("prefix")).size()-1;
                            Input in = getModule(((JSONArray)jin.get("prefix")).get(lsize).toString()).getInput(jin.get("key").toString()+jin.get("appendix").toString());
                            Output out = getModule(((JSONArray)jout.get("prefix")).get(lsize).toString()).getOutput(jout.get("key").toString()+jout.get("appendix").toString());
                            if (in != null && out != null) {
                                if (in.isConnected()) {
                                    for (int i = 0; i < connections.getChildren().size(); ++i) {
                                        Connection con = (Connection) connections.getChildren().get(i);
                                        if (con.in.equals(in)) {
                                            con.in.setConnected(false);
                                            con.out.setConnected(false);
                                            connections.getChildren().remove(i);
                                        }
                                    }
                                }
                            }
                            connections.getChildren().add(new Connection(in, out, _index));
                            break;
                        case "module":
                            if (jobj.get("key").toString().equals("Parameter")){
                                params_in.getChildren().add(new java.lang.Module(jobj, _index));
                            }else {
                                modules.getChildren().add(new java.lang.Module(jobj, _index));
                            }
                            break;
                        case "processor":
                            modules.getChildren().add(new Processor(jobj, _index));
                            break;
                    }
                    break;
                case "change":
                    configure(jobj);
                    break;
                case "remove":
                    ObservableList gmodule = modules.getChildren();
                    for (int i = 0; i < gmodule.size(); ++i) {
                        JSONObject json = ((java.lang.Module) gmodule.get(i)).getJSONIdentifier();
                        if (json.equals(jobj)) {
                            ObservableList gin = ((java.lang.Module) gmodule.get(i)).inputs.getChildren();
                            for (int j = 0; j < gin.size(); ++j){
                                Input in = (Input)gin.get(j);
                                if (in.isConnected()){
                                    ObservableList gcon = connections.getChildren();
                                    for (int k = 0; k < gcon.size(); ++k){
                                        Connection con = (Connection)gcon.get(k);
                                        if (con.in == in){
                                            try {
                                                con.close();
                                            } catch (Exception e) {
                                                //e.printStackTrace();
                                            }
                                            gcon.remove(k);
                                            break;
                                        }
                                    }
                                }
                            }
                            ObservableList gout = ((java.lang.Module) gmodule.get(i)).outputs.getChildren();
                            for (int j = 0; j < gout.size(); ++j){
                                Output out = (Output)gout.get(j);
                                if (out.isConnected()){
                                    ObservableList gcon = connections.getChildren();
                                    for (int k = 0; k < gcon.size(); ++k){
                                        Connection con = (Connection)gcon.get(k);
                                        if (con.out == out){
                                            try {
                                                con.close();
                                            } catch (Exception e) {
                                                //e.printStackTrace();
                                            }
                                            gcon.remove(k);
                                        }
                                    }
                                }
                            }
                            try {
                                ((java.lang.Module) gmodule.get(i)).close();
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }

                            modules.getChildren().remove(i);
                            break;
                        }
                    }
                    break;
                }
        }else if(event.equals("delete")){
            if (args.get("prefix").equals(getAsPrefix())){
                //TODO delete
                JSONObject jmsg = new JSONObject();
                jmsg.put("operation", "remove");
                JSONObject jargs = new JSONObject();
                jargs.put("subject", getJSONIdentifier());
                jargs.put("object", args);
                jmsg.put("args", jargs);
                _eventHandler.publishEvent(10,"try", jmsg);
            }
        }

    }
}

package msys.client;

import com.google.gson.Gson;
import javafx.application.Platform;
import msys.client.eventhandling.GUIEventHandler;
import msys.client.eventhandling.IGUIEventClient;
import com.google.gson.Gson.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.util.HashMap;
import java.util.Map;

public class Client implements IGUIEventClient
{
    private ZContext context;
    private ZMQ.Socket req_socket;
    private ZMQ.Socket sub_socket;
    private long server_id = 0;
    private long msg_counter = 0;
    private Thread listenerThread;
    private GUIEventHandler _eventHandler;

    static Client client = new Client();

    public void request_state(){
        Map<String, Object> json = new HashMap<>();
        json.put("operation", "state");
        json.put("args", "all");
        Gson gson = new Gson();
        JSONObject jmsg = request("get",json);
        server_id = (long)jmsg.get("serverID");
        msg_counter = (long) jmsg.get("msgNr");
        if (jmsg.containsKey("receive"))
        {
            JSONObject jstate = (JSONObject)jmsg.get("receive");
            System.out.println("[Client]: state: " + jstate.toString());
            _eventHandler.publishEvent("load", (JSONObject)jstate.get("args"));
        }

        //System.out.println("State : " + jmsg.toString());
    }

    private void processChangeResponse(JSONObject jchange){
        String soper = (String)jchange.get("operation");
        if (soper.equals("change")){
            JSONObject jargs = (JSONObject) jchange.get("args");
            if (jargs != null){
                JSONObject jobj = (JSONObject) jargs.get("object");
                if (jobj != null){
                    if (jobj.containsKey("GUI")){
                        _eventHandler.publishEvent(5,"change", jchange);
                        return;
                    }
                }
            }
        }
        _eventHandler.publishEvent("change", jchange);
    }

    private void listen(){
        while (!Thread.currentThread().isInterrupted()) {
            // Read envelope with address
            String address = sub_socket.recvStr();
            // Read message contents
            String content = sub_socket.recvStr();

            JSONParser jsonParser = new JSONParser();
            JSONObject jmsg = null;
            try {
                jmsg = (JSONObject) jsonParser.parse(content);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if ((long) jmsg.get("serverID") == server_id){
                if ((long) jmsg.get("msgNr") == msg_counter) {
                    //<--close Thread

                    JSONObject finalJmsg = jmsg;
                    Platform.runLater(new Runnable() {
                                          @Override
                                          public void run() {
                                              if (finalJmsg.get("change") instanceof JSONArray){
                                                  JSONArray jarray = (JSONArray) finalJmsg.get("change");
                                                  for(int i = 0; i < jarray.size(); ++i){
                                                      processChangeResponse((JSONObject)jarray.get(i));
                                                  }
                                              }else{
                                                  JSONObject jchange = (JSONObject) finalJmsg.get("change");
                                                  processChangeResponse(jchange);
                                              }
                                          }
                                      });
                    //<--reopen Thread
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            request_state();
                        }
                     });
                }
            }
            System.out.println("[Client]: listen: " + address + " : " + content);//Todo
            ++msg_counter;
        }
    }

    public Client(){
        _eventHandler = GUIEventHandler.getEventHandler(0);
        _eventHandler.addEventListener(this, 10);
        try{
            context = new ZContext();
            req_socket = context.createSocket(SocketType.REQ);
            req_socket.connect("tcp://localhost:5555");

            sub_socket = context.createSocket(SocketType.SUB);
            sub_socket.connect("tcp://localhost:5556");
            sub_socket.subscribe("CHANGE".getBytes(ZMQ.CHARSET));
        } catch (Exception e) {
            e.printStackTrace();
        }
        request_state();

        Runnable runnable = this::listen;
        listenerThread = new Thread(runnable);
        listenerThread.start();
    }

    public JSONObject request(JSONObject json){
        req_socket.send(json.toString().getBytes(ZMQ.CHARSET), 0);
        String reply = req_socket.recvStr(0);
        JSONParser jsonParser = new JSONParser();
        JSONObject jmsg = null;
        try {
            jmsg = (JSONObject) jsonParser.parse(reply);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //System.out.println("[Client]: request: "+ jmsg.toString());
        return jmsg;
    }

    public JSONObject request(String flag, JSONObject json){
        JSONObject jmsg = new JSONObject();
        jmsg.put("serverID", server_id);
        jmsg.put("msgNr", msg_counter);
        jmsg.put("protocol", "0.1");

        JSONArray jcontext = new JSONArray();
        JSONObject jelem = new JSONObject();
        jelem.put(flag, json);
        jcontext.add(jelem);
        jmsg.put("context", jcontext);
        return request(jmsg);
    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {
        if (event.equals("try")){
            JSONObject jans = request("try",args);
        }
        if (event.equals("get")){
            JSONObject jans = request("get",  args);
            _eventHandler.publishEvent("receive", (JSONObject)jans.get("receive"));
        }
    }

    @Override
    public void close() throws Exception {
        listenerThread.join();
        //close Server
        _eventHandler.removeEventListener(this);
    }

    public long getID(){return server_id;}
}
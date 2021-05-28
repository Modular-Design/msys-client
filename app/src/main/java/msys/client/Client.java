package msys.client;

import com.google.gson.Gson;
import javafx.application.Platform;
import msys.client.eventhandling.Events;
import msys.client.eventhandling.GUIEventClient;
import msys.client.eventhandling.IGUIEventClient;


import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.http.util.EntityUtils;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Client extends GUIEventClient
{
    private String protocol;
    private String host;
    //private Websocket
    private ZMQ.Socket sub_socket;
    private final Thread listenerThread;

    //static Client client = new Client("ipc://localhost:8000", "/pubsub");

    /**
     * @param url the url of the msys-Server
     * @param endpoint the Publischer-Subscriber-Endpoint typically "/pubsub"
     */
    public Client(String url, String endpoint){
        super(0);

        //url is <protocol>//<host>
        String[] parts= url.split("://");
        switch (parts.length) {
            case 1 -> {
                protocol = "http";
                host = parts[0];
            }
            case 2 -> {
                protocol = parts[0];
                host = parts[1];
            }
        }


        try{
            ZContext context = new ZContext();
            sub_socket = context.createSocket(SocketType.SUB);
            sub_socket.connect("ipc://"+host+ endpoint);
            sub_socket.subscribe("".getBytes());
            //sub_socket.subscribe("connect".getBytes(ZMQ.CHARSET));
            //sub_socket.subscribe("change".getBytes(ZMQ.CHARSET));
            //sub_socket.subscribe("delete".getBytes(ZMQ.CHARSET));
        } catch (Exception e) {
            e.printStackTrace();
        }
        request_state();

        Runnable runnable = this::listen;
        listenerThread = new Thread(runnable);
        listenerThread.start();
    }

    /**
     * sends the according http-request
     * @param event process event
     * @param map mapped body with url-key
     * @return response
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object>  request(Events event, Map<String, Object> map){
        var path = (String)map.get("url");
        if (path == null){
            return null;
        }

        // create request
        String body = new Gson().toJson(map);
        HttpRequestBase request = null;

        String url = null;
        try {

            URL u = new URL( protocol +"://"+ host + path);
            URI uri = new URI(u.getProtocol(), u.getUserInfo(), u.getHost(), u.getPort(), u.getPath(), u.getQuery(), u.getRef());
            url = uri.toString();

        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

        switch (event) {
            case GET -> request = new HttpGet(url);
            case ADD, CONNECT -> {
                request = new HttpPost(url);
                try {
                    ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(body));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            case CHANGE -> {
                request = new HttpPut(url);
                try {
                    ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(body));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            case DELETE -> request = new HttpDelete(url);
        }

        // send request and send response
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String json = EntityUtils.toString(response.getEntity());
            try {
                return new Gson().fromJson(json, Map.class);//TODO unsafe
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void request_state(){
        // placeholder mabye later for reloader
    }


    /**
     * listens to the Publisher and Subscriber-Endpoint
     */
    @SuppressWarnings("unchecked")
    private void listen(){
        while (!Thread.currentThread().isInterrupted()) {
            // Read envelope with address
            String topic = sub_socket.recvStr();
            // Read message contents
            String body = sub_socket.recvStr();

            System.out.println("[Client]: "+body);
            Map<String, Object> map = new Gson().fromJson(body, Map.class);//TODO unsafe

            boolean everything_ok = true;

            if (everything_ok){
                Events event = event_from_string(topic);
                Platform.runLater(() -> publishEvent(1, event, map));
            } else {
                Platform.runLater(this::request_state);
            }
        }
    }

    private Events event_from_string(String topic){
        System.out.println(topic); //TODO
        switch (topic){
            case "add" ->{
                return Events.ADD;
            }
            case "connect" ->{
                return Events.CONNECT;
            }
            case "change" ->{
                return Events.CHANGE;
            }
            case "delete" ->{
                return Events.DELETE;
            }
            case "get" ->{
                return Events.GET;
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        super.close();
        listenerThread.join();
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, Integer level, Events event, Map<String, Object> msg) {
        System.out.println(level);
        if (level == 0){
            processGUIEvent(sender, event, msg);
        }
    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, Events event, Map<String, Object> msg) {
        System.out.println("processing");
        var result = request(event,  msg);
        System.out.println(result);//sout
        if (result != null){
            if (sender != null){
                sender.processGUIEvent(this, event, result);
            }else {
                publishEvent(1, event, result);
            }

        }
    }
}
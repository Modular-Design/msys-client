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
import java.net.URI;


import java.net.URISyntaxException;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class Client extends GUIEventClient
{
    private String protocol;
    private String host;

    //static Client client = new Client("ipc://localhost:8000", "/pubsub");

    /**
     * @param url the url of the msys-Server
     * @param endpoint the Publischer-Subscriber-Endpoint typically "/pubsub"
     */
    public Client(String url, String endpoint){
        super(0, 0);

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
            URL u = new URL( "http://"+ host +"/pubsub");
            URI uri = new URI("ws", null, u.getHost(), u.getPort(), u.getPath(), u.getQuery(), u.getRef());
            System.out.println("[Client]: "+ uri.toASCIIString());
            WebSocketClient client = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("opened connection");
                }

                @Override
                @SuppressWarnings("unchecked")
                public void onMessage(String message) {
                    Map<String, Object> msg = new Gson().fromJson(message, Map.class);//TODO unsafe
                    // Read envelope with address
                    String topic = (String) msg.get("topic");
                    // Read message contents
                    String receiver = new Gson().toJson(msg.get("receiver"));
                    Map<String, Object> map = (Map<String, Object>)msg.get("content");

                    boolean everything_ok = true;

                    if (everything_ok){
                        Platform.runLater(() -> publishEvent(receiver,1, topic, map));
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    // The codecodes are documented in class org.java_websocket.framing.CloseFrame
                    System.out.println(
                            "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                                    + reason);
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                    // if the error is fatal then onClose will be called additionally
                }
            };
            client.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        request_state();

        //Runnable runnable = this::listen;
        //listenerThread = new Thread(runnable);
        //listenerThread.start();
    }

    /**
     * sends the according http-request
     * @param event process event
     * @param map mapped body with url-key
     * @return response
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object>  request(String event, Map<String, Object> map){
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

        if (Events.GET.equals(event)) {
            request = new HttpGet(url);
        } else if (Events.ADD.equals(event) || Events.CONNECT.equals(event)) {
            request = new HttpPost(url);
            try {
                ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(body));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (Events.CHANGE.equals(event)) {
            request = new HttpPut(url);
            try {
                ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(body));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (Events.DELETE.equals(event)) {
            request = new HttpDelete(url);
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

        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {
        if (level == 0 || receiver.equals("Client")){
            processGUIEvent(sender, event, msg);
        }
    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {
        var result = request(event,  msg);
        if (result != null){
            if (sender != null){
                sender.processGUIEvent(this, event, result);
            }else {
                publishEvent(null, 1, event, result);
            }

        }
    }
}
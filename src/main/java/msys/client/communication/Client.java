package msys.client.communication;

import com.google.gson.Gson;

import msys.client.eventhandling.GUIEventClient;
import msys.client.eventhandling.IGUIEventClient;

import msys.client.eventhandling.Receivers;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.http.util.EntityUtils;
import java.net.URI;

import java.net.URISyntaxException;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;


public class Client extends GUIEventClient
{
    //static Client client = new Client("ipc://localhost:8000", "/pubsub");

    public Client(){
        super(Receivers.Client, 0, 0);

        /*
        try{
            URL u = new URL( "http://"+ host +"/pubsub");
            URI uri = new URI("ws", null, u.getHost(), u.getPort(), u.getPath(), u.getQuery(), u.getRef());
            System.out.println("[Client]: "+ uri.toASCIIString());
            WebSocketClient client = new WebSocketConnector(uri, this);
            client.connect();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[Client]: Exception");
        }
        //*/

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
        var url = (String)map.get("url");
        if (url == null){
            throw new NullPointerException("Url cant be null!");
        }

        // create request
        String body = new Gson().toJson(map);
        HttpRequestBase request;

        if (NetworkEvents.GET.equals(event)){
            request = new HttpGet(url);
        } else if (NetworkEvents.PUT.equals(event)) {
            request = new HttpPut(url);
            try {
                ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(body));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (NetworkEvents.POST.equals(event)) {
            request = new HttpPost(url);
            try {
                ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(body));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (NetworkEvents.DELETE.equals(event)) {
            request = new HttpDelete(url);
        } else{
            throw new RuntimeException("Dont support the folling Event:" + event+ "!");
        }

        // send request and receive response
        System.out.println("[Client]: send request and receive response");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            System.out.println(response);
            System.out.println(response.getEntity());
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
        System.out.println("[Client]: " + result.toString());
        if (result != null){
            if (sender != null){
                sender.processGUIEvent(this, event, result);
            }else {
                publishEvent(null, 1, event, result);
            }

        }
    }
}
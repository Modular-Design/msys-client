package msys.client.communication;

import com.google.gson.Gson;
import javafx.application.Platform;
import msys.client.eventhandling.IGUIEventClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class WebSocketConnector extends WebSocketClient {
    private final IGUIEventClient eventClient;

    public WebSocketConnector(IGUIEventClient parent,String uri) throws URISyntaxException {
        super(new URI(uri));
        this.eventClient = parent;
    }

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

        Platform.runLater(() -> eventClient.publishEvent(receiver, 1, topic, map));
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
        System.out.println("[Client]: [Websocket] onError");
        // if the error is fatal then onClose will be called additionally
    }
}

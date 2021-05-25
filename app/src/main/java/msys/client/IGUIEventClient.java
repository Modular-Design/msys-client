package msys.client;

import org.json.simple.JSONObject;

public interface IGUIEventClient extends AutoCloseable {
    void processGUIEvent(String event, JSONObject args);
}

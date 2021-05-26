package msys.client.eventhandling;

import java.util.Map;

public interface IGUIEventClient extends AutoCloseable {
    void processGUIEvent(String event, Map<String, Object> args);
}

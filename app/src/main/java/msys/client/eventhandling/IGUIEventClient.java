package msys.client.eventhandling;

import java.util.Map;

public interface IGUIEventClient extends AutoCloseable {

    /**
     * Process an GUIEvent
     *
     * @param sender if a sender is not null treat this as a direct message
     * @param level
     * @param event
     * @param msg
     */
    void categorizeGUIEvent(IGUIEventClient sender, Integer level, Events event, Map<String, Object> msg);

    /**
     * Here the GUIEvent processing takes place
     * The method has to assume the Event wants to make changes to the process.
     * Its also used for direct messaging and responding.
     *
     * @param sender
     * @param event
     * @param msg
     */
    void processGUIEvent(IGUIEventClient sender, Events event, Map<String, Object> msg);
}

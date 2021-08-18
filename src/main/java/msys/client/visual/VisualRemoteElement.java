package msys.client.visual;

public abstract class VisualRemoteElement extends VisualElement implements IRemote{
    /**
     * level 0: Client
     * level 1: Connectables
     * level 2: Connections
     * level 3: Modules
     * level 4: Manager
     *
     * @param id
     * @param handler_no
     * @param level
     */
    private String host;

    public VisualRemoteElement(String id, int handler_no, int level) {
        super(id, handler_no, level);
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }
}

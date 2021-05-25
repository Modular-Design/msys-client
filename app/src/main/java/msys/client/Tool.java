package msys.client;

import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import org.json.simple.JSONObject;

public class Tool extends Label {
    private JSONObject _context = new JSONObject();

    public Tool(JSONObject json){
        super(json.get("key").toString());
        _context = json;
        addEventHandler(MouseEvent.DRAG_DETECTED, e -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            JSONObject jmsg = new JSONObject();
            jmsg.put("server", Client.client.getID());
            jmsg.put("add", _context);

            content.putString(jmsg.toString());
            System.out.println("[Tool]: started drag with context = " + jmsg.toString());
            db.setContent(content);
            e.consume();
        });
    }
}

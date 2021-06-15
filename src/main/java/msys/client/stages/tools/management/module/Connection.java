package msys.client.stages.tools.management.module;

import javafx.animation.Interpolator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import msys.client.eventhandling.IGUIEventClient;
import msys.client.visual.VisualElement;

import java.util.ArrayList;
import java.util.Map;

public class Connection extends VisualElement{
    private Module parent;
    private Connectable in;
    private Connectable out;
    private Line line = new Line();

    private Group root = new Group();
    private boolean linear = true;

    /**
     * level 0: Client
     * level 1: Connectables
     * level 2: Connections
     * level 3: Modules
     * level 4: Manager
     *
     * @param handler_no
     */
    public Connection(int handler_no, Connectable out, Connectable in) {
        super(null, handler_no, 2);
        this.in = in;
        this.out = out;
        this.parent = parent;
        updateVisual();
    }

    @Override
    public void categorizeGUIEvent(IGUIEventClient sender, String receiver, Integer level, String event, Map<String, Object> msg) {
        if (receiver != null){
            if (receiver.equals(in.getID()) || receiver.equals(out.getID())){
                processGUIEvent(sender, event, msg);
            }
        }
    }

    @Override
    public void processGUIEvent(IGUIEventClient sender, String event, Map<String, Object> msg) {
        updateVisual();
    }

    private void update_shape(){
        root.getChildren().clear();
        if (linear){
            root.getChildren().addAll(line);
        }
        else{

        }
    }

    public ArrayList<DoubleBinding> iterateParent(Node node, int steps){
        DoubleBinding bx = node.layoutXProperty().add(node.translateXProperty());
        DoubleBinding by = node.layoutYProperty().add(node.translateYProperty());
        Parent parent = node.getParent();
        for (int i = 0; i < steps; i++) {
            bx = bx.add(parent.layoutXProperty()).add(parent.translateXProperty());
            by = by.add(parent.layoutYProperty()).add(parent.translateYProperty());
            parent = parent.getParent();
        }
        ArrayList<DoubleBinding> res = new ArrayList<>();
        res.add(bx);
        res.add(by);
        return res;
    }

    public void updateVisual(){
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(1);//20 for hidden

        Circle in_center = in.getInputCenter();
        Circle out_center = out.getOutputCenter();
        var props = iterateParent(in_center, 9);
        line.startXProperty().bind(in_center.centerXProperty().add(props.get(0)));
        line.startYProperty().bind(in_center.centerYProperty().add(props.get(1)));

        props = iterateParent(out_center, 9);
        line.endXProperty().bind(in_center.centerXProperty().add(props.get(0)));
        line.endYProperty().bind(in_center.centerYProperty().add(props.get(1)));
        update_shape();
    }

    @Override
    public Node getVisual() {
        return root;
    }
}

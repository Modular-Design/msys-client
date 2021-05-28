package msys.client.stages;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import msys.client.stages.tools.management.ModuleManger;
import msys.client.stages.tools.management.ProjectManager;
import msys.client.stages.tools.management.PropertyManager;
import msys.client.stages.tools.management.ToolManager;

public class Project extends Stage {
    private ProjectManager project_manager = new ProjectManager();
    private ToolManager tool_manager = new ToolManager();
    private PropertyManager property_manager = new PropertyManager();
    private ModuleManger module_manager = new ModuleManger();

    public Project(){
        Circle circ = new Circle(40, 40, 30);
        BorderPane root = new BorderPane();
        root.setTop(project_manager.getVisual());
        root.setLeft(tool_manager.getVisual());
        root.setRight(property_manager.getVisual());
        root.setCenter(module_manager.getVisual());
        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/light-theme.css").toExternalForm());
        setTitle("Project");
        setMaximized(true);
        setScene(scene);

        //Adding a tile to the application
    }
}

package msys.client.stages;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import msys.client.stages.tools.management.ModuleManager;
import msys.client.stages.tools.management.ProjectManager;
import msys.client.stages.tools.management.PropertyManager;
import msys.client.stages.tools.management.ToolManager;

public class Project extends Stage {
    private ProjectManager project_manager = new ProjectManager(0);
    private ToolManager tool_manager = new ToolManager(0);
    private PropertyManager property_manager = new PropertyManager(0);
    private ModuleManager module_manager = new ModuleManager(0);

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

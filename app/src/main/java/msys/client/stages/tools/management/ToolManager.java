package msys.client.stages.tools.management;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;

public class ToolManager extends BorderPane {
    private MenuBar extentsions = new MenuBar();

    public ToolManager(){
        Menu msys = new Menu("msys");
        extentsions.getMenus().add(msys);
        extentsions.setRotate(-90);
        setTop(extentsions);

    }

}

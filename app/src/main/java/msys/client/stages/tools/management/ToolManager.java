package msys.client.stages.tools.management;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

public class ToolManager extends VBox {
    private MenuBar extentsions = new MenuBar();
    //private MenuBar extentsions = new MenuBar();

    public ToolManager(){
        Menu msys_opt = new Menu("_2:msys_opt");
        Menu msys = new Menu("_1:msys");
        extentsions.getMenus().add(msys_opt);
        extentsions.getMenus().add(msys);
        Rotate rotation = new Rotate(-90);
        rotation.setPivotX(-extentsions.getWidth()/2);
        rotation.setPivotY(extentsions.getHeight()/2);
        Transform translate = new Translate(-extentsions.getMaxWidth(),0);
        //setTop(extentsions);
        extentsions.getTransforms().addAll(rotation, translate);
        //extentsions.setRotate(-90);

        Region spacer = new Region();
        spacer.getStyleClass().add("menu-bar");
        VBox.setVgrow(spacer, Priority.SOMETIMES);
        spacer.setMaxWidth(extentsions.getWidth());
        getChildren().addAll(extentsions);//, spacer
                //getTransforms().add(rotation);
        //msys.setMinSize(0,0);
        //extentsions.setRotate(-90);


    }

}

package msys.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {
    private GUIEventHandler _eventhandler = GUIEventHandler.getEventHandler(0);
    private Optimizer _optimizer = new Optimizer();
    private PassiveProcessorView _view = new PassiveProcessorView();
    @FXML
    public Workshop workshop;
    @FXML
    public Toolbox toolbox;
    @FXML
    public PropertyManager propertymanager;
    @FXML
    public SplitPane sPane;

    private Stage primaryStage;

    public void setStage(Stage stage){
        primaryStage = stage;
    }

    @FXML
    public void miNewClicked(Event e){
        System.out.println("[FXMLDocumentController]: Not implemented!");
    }

    @FXML
    public void miOpenClicked(Event e){
        System.out.println("[FXMLDocumentController]: Not implemented!");
    }

    @FXML
    public void miOpen_RecentClicked(Event e){
        System.out.println("[FXMLDocumentController]: Not implemented!");
    }

    @FXML
    public void miClose_ProjectClicked(Event e){
        System.out.println("[FXMLDocumentController]: Not implemented!");
    }

    @FXML
    public void miSettingsClicked(Event e){
        System.out.println("[FXMLDocumentController]: Not implemented!");
    }

    @FXML
    public void miSaveClicked(Event e){
        JSONObject json = new JSONObject();
        json.put("operation", "save");
        json.put("args", "");
        _eventhandler.publishEvent("try", json);
    }

    @FXML
    public void miSave_AsClicked(Event e){
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            //saveTextToFile(sampleText, file);
            JSONObject json = new JSONObject();
            json.put("operation", "state");
            json.put("args", "all");

            _eventhandler.publishEvent("try", json);
            System.out.println("[FXMLDocumentController]: Not implemented!");
        }
    }

    @FXML
    public void miExitClicked(Event e){
        Platform.exit();
        System.exit(0);
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Loading...");
        workshop.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        SplitPane.setResizableWithParent(workshop, false);
        SplitPane.setResizableWithParent(toolbox, false);
        SplitPane.setResizableWithParent(propertymanager, false);
        sPane.setDividerPositions(0.1, 0.9);
    }

    public void miOptimizerClicked(ActionEvent actionEvent) {
        _optimizer.show();
    }

    //@FXML private TabPane tabPane;

    /*public FXMLDocumentController() {

    }//*/
}

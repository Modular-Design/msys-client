package msys.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import org.json.simple.JSONObject;


import java.io.IOException;

public class GUIMain extends Application implements IGUIEventClient{
    private GUIEventHandler _eventHandler;
    private Client client = Client.client;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        _eventHandler = GUIEventHandler.getEventHandler(0);

        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
           throw new RuntimeException(e);
        }

        root.setFocusTraversable(true);

        root.setOnKeyPressed(e -> {
            System.out.println("[Application]: "+e.getText());
            JSONObject json = new JSONObject();
            KeyCode code = e.getCode();

            if (code == KeyCode.ENTER){
                json.put("key", "ENTER");
            }else if(code == KeyCode.DELETE){
                json.put("key", "DELETE");
            }

            _eventHandler.publishEvent(5, "KeyTyped", json);
            /*if (isSelected) {
                JSONObject json = new JSONObject();
                json.put("id", ID);
                _eventHandler.publishEvent("OPEN_PROCESSOR", json);
            }*/
        });

        FXMLDocumentController controller = loader.getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("dark-theme.css")));
        //TODO change back to FullScreen
        //primaryStage.setFullScreen(true);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);

        primaryStage.showingProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    controller.sPane.setDividerPositions(0.1f, 0.9f);
                    observable.removeListener(this);
                }
            }

        });
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();



    }

    @Override
    public void processGUIEvent(String event, JSONObject args) {

    }

    @Override
    public void close() throws Exception {

    }
}

package msys.client.stages;

import javafx.stage.Modality;
import javafx.stage.Stage;

public class Login extends Stage {
    public Login(){
        //Creating another stage other than primary default stage
        setTitle("Login");
        //Set the width and height of the other stage window
        setWidth(400);
        setHeight(400);
        initModality(Modality.APPLICATION_MODAL);
    }
}

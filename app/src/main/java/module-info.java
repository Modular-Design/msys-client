module msys_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.commons.codec;
    //requires javax.websocket.client.api;

    opens msys.client to javafx.fxml;
    exports msys.client;
}
module msys.client {
    requires javafx.controls;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.commons.codec;
    requires  Java.WebSocket;
    requires org.apache.commons.configuration2;

    exports msys.client;
    exports msys.client.communication;
}
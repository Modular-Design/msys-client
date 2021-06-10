module msys.client {
    requires javafx.controls;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.commons.codec;
    // requires org.kordamp.iconli.core;
    // requires org.kordamp.ikonli.javafx;
    // requires org.kordamp.ikonli.fontawesome;
    requires  Java.WebSocket;

    exports msys.client;
}
module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires org.slf4j;
    requires java.sql;
    requires java.sql.rowset;

    opens com.example.client to javafx.fxml;
    exports com.example.client;
    exports com.example.client.client1;
    opens com.example.client.client1 to javafx.fxml;
    exports com.example.client.client2;
    opens com.example.client.client2 to javafx.fxml;
    exports org.example;
    opens org.example to javafx.fxml;

}
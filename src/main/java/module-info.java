module org.example.foodrecipeplatform {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    requires firebase.admin;
    requires com.google.auth;
    requires com.google.auth.oauth2;
    requires google.cloud.firestore;
    requires google.cloud.core;
    requires com.google.api.apicommon;
    requires org.json;
    requires static java.sql;


    opens org.example.foodrecipeplatform to javafx.fxml;
    exports org.example.foodrecipeplatform;
}
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
    requires org.checkerframework.checker.qual;
    requires java.desktop;
    requires json.simple;

    opens org.example.foodrecipeplatform to javafx.fxml;
    exports org.example.foodrecipeplatform;
    exports org.example.foodrecipeplatform.Controller;
    opens org.example.foodrecipeplatform.Controller to javafx.fxml;
}
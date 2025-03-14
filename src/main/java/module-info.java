module org.example.foodrecipeplatform {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.foodrecipeplatform to javafx.fxml;
    exports org.example.foodrecipeplatform;
}
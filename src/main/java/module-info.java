module org.example.notes {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jdk.compiler;

    opens org.example.notes to javafx.fxml;
    exports org.example.notes;
    exports org.example.notes.Controllers;
    opens org.example.notes.Controllers to javafx.fxml;
}
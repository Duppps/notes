module org.example.notes {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.notes to javafx.fxml;
    exports org.example.notes;
}
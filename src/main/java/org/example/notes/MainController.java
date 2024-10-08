package org.example.notes;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.File;

public class MainController {

    @FXML
    protected TextField showPath;

    @FXML
    protected VBox mainNode;

    public void setDirectory(File directory) {
        DirectoryReader directoryReader = new DirectoryReader(directory);

        showPath.setText(directoryReader.getPath());
    }


}

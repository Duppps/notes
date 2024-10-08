package org.example.notes.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class SelectWorkspaceController {

    @FXML
    protected VBox mainNode;

    @FXML
    protected void onSelectDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Escolher Diretório");
        File selectedDirectory = directoryChooser.showDialog(getCurrentStage());

        if (selectedDirectory != null) {
            loadMainScreen(selectedDirectory);
        }
    }

    private Stage getCurrentStage() {
        return (Stage) mainNode.getScene().getWindow();
    }

    private void loadMainScreen(File selectedDirectory) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/notes/main-view.fxml"));
            Parent newRoot = loader.load();

            MainController mainController = loader.getController();
            mainController.setDirectory(selectedDirectory);

            Stage currentStage = getCurrentStage();
            currentStage.setScene(new Scene(newRoot));
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
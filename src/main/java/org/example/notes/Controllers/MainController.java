package org.example.notes.Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.example.notes.Note;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private TreeView<Note> directoryTree;

    @FXML
    private Label labelTitle;

    @FXML
    private Label labelDesc;

    @FXML
    private TextField textFieldTitle;

    @FXML
    private TextArea textAreaDesc;

    @FXML
    private Button buttonSave;

    private File workspace;

    public void setDirectory(File directory) {
        this.workspace = directory;
        TreeItem<Note> rootItem = new TreeItem<>(new Note(directory.getName(), "", directory));
        directoryTree.setRoot(rootItem);
        buildDirectoryTree(rootItem);
    }

    private void buildDirectoryTree(TreeItem<Note> rootItem) {
        File rootDir = rootItem.getValue().getFile();
        File[] directories = rootDir.listFiles(File::isDirectory);
        File[] notes = rootDir.listFiles(File::isFile);

        if (notes != null) {
            for (File file : notes) {
                if (file.getName().endsWith(".txt")) {
                    Note note = new Note(file.getName(), "", file);
                    TreeItem<Note> noteItem = new TreeItem<>(note);
                    rootItem.getChildren().add(noteItem);
                }
            }
        }

        if (directories != null) {
            for (File directory : directories) {
                TreeItem<Note> directoryItem = new TreeItem<>(new Note(directory.getName(), "", directory));
                rootItem.getChildren().add(directoryItem);
                buildDirectoryTree(directoryItem);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelTitle.setVisible(false);
        labelDesc.setVisible(false);
        textFieldTitle.setVisible(false);
        textAreaDesc.setVisible(false);
        buttonSave.setVisible(false);

        labelTitle.getStyleClass().add("form-label");
        labelDesc.getStyleClass().add("form-label");
        textFieldTitle.getStyleClass().add("form-control");
        textAreaDesc.getStyleClass().add("form-control");
        buttonSave.getStyleClass().add("button-primary");

        directoryTree.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<Note> call(TreeView<Note> param) {
                return new TreeCell<>() {
                    @Override
                    protected void updateItem(Note item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getTitle());
                        }
                    }
                };
            }
        });

        directoryTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue().getFile().isFile()) {
                showForm(newValue.getValue());
            }
        });
    }

    public void onNewClick() {
        showForm(null);
        textFieldTitle.setText("");
        textAreaDesc.setText("");

        directoryTree.getSelectionModel().clearSelection();
    }

    public void showForm(Note note) {
        labelTitle.setVisible(true);
        labelDesc.setVisible(true);
        textFieldTitle.setVisible(true);
        textAreaDesc.setVisible(true);
        buttonSave.setVisible(true);

        if (note != null && note.getFile().exists()) {
            String[] titleAndDescription = readFile(note.getFile());
            textFieldTitle.setText(titleAndDescription[0]);
            textAreaDesc.setText(titleAndDescription[1]);

            buttonSave.setText("Atualizar nota");
            buttonSave.setOnAction(updateNote(note));
        } else {
            textFieldTitle.setText("");
            textAreaDesc.setText("");
            buttonSave.setText("Criar nota");
            buttonSave.setOnAction(createNewNote());
        }
    }

    public String[] readFile(File file) {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            properties.load(reader);
            String title = properties.getProperty("title", "");
            String description = properties.getProperty("description", "");
            return new String[]{title, description};
        } catch (IOException e) {
            e.printStackTrace();
            return new String[]{"", ""};
        }
    }

    public EventHandler<ActionEvent> updateNote(Note note) {
        return new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                Properties properties = new Properties();
                properties.setProperty("title", textFieldTitle.getText());
                properties.setProperty("description", textAreaDesc.getText());

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(note.getFile()))) {
                    properties.store(writer, null);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Nota Atualizada");
                    alert.setHeaderText(null);
                    alert.setContentText("Nota salva com sucesso!");
                    alert.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro ao Salvar");
                    alert.setHeaderText(null);
                    alert.setContentText("Não foi possível salvar a nota.");
                    alert.showAndWait();
                }
            }
        };
    }

    public EventHandler<ActionEvent> createNewNote() {
        return new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                String title = textFieldTitle.getText();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Salvar Nota");

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Arquivos de Texto (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                fileChooser.setInitialDirectory(workspace);

                String recommendedFileName = title != null && !title.isEmpty() ? title + ".txt" : "nota.txt";
                fileChooser.setInitialFileName(recommendedFileName);

                File file = fileChooser.showSaveDialog(null);

                if (file != null) {
                    Note note = new Note(title, textAreaDesc.getText(), file);
                    try {
                        note.save();

                        updateDirectoryTree(workspace);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Nota Salva");
                        alert.setHeaderText(null);
                        alert.setContentText("Nota salva com sucesso!");
                        alert.showAndWait();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro ao Salvar");
                        alert.setHeaderText(null);
                        alert.setContentText("Não foi possível salvar a nota.");
                        alert.showAndWait();
                    }
                }
            }
        };
    }

    private void updateDirectoryTree(File directory) {
        TreeItem<Note> rootItem = new TreeItem<>(new Note(directory.getName(), "", directory));
        directoryTree.setRoot(rootItem);
        buildDirectoryTree(rootItem);
    }

    public void deleteItem() {
        TreeItem<Note> selectedItem = directoryTree.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            Note selectedNote = selectedItem.getValue();
            File fileToDelete = selectedNote.getFile();

            if (!fileToDelete.getAbsolutePath().equals(workspace.getAbsolutePath())) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmar Exclusão");

                if (fileToDelete.isDirectory()) {
                    alert.setHeaderText("Você realmente deseja excluir esta pasta?");
                    alert.setContentText("A pasta \"" + fileToDelete.getName() + "\" será excluída.");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        deleteDirectory(fileToDelete);
                        selectedItem.getParent().getChildren().remove(selectedItem);
                    }
                } else {
                    alert.setHeaderText("Você realmente deseja excluir esta nota?");
                    alert.setContentText("A nota \"" + fileToDelete.getName() + "\" será excluída.");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        fileToDelete.delete();
                        selectedItem.getParent().getChildren().remove(selectedItem);
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Impossível Excluir");
                alert.setHeaderText("Impossível excluir a pasta raiz do workspace.");
                alert.showAndWait();
            }
        }
    }


    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}

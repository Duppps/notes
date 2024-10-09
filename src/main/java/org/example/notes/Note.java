package org.example.notes;

import javafx.scene.control.Alert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Note {
    private String title;
    private String description;
    private File file;

    public Note(String title, String description, File file) {
        this.title = title;
        this.description = description;
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public File getFile() {
        return file;
    }

    public void save() throws IOException {
        if (file != null) {
            Properties properties = new Properties();
            properties.setProperty("title", this.title);
            properties.setProperty("description", this.description);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                properties.store(writer, null);
            }
        }
    }

    public void update() {

    }
}

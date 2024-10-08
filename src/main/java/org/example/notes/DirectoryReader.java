package org.example.notes;

import java.io.File;
import java.util.ArrayList;

public class DirectoryReader {
    private File path;

    public DirectoryReader(File path) {
        this.path = path;
    }

    public String getPath() {
        return this.path.getAbsolutePath();
    }

    public ArrayList<File> readDirectory() {
        ArrayList<File> fileList = new ArrayList<>();

        if (path.isDirectory()) {
            File[] files = path.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        fileList.add(file);
                    }
                    else if (file.isFile() && file.getName().endsWith(".txt")) {
                        fileList.add(file);
                    }
                }
            } else {
                System.out.println("Diretório vazio ou inacessível.");
            }
        } else {
            System.out.println("O caminho especificado não é um diretório.");
        }

        return fileList;
    }
}

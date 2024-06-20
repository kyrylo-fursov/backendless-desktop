package backendless;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileOperationsPane extends GridPane {

    private BackendlessUser loggedInUser;
    private String userDirectory = "";

    public FileOperationsPane() {
        setHgap(10);
        setVgap(10);

        Label loggedInLabel = new Label("Logged in as: Not logged in");
        add(loggedInLabel, 0, 0, 2, 1);

        Label folderNameLabel = new Label("Folder Name:");
        add(folderNameLabel, 0, 1);
        TextField folderNameField = new TextField();
        add(folderNameField, 1, 1);

        Button createFolderButton = new Button("Create Folder");
        add(createFolderButton, 1, 2);
        createFolderButton.setOnAction(e -> {
            String folderName = folderNameField.getText();
            if (folderName.isEmpty()) {
                showAlert("Folder name cannot be empty");
                return;
            }
            FileOperations.createFolder(userDirectory + "/" + folderName);
        });

        Label fileNameLabel = new Label("File Name:");
        add(fileNameLabel, 0, 3);
        TextField fileNameField = new TextField();
        add(fileNameField, 1, 3);

        Button deleteFileButton = new Button("Delete File");
        add(deleteFileButton, 1, 4);
        deleteFileButton.setOnAction(e -> {
            String fileName = fileNameField.getText();
            if (fileName.isEmpty()) {
                showAlert("File name cannot be empty");
                return;
            }
            FileOperations.deleteFile(userDirectory + "/" + fileName);
        });

        Button listFilesButton = new Button("List Files");
        add(listFilesButton, 1, 5);
        listFilesButton.setOnAction(e -> FileOperations.listFiles(userDirectory));

        Button uploadFileButton = new Button("Upload File");
        add(uploadFileButton, 1, 6);
        uploadFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                FileOperations.uploadFile(userDirectory, file);
            }
        });

        Button downloadFileButton = new Button("Download File");
        add(downloadFileButton, 1, 7);
        downloadFileButton.setOnAction(e -> {
            String fileName = fileNameField.getText();
            if (fileName.isEmpty()) {
                showAlert("File name cannot be empty");
                return;
            }
            FileOperations.downloadFile(fileName);
        });
    }

    public void setLoggedInUser(BackendlessUser user) {
        this.loggedInUser = user;
        if (user != null) {
            this.userDirectory = "/user_" + user.getEmail();
            Platform.runLater(() -> {
                updateUIForLoggedInUser();
            });
        }
    }

    private void updateUIForLoggedInUser() {
        Label loggedInLabel = new Label("Logged in as: " + (loggedInUser != null ? loggedInUser.getEmail() : "Not logged in"));
        add(loggedInLabel, 0, 0, 2, 1);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}




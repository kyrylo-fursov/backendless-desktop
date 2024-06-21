package backendless.plane;

import backendless.service.FileOperations;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.backendless.BackendlessUser;

import java.io.File;

public class FileOperationsPane extends GridPane {

    private BackendlessUser loggedInUser;
    private String userDirectory = "";

    private Label loggedInLabel = new Label("Logged in as: Not logged in");
    private TextField fileNameField = new TextField();
    private TextArea filesListArea = new TextArea();

    public FileOperationsPane() {
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(10, 10, 10, 10));

        loggedInLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        add(loggedInLabel, 0, 0, 2, 1);

        Label fileNameLabel = new Label("File Name:");
        add(fileNameLabel, 0, 1);
        fileNameField.setPromptText("Enter file name");
        add(fileNameField, 1, 1);

        Button deleteFileButton = new Button("Delete File");
        deleteFileButton.setOnAction(e -> deleteFile());
        HBox fileBox = new HBox(10, fileNameField, deleteFileButton);
        add(fileBox, 1, 2);

        Button listFilesButton = new Button("List Files");
        listFilesButton.setOnAction(e -> listFiles());
        add(listFilesButton, 1, 3);

        filesListArea.setEditable(false);
        filesListArea.setPromptText("Files will be listed here...");
        add(filesListArea, 0, 4, 2, 1);

        Button uploadFileButton = new Button("Upload File");
        uploadFileButton.setOnAction(e -> uploadFile());
        add(uploadFileButton, 1, 5);

        Button downloadFileButton = new Button("Download File");
        downloadFileButton.setOnAction(e -> downloadFile());
        add(downloadFileButton, 1, 6);
    }

    private void deleteFile() {
        String fileName = fileNameField.getText();
        if (fileName.isEmpty()) {
            showAlert("File name cannot be empty");
            return;
        }
        FileOperations.deleteFile(userDirectory + "/" + fileName);
    }

    private void listFiles() {
        FileOperations.listFiles(userDirectory, files -> {
            StringBuilder fileList = new StringBuilder("Files:\n");
            for (String file : files) {
                fileList.append(file).append("\n");
            }
            Platform.runLater(() -> filesListArea.setText(fileList.toString()));
        });
    }

    private void uploadFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            FileOperations.uploadFile(userDirectory, file);
        }
    }

    private void downloadFile() {
        String fileName = fileNameField.getText();
        if (fileName.isEmpty()) {
            showAlert("File name cannot be empty");
            return;
        }
        FileOperations.downloadFile(userDirectory + "/" + fileName);
    }

    public void setLoggedInUser(BackendlessUser user) {
        this.loggedInUser = user;
        if (user != null) {
            this.userDirectory = "/user_" + user.getEmail();
            Platform.runLater(this::updateUIForLoggedInUser);
        }
    }

    private void updateUIForLoggedInUser() {
        loggedInLabel.setText("Logged in as: " + (loggedInUser != null ? loggedInUser.getEmail() : "Not logged in"));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
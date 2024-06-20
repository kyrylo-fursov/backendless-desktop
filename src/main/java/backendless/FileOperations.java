package backendless;

import com.backendless.Backendless;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.files.FileInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;


public class FileOperations {

    public static void createUserDirectory(String email) {
        String userDirectory = "/user_" + email;
        Backendless.Files.createDirectory(userDirectory, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                createSharedWithMeFolder(userDirectory);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error creating user directory: " + fault.getMessage()));
            }
        });
    }

    public static void createSharedWithMeFolder(String userDirectory) {
        String sharedWithMeDirectory = userDirectory + "/shared_with_me";
        Backendless.Files.createDirectory(sharedWithMeDirectory, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Platform.runLater(() -> showAlert("User and shared_with_me directories created successfully."));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error creating shared_with_me directory: " + fault.getMessage()));
            }
        });
    }

    public static void createFolder(String folderName) {
        Backendless.Files.createDirectory(folderName, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Platform.runLater(() -> showAlert("Folder created successfully."));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error creating folder: " + fault.getMessage()));
            }
        });
    }

    public static void deleteFile(String fileName) {
        Backendless.Files.remove(fileName, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer response) {
                Platform.runLater(() -> showAlert("File deleted successfully."));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error deleting file: " + fault.getMessage()));
            }
        });
    }

    public static void listFiles(String directory) {
        Backendless.Files.listing(directory, "*", false, new AsyncCallback<List<FileInfo>>() {
            @Override
            public void handleResponse(List<FileInfo> response) {
                StringBuilder fileList = new StringBuilder("Files:\n");
                for (FileInfo file : response) {
                    fileList.append(file.getName()).append("\n");
                }
                Platform.runLater(() -> showAlert(fileList.toString()));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error listing files: " + fault.getMessage()));
            }
        });
    }

    public static void uploadFile(String directory, File file) {
        Backendless.Files.upload(file, directory, new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(BackendlessFile backendlessFile) {
                Platform.runLater(() -> showAlert("File uploaded successfully."));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error uploading file: " + fault.getMessage()));
            }
        });
    }

    public static void downloadFile(String filePath) {
        try {
            String fileURL = "https://eu-api.backendless.com/" + "YOUR-APPLICATION-ID" + "/" + "YOUR-API-KEY" + "/files" + filePath;
            URL website = new URL(fileURL);
            HttpURLConnection connection = (HttpURLConnection) website.openConnection();
            InputStream inputStream = connection.getInputStream();
            ReadableByteChannel rbc = Channels.newChannel(inputStream);
            File file = new File("downloaded_" + new File(filePath).getName());
            FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            Platform.runLater(() -> showAlert("File downloaded successfully to: " + file.getAbsolutePath()));
        } catch (IOException e) {
            Platform.runLater(() -> showAlert("Error downloading file: " + e.getMessage()));
        }
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
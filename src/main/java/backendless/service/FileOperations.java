package backendless.service;

import com.backendless.Backendless;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.files.FileInfo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static backendless.MainApp.APP_ID;
import static backendless.MainApp.API_KEY;

public class FileOperations {
    private static final String SERVER_URL = "https://lovelycreator-eu.backendless.app";

    public static void createUserDirectory(String email, String userId) {
        String userDirectory = "/user_" + email;
        Backendless.Files.createDirectory(userDirectory, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                try {
                    setUserDirectoryPermissions(userDirectory, userId);
                    Platform.runLater(() -> showAlert("User directory created successfully."));
                } catch (IOException e) {
                    Platform.runLater(() -> showAlert("Error setting permissions: " + e.getMessage()));
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error creating user directory: " + fault.getMessage()));
            }
        });
    }

    private static void setUserDirectoryPermissions(String directoryPath, String userId) throws IOException {
        String[] permissions = {"READ", "WRITE", "DELETE"};
        for (String permission : permissions) {
            setPermission(directoryPath, userId, permission);
        }
    }

    public static void uploadFile(String directory, File file, AsyncCallback<BackendlessFile> callback) {
        Backendless.Files.upload(file, directory, callback);
    }

    private static void setPermission(String directoryPath, String userId, String permission) throws IOException {
        String url = SERVER_URL + "/api/files/permissions/grant/" + directoryPath;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("application-id", APP_ID);
        con.setRequestProperty("secret-key", API_KEY);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        String jsonInputString = "{\"permission\": \"" + permission + "\", \"user\": \"" + userId + "\"}";
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            throw new IOException("Failed to set permission: " + response.toString());
        }
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

    public static void listFiles(String directory, FileListCallback callback) {
        Backendless.Files.listing(directory, "*", false, new AsyncCallback<List<FileInfo>>() {
            @Override
            public void handleResponse(List<FileInfo> response) {
                List<String> fileList = new ArrayList<>();
                for (FileInfo file : response) {
                    fileList.add(file.getName());
                }
                callback.onFilesListed(fileList);
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
            String fileURL = SERVER_URL + "/api/files" + filePath;
            URL website = new URL(fileURL);
            HttpURLConnection connection = (HttpURLConnection) website.openConnection();
            connection.setRequestMethod("GET");
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

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public interface FileListCallback {
        void onFilesListed(List<String> files);
    }
}
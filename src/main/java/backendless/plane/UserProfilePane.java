package backendless.plane;
import backendless.FileOperations;
import backendless.LocationUpdater;
import com.backendless.Backendless;
import com.backendless.files.BackendlessFile;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.BackendlessUser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import static backendless.MainApp.SERVER_URL;


public class UserProfilePane extends GridPane {

    private Label loggedInLabel = new Label("Logged in as: Not logged in");
    private TextField emailField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private TextField nameField = new TextField();
    private TextField ageField = new TextField();
    private Label avatarLabel = new Label();
    private Button uploadAvatarButton = new Button("Upload Avatar");
    private Button saveNameButton = new Button("Save Name");
    private Button saveAgeButton = new Button("Save Age");
    private Button savePasswordButton = new Button("Save Password");

    private static final String SERVER_URL = "https://eu-api.backendless.com"; // replace with your server URL

    public UserProfilePane() {
        setHgap(10);
        setVgap(10);

        add(loggedInLabel, 0, 0, 2, 1);

        Label emailLabel = new Label("Email:");
        add(emailLabel, 0, 1);
        emailField.setDisable(true); // Disable email field as it's not editable
        add(emailField, 1, 1);

        Label passwordLabel = new Label("Password:");
        add(passwordLabel, 0, 2);
        add(passwordField, 1, 2);
        add(savePasswordButton, 2, 2);

        Label nameLabel = new Label("Name:");
        add(nameLabel, 0, 3);
        add(nameField, 1, 3);
        add(saveNameButton, 2, 3);

        Label ageLabel = new Label("Age:");
        add(ageLabel, 0, 4);
        add(ageField, 1, 4);
        add(saveAgeButton, 2, 4);

        Label avatar = new Label("Avatar:");
        add(avatar, 0, 5);
        add(avatarLabel, 1, 5);
        add(uploadAvatarButton, 1, 6);

        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null) {
            updateUIForLoggedInUser(user);
        }

        savePasswordButton.setOnAction(e -> savePassword());
        saveNameButton.setOnAction(e -> saveName());
        saveAgeButton.setOnAction(e -> saveAge());
        uploadAvatarButton.setOnAction(e -> uploadAvatar());
    }

    private void updateUIForLoggedInUser(BackendlessUser user) {
        loggedInLabel.setText("Logged in as: " + user.getEmail());
        emailField.setText(user.getEmail());
        nameField.setText((String) user.getProperty("name"));
        ageField.setText(String.valueOf(user.getProperty("age")));
        if (user.getProperty("avatar") != null) {
            avatarLabel.setText(user.getProperty("avatar").toString());
        }
    }

    private void savePassword() {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null && !passwordField.getText().isEmpty()) {
            user.setPassword(passwordField.getText());
            Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser updatedUser) {
                    Platform.runLater(() -> showAlert("Password updated successfully."));
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Platform.runLater(() -> showAlert("Error updating password: " + fault.getMessage()));
                }
            });
        } else {
            showAlert("Password cannot be empty.");
        }
    }

    private void saveName() {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null && !nameField.getText().isEmpty()) {
            user.setProperty("name", nameField.getText());
            Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser updatedUser) {
                    Platform.runLater(() -> showAlert("Name updated successfully."));
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Platform.runLater(() -> showAlert("Error updating name: " + fault.getMessage()));
                }
            });
        } else {
            showAlert("Name cannot be empty.");
        }
    }

    private void saveAge() {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null) {
            String ageText = ageField.getText();
            if (!ageText.isEmpty() && isValidInteger(ageText)) {
                user.setProperty("age", Integer.parseInt(ageText));
                Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser updatedUser) {
                        Platform.runLater(() -> showAlert("Age updated successfully."));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Platform.runLater(() -> showAlert("Error updating age: " + fault.getMessage()));
                    }
                });
            } else {
                showAlert("Invalid age value.");
            }
        }
    }

    private void uploadAvatar() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            BackendlessUser user = Backendless.UserService.CurrentUser();
            if (user != null) {
                String userId = user.getObjectId();
                String email = user.getEmail();
                String userDirectory = "/user_" + email;
                String destinationPath = userDirectory + "/avatar.jpg";
                FileOperations.uploadFile(userDirectory, file, new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse(BackendlessFile backendlessFile) {
                        String avatarUrl = SERVER_URL + destinationPath;
                        user.setProperty("avatar", avatarUrl);
                        Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser updatedUser) {
                                Platform.runLater(() -> {
                                    avatarLabel.setText(avatarUrl);
                                    showAlert("Profile picture updated successfully.");
                                });
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Platform.runLater(() -> showAlert("Error updating profile: " + fault.getMessage()));
                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Platform.runLater(() -> showAlert("Error uploading avatar: " + fault.getMessage()));
                    }
                });
            }
        }
    }

    private boolean isValidInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
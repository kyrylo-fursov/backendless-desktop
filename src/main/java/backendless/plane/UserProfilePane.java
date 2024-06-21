package backendless.plane;
import backendless.FileOperations;
import com.backendless.Backendless;
import com.backendless.files.BackendlessFile;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.BackendlessUser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;


public class UserProfilePane extends GridPane {

    private Label loggedInLabel = new Label("Logged in as: Not logged in");
    private PasswordField passwordField = new PasswordField();
    private TextField nameField = new TextField();
    private TextField ageField = new TextField();
    private ImageView avatarImageView = new ImageView();
    private Button uploadAvatarButton = new Button("Upload Avatar");
    private Button saveNameButton = new Button("Save Name");
    private Button saveAgeButton = new Button("Save Age");
    private Button savePasswordButton = new Button("Save Password");

    private static final String SERVER_URL = "https://lovelycreator-eu.backendless.app"; // replace with your server URL

    public UserProfilePane() {
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(10, 10, 10, 10));

        add(loggedInLabel, 0, 0, 2, 1);

        Label passwordLabel = new Label("Password:");
        add(passwordLabel, 0, 1);
        add(passwordField, 1, 1);
        add(savePasswordButton, 2, 1);

        Label nameLabel = new Label("Name:");
        add(nameLabel, 0, 2);
        add(nameField, 1, 2);
        add(saveNameButton, 2, 2);

        Label ageLabel = new Label("Age:");
        add(ageLabel, 0, 3);
        add(ageField, 1, 3);
        add(saveAgeButton, 2, 3);

        Label avatarLabel = new Label("Avatar:");
        add(avatarLabel, 0, 4);
        avatarImageView.setFitWidth(100);
        avatarImageView.setFitHeight(100);
        avatarImageView.setPreserveRatio(true);
        add(avatarImageView, 1, 4);
        add(uploadAvatarButton, 1, 5);

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
        nameField.setText((String) user.getProperty("name"));
        ageField.setText(String.valueOf(user.getProperty("age")));
        if (user.getProperty("avatar") != null) {
            String avatarUrl = user.getProperty("avatar").toString();
            avatarImageView.setImage(new Image(avatarUrl));
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
                String userDirectory = "/user_" + user.getEmail();
                FileOperations.uploadFile(userDirectory, file, new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse(BackendlessFile backendlessFile) {
                        String avatarUrl = backendlessFile.getFileURL();
                        user.setProperty("avatar", avatarUrl);
                        Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser updatedUser) {
                                Platform.runLater(() -> {
                                    avatarImageView.setImage(new Image(avatarUrl));
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
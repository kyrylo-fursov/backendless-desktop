package backendless;
import com.backendless.Backendless;
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

    private TextField emailField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private TextField nameField = new TextField();
    private TextField ageField = new TextField();
    private CheckBox trackLocationCheckBox = new CheckBox("Track my location");
    private Label avatarLabel = new Label();
    private Button uploadAvatarButton = new Button("Upload Avatar");
    private Button saveButton = new Button("Save");

    public UserProfilePane() {
        setHgap(10);
        setVgap(10);

        Label emailLabel = new Label("Email:");
        add(emailLabel, 0, 0);
        emailField.setDisable(true); // Disable email field as it's not editable
        add(emailField, 1, 0);

        Label passwordLabel = new Label("Password:");
        add(passwordLabel, 0, 1);
        add(passwordField, 1, 1);

        Label nameLabel = new Label("Name:");
        add(nameLabel, 0, 2);
        add(nameField, 1, 2);

        Label ageLabel = new Label("Age:");
        add(ageLabel, 0, 3);
        add(ageField, 1, 3);

        add(trackLocationCheckBox, 1, 4);

        Label avatar = new Label("Avatar:");
        add(avatar, 0, 5);
        add(avatarLabel, 1, 5);
        add(uploadAvatarButton, 1, 6);

        add(saveButton, 1, 7);

        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null) {
            emailField.setText(user.getEmail());
            nameField.setText((String) user.getProperty("name"));
            ageField.setText(String.valueOf(user.getProperty("age")));
            trackLocationCheckBox.setSelected((Boolean) user.getProperty("trackLocation"));

            if (user.getProperty("avatar") != null) {
                avatarLabel.setText(user.getProperty("avatar").toString());
            }
        }

        saveButton.setOnAction(e -> saveUserProfile());
        uploadAvatarButton.setOnAction(e -> uploadAvatar());
    }

    private void saveUserProfile() {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null) {
            if (!passwordField.getText().isEmpty()) {
                user.setPassword(passwordField.getText());
            }
            user.setProperty("name", nameField.getText());
            user.setProperty("age", Integer.parseInt(ageField.getText()));
            user.setProperty("trackLocation", trackLocationCheckBox.isSelected());

            Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser updatedUser) {
                    Platform.runLater(() -> showAlert("Profile updated successfully."));
                    if (trackLocationCheckBox.isSelected()) {
                        startLocationTracking(updatedUser);
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Platform.runLater(() -> showAlert("Error updating profile: " + fault.getMessage()));
                }
            });
        }
    }

    private void uploadAvatar() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            updateProfilePicture(Backendless.UserService.CurrentUser().getUserId(), file.getAbsolutePath());
        }
    }

    private void updateProfilePicture(String userId, String filePath) {
        String destinationPath = "/avatars/" + userId + ".jpg";
        Backendless.Files.copyFile(filePath, destinationPath, new AsyncCallback<String>() {
            @Override
            public void handleResponse(String response) {
                // Update the user profile with the avatar URL
                String avatarUrl = SERVER_URL + destinationPath;
                BackendlessUser user = Backendless.UserService.CurrentUser();
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
                Platform.runLater(() -> showAlert("Error copying file: " + fault.getMessage()));
            }
        });
    }

    private void startLocationTracking(BackendlessUser user) {
        LocationUpdater locationUpdater = new LocationUpdater(user);
        locationUpdater.start();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
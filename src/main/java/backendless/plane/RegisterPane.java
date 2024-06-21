package backendless.plane;

import backendless.service.FileOperations;
import com.backendless.BackendlessUser;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;


public class RegisterPane extends GridPane {

    public RegisterPane() {
        setHgap(10);
        setVgap(10);

        Label emailLabel = new Label("Email:");
        add(emailLabel, 0, 0);
        TextField emailField = new TextField();
        add(emailField, 1, 0);

        Label passwordLabel = new Label("Password:");
        add(passwordLabel, 0, 1);
        PasswordField passwordField = new PasswordField();
        add(passwordField, 1, 1);

        Label nameLabel = new Label("Name:");
        add(nameLabel, 0, 2);
        TextField nameField = new TextField();
        add(nameField, 1, 2);

        Label ageLabel = new Label("Age:");
        add(ageLabel, 0, 3);
        TextField ageField = new TextField();
        add(ageField, 1, 3);

        Label genderLabel = new Label("Gender:");
        add(genderLabel, 0, 4);
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        add(genderComboBox, 1, 4);

        Button registerButton = new Button("Register");
        add(registerButton, 1, 5);

        registerButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            String name = nameField.getText();
            String ageText = ageField.getText();
            String gender = genderComboBox.getValue();

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showAlert("Invalid email format");
                return;
            }

            if (ageText.isEmpty() || !ageText.matches("\\d+")) {
                showAlert("Age must be a number");
                return;
            }

            int age = Integer.parseInt(ageText);
            if (age < 5) {
                showAlert("Age must be 5 or older");
                return;
            }

            BackendlessUser user = new BackendlessUser();
            user.setProperty("email", email);
            user.setPassword(password);
            user.setProperty("name", name);
            user.setProperty("age", age);
            user.setProperty("gender", gender);

            Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser registeredUser) {
                    Platform.runLater(() -> {
                        showAlert("User registered: " + registeredUser.getEmail());
                        FileOperations.createUserDirectory(email, registeredUser.getObjectId());
                    });
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Platform.runLater(() -> showAlert("Error: " + fault.getMessage()));
                }
            });
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
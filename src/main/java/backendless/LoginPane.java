package backendless;

import com.backendless.Backendless;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.BackendlessUser;

public class LoginPane extends GridPane {

    private static BackendlessUser loggedInUser;
    private final FileOperationsPane fileOperationsPane;

    public LoginPane(FileOperationsPane fileOperationsPane) {
        this.fileOperationsPane = fileOperationsPane;
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

        Button loginButton = new Button("Login");
        add(loginButton, 1, 2);

        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser user) {
                    loggedInUser = user;
                    Platform.runLater(() -> {
                        showAlert("User logged in: " + user.getEmail());
                        fileOperationsPane.setLoggedInUser(user);
                    });
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Platform.runLater(() -> showAlert("Error: " + fault.getMessage()));
                }
            });
        });
    }

    public static BackendlessUser getLoggedInUser() {
        return loggedInUser;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
package backendless;

import com.backendless.Backendless;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class ResetPasswordPane extends GridPane {

    public ResetPasswordPane() {
        setHgap(10);
        setVgap(10);

        Label emailLabel = new Label("Email:");
        add(emailLabel, 0, 0);
        TextField emailField = new TextField();
        add(emailField, 1, 0);

        Button resetPasswordButton = new Button("Reset Password");
        add(resetPasswordButton, 1, 1);

        resetPasswordButton.setOnAction(e -> {
            String email = emailField.getText();

            Backendless.UserService.restorePassword(email, new AsyncCallback<Void>() {
                @Override
                public void handleResponse(Void response) {
                    Platform.runLater(() -> showAlert("Password reset email sent."));
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
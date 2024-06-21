package backendless;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.messaging.BodyParts;
import com.backendless.messaging.EmailEnvelope;
import com.backendless.messaging.MessageStatus;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import com.backendless.exceptions.BackendlessFault;

import java.net.URL;


public class FeedbackPane extends GridPane {

    private TextArea messageField = new TextArea();
    private ComboBox<String> typeComboBox = new ComboBox<>();
    private Button sendButton = new Button("Send Feedback");

    public FeedbackPane() {
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(10, 10, 10, 10));

        Label typeLabel = new Label("Type:");
        add(typeLabel, 0, 0);
        typeComboBox.getItems().addAll("Error", "Suggestion");
        typeComboBox.getSelectionModel().selectFirst();
        add(typeComboBox, 1, 0);

        Label messageLabel = new Label("Message:");
        add(messageLabel, 0, 1);
        messageField.setPromptText("Enter your message here...");
        messageField.setPrefHeight(200);
        add(messageField, 1, 1);

        HBox buttonBox = new HBox(10, sendButton);
        add(buttonBox, 1, 2);

        sendButton.setOnAction(e -> sendFeedback());
    }

    private void sendFeedback() {
        String type = typeComboBox.getSelectionModel().getSelectedItem();
        String message = messageField.getText();

        if (message.isEmpty()) {
            showAlert("Message cannot be empty.");
            return;
        }

        String subject = "Feedback: " + type;
        sendEmail(subject, message);
    }

    private void sendEmail(String subject, String message) {
        String recipient = "kirfurs31@gmail.com"; 
        BodyParts bodyParts = new BodyParts();
        bodyParts.setTextMessage(message);

        Backendless.Messaging.sendHTMLEmail(subject, String.valueOf(bodyParts), recipient, new AsyncCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus response) {
                Platform.runLater(() -> showAlert("Feedback sent successfully."));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error sending feedback: " + fault.getMessage()));
            }
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

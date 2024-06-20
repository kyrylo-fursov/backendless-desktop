package backendless;

import com.backendless.BackendlessUser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.HashMap;
import java.util.Map;

public class MainApp extends Application {

    private static final String APP_ID = "A1520ABC-8D52-4A40-B9A8-945270F0F8C2";
    private static final String API_KEY = "07BEA6C1-A0BB-46E7-ABD4-7FD45756E1DD";
    private static final String SERVER_URL = "https://eu-api.backendless.com";

    public static void main(String[] args) {
        Backendless.setUrl(SERVER_URL);
        Backendless.initApp(APP_ID, API_KEY);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Backendless Test");

        Label label = new Label("Saving object to Backendless...");
        VBox root = new VBox();
        root.getChildren().add(label);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        HashMap<String, Object> testObject = new HashMap<>();
        testObject.put("foo", "bar");

        Backendless.Data.of("TestTable").save(testObject, new AsyncCallback<Map>() {
            @Override
            public void handleResponse(Map response) {
                Platform.runLater(() -> label.setText("Object is saved in Backendless. Please check in the console."));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                String faultDetails = "Error Code: " + fault.getCode() + "\n" +
                        "Message: " + fault.getMessage() + "\n" +
                        "Detail: " + fault.getDetail();
                System.err.println(faultDetails);
                Platform.runLater(() -> {
                    label.setText("Error: " + fault.getMessage());
                    showAlert("Error", faultDetails);
                });
            }
        });
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}

package backendless;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import com.backendless.Backendless;


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
        primaryStage.setTitle("User Management");

        TabPane tabPane = new TabPane();
        Tab registerTab = new Tab("Register");
        Tab loginTab = new Tab("Login");
        Tab fileOperationsTab = new Tab("File Operations");
        Tab resetPasswordTab = new Tab("Reset Password");

        FileOperationsPane fileOperationsPane = new FileOperationsPane();

        registerTab.setContent(new RegisterPane());
        loginTab.setContent(new LoginPane(fileOperationsPane));
        fileOperationsTab.setContent(fileOperationsPane);
        resetPasswordTab.setContent(new ResetPasswordPane());

        tabPane.getTabs().addAll(registerTab, loginTab, fileOperationsTab, resetPasswordTab);

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}